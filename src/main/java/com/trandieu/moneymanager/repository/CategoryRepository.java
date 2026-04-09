package com.trandieu.moneymanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trandieu.moneymanager.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

   // select * from categories where profile_id = ?
   List<CategoryEntity> findByProfileId(Long profileId);

   // select * from categories where id = ? and profile_id = ?
   Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

   // select * from categories where type = ? and profile_id = ?
   List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

   // select count(*) from categories where name = ? and profile_id = ?
   Boolean existsByNameAndProfileId(String name, Long profileId);
}
