package com.trandieu.moneymanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trandieu.moneymanager.entity.ProfileEntity;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
   // select * from profile_entity where email = ?
   Optional<ProfileEntity> findByEmail(String email);

   // select * from profile_entity where activation_token = ?
   Optional<ProfileEntity> findByActivationToken(String activationToken);
}
