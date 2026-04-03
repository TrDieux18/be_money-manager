package com.trandieu.moneymanager.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trandieu.moneymanager.dto.AuthDTO;
import com.trandieu.moneymanager.dto.ProfileDTO;
import com.trandieu.moneymanager.entity.ProfileEntity;
import com.trandieu.moneymanager.repository.ProfileRepository;
import com.trandieu.moneymanager.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
   private final ProfileRepository profileRepository;

   private final EmailService emailService;

   private final PasswordEncoder passwordEncoder;

   private final AuthenticationManager authenticationManager;

   private final JwtUtil jwtUtil;

   public ProfileDTO registerProfile(ProfileDTO profileDTO) {
      ProfileEntity profileEntity = toEntity(profileDTO);
      profileEntity.setEmail(profileEntity.getEmail().trim());
      profileEntity.setPassword(passwordEncoder.encode(profileEntity.getPassword()));

      if (profileRepository.findByEmail(profileEntity.getEmail()).isPresent()) {
         throw new RuntimeException("Email already exists");
      }

      profileEntity.setActivationToken(UUID.randomUUID().toString());
      profileEntity = profileRepository.save(profileEntity);
      // send activation email here using profileEntity.getEmail() and
      // profileEntity.getActivationToken()
      String activationLink = "http://localhost:8080/api/activate?token=" + profileEntity.getActivationToken();
      String subject = "Activate your account";
      String body = "Please click the following link to activate your account: " + activationLink;
      emailService.sendEmail(profileEntity.getEmail(), subject, body);

      return toDTO(profileEntity);

   }

   public ProfileEntity toEntity(ProfileDTO profileDTO) {
      return ProfileEntity.builder()
            .id(profileDTO.getId())
            .fullName(profileDTO.getFullName())
            .email(profileDTO.getEmail())
            .password(profileDTO.getPassword())
            .profileImageUrl(profileDTO.getProfileImageUrl())
            .createdAt(profileDTO.getCreatedAt())
            .updatedAt(profileDTO.getUpdatedAt())
            .build();
   }

   public ProfileDTO toDTO(ProfileEntity profileEntity) {
      return ProfileDTO.builder()
            .id(profileEntity.getId())
            .fullName(profileEntity.getFullName())
            .email(profileEntity.getEmail())
            .profileImageUrl(profileEntity.getProfileImageUrl())
            .createdAt(profileEntity.getCreatedAt())
            .updatedAt(profileEntity.getUpdatedAt())
            .build();
   }

   public boolean activateProfile(String activationToken) {
      return profileRepository.findByActivationToken(activationToken)
            .map(p -> {
               p.setIsActive(true);
               profileRepository.save(p);
               return true;
            }).orElse(false);
   }

   public boolean isAccountActive(String email) {
      return profileRepository.findByEmail(email)
            .map(ProfileEntity::getIsActive)
            .orElse(false);
   }

   public ProfileEntity getCurrentProfile() {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      return profileRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authentication.getName()));
   }

   public ProfileDTO getPublicProfile(String email) {
      ProfileEntity currentUser = null;
      if (email == null) {
         currentUser = getCurrentProfile();
      } else {
         currentUser = profileRepository.findByEmail(email)
               .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
      }
      return ProfileDTO.builder()
            .id(currentUser.getId())
            .fullName(currentUser.getFullName())
            .email(currentUser.getEmail())
            .profileImageUrl(currentUser.getProfileImageUrl())
            .createdAt(currentUser.getCreatedAt())
            .updatedAt(currentUser.getUpdatedAt())
            .build();
   }

   public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {

      try {
         authenticationManager
               .authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
         String token = jwtUtil.generateToken(authDTO.getEmail());
         return Map.of(
               "token", token,
               "user", getPublicProfile(authDTO.getEmail()));
      } catch (AuthenticationException e) {
         throw new RuntimeException("Invalid email or password");
      } catch (Exception e) {
         throw new RuntimeException("Login failed: " + e.getMessage());
      }
   }

}
