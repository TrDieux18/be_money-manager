package com.trandieu.moneymanager.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trandieu.moneymanager.dto.AuthDTO;
import com.trandieu.moneymanager.dto.ProfileDTO;
import com.trandieu.moneymanager.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfileController {

   private final ProfileService profileService;

   @PostMapping("/register")
   public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO) {
      ProfileDTO createdProfile = profileService.registerProfile(profileDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
   }

   @GetMapping("/activate")
   public ResponseEntity<String> activateProfile(@RequestParam("token") String token) {
      boolean isActivated = profileService.activateProfile(token);
      if (isActivated) {
         return ResponseEntity.ok("Profile activated successfully");
      } else {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already activated");
      }
   }

   @PostMapping("/login")
   public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
      try {
         if (!profileService.isAccountActive(authDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                  Map.of("message", "Account is not activated. Please check your email for the activation link."));
         }
         Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
         return ResponseEntity.ok(response);
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
      }
   }

}
