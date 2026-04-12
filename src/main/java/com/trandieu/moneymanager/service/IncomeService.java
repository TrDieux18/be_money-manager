package com.trandieu.moneymanager.service;

import org.springframework.stereotype.Service;

import com.trandieu.moneymanager.dto.IncomeDTO;
import com.trandieu.moneymanager.entity.CategoryEntity;
import com.trandieu.moneymanager.entity.IncomeEntity;
import com.trandieu.moneymanager.entity.ProfileEntity;
import com.trandieu.moneymanager.repository.CategoryRepository;
import com.trandieu.moneymanager.repository.IncomeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {

   private final ProfileService profileService;
   private final CategoryRepository categoryRepository;
   private final IncomeRepository incomeRepository;

   // add new income
   public IncomeDTO addIncome(IncomeDTO dto) {
      ProfileEntity profile = profileService.getCurrentProfile();
      CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
      IncomeEntity newIncome = toEntity(dto, profile, category);
      newIncome = incomeRepository.save(newIncome);
      return toDTO(newIncome);

   }

   // helper methods
   private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category) {
      return IncomeEntity.builder()
            .name(dto.getName())
            .icon(dto.getIcon())
            .amount(dto.getAmount())
            .date(dto.getDate())
            .profile(profile)
            .category(category)
            .build();
   }

   private IncomeDTO toDTO(IncomeEntity entity) {
      return IncomeDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .icon(entity.getIcon())
            .amount(entity.getAmount())
            .date(entity.getDate())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
            .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
            .build();
   }
}
