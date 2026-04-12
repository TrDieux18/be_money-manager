package com.trandieu.moneymanager.service;

import org.springframework.stereotype.Service;

import com.trandieu.moneymanager.dto.ExpenseDTO;
import com.trandieu.moneymanager.entity.CategoryEntity;
import com.trandieu.moneymanager.entity.ExpenseEntity;
import com.trandieu.moneymanager.entity.ProfileEntity;
import com.trandieu.moneymanager.repository.CategoryRepository;
import com.trandieu.moneymanager.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

   private final CategoryRepository categoryRepository;
   private final ExpenseRepository expenseRepository;
   private final ProfileService profileService;

   // add new expense
   public ExpenseDTO addExpense(ExpenseDTO dto) {
      ProfileEntity profile = profileService.getCurrentProfile();
      CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
      ExpenseEntity newExpense = toEntity(dto, profile, category);
      newExpense = expenseRepository.save(newExpense);
      return toDTO(newExpense);

   }

   // helper methods
   private ExpenseEntity toEntity(ExpenseDTO expenseDTO, ProfileEntity profile, CategoryEntity category) {
      return ExpenseEntity.builder()
            .name(expenseDTO.getName())
            .icon(expenseDTO.getIcon())
            .amount(expenseDTO.getAmount())
            .date(expenseDTO.getDate())
            .profile(profile)
            .category(category)
            .build();
   }

   private ExpenseDTO toDTO(ExpenseEntity entity) {
      return ExpenseDTO.builder()
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
