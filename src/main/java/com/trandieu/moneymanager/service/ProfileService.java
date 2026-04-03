package com.trandieu.moneymanager.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trandieu.moneymanager.dto.ProfileDTO;
import com.trandieu.moneymanager.entity.ProfileEntity;
import com.trandieu.moneymanager.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
   private final ProfileRepository profileRepository;

   private final EmailService emailService;

   private final PasswordEncoder passwordEncoder;

   public ProfileDTO registerProfile(ProfileDTO profileDTO) {
      ProfileEntity profileEntity = toEntity(profileDTO);
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
            .password(passwordEncoder.encode(profileDTO.getPassword()))
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
}
