package com.trandieu.moneymanager.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.trandieu.moneymanager.dto.CategoryDTO;
import com.trandieu.moneymanager.entity.CategoryEntity;
import com.trandieu.moneymanager.entity.ProfileEntity;
import com.trandieu.moneymanager.repository.CategoryRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
   private final ProfileService profileService;
   private final CategoryRepository categoryRepository;

   // save category
   public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
      ProfileEntity profile = profileService.getCurrentProfile();
      if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
         throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists");
      }
      CategoryEntity newCategory = toEntity(categoryDTO, profile);
      newCategory = categoryRepository.save(newCategory);
      return toDTO(newCategory);
   }

   // helper method
   private CategoryEntity toEntity(CategoryDTO dto, ProfileEntity profile) {
      return CategoryEntity.builder()
            .name(dto.getName())
            .type(dto.getType())
            .icon(dto.getIcon())
            .profile(profile)
            .build();
   }

   private CategoryDTO toDTO(CategoryEntity entity) {
      return CategoryDTO.builder()
            .id(entity.getId())
            .profileId(entity.getProfile() != null ? entity.getProfile().getId() : null)
            .name(entity.getName())
            .type(entity.getType())
            .icon(entity.getIcon())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
   }
}
