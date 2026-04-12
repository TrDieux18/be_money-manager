package com.trandieu.moneymanager.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.trandieu.moneymanager.dto.CategoryDTO;
import com.trandieu.moneymanager.entity.CategoryEntity;
import com.trandieu.moneymanager.entity.ProfileEntity;
import com.trandieu.moneymanager.repository.CategoryRepository;

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

   // get categories for current user
   public List<CategoryDTO> getCategoriesForCurrentUser() {
      ProfileEntity profile = profileService.getCurrentProfile();
      List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
      return categories.stream().map(this::toDTO).toList();
   }

   // get categories by type for current user
   public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
      ProfileEntity profile = profileService.getCurrentProfile();
      List<CategoryEntity> categories = categoryRepository.findByTypeAndProfileId(type, profile.getId());
      return categories.stream().map(this::toDTO).toList();
   }

   public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
      ProfileEntity profile = profileService.getCurrentProfile();
      CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
            .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found or not accessible"));
      existingCategory.setName(categoryDTO.getName());
      existingCategory.setType(categoryDTO.getType());
      existingCategory.setIcon(categoryDTO.getIcon());
      existingCategory = categoryRepository.save(existingCategory);
      return toDTO(existingCategory);
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
