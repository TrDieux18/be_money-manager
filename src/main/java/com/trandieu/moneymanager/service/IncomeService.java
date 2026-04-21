package com.trandieu.moneymanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.trandieu.moneymanager.dto.ExpenseDTO;
import com.trandieu.moneymanager.dto.IncomeDTO;
import com.trandieu.moneymanager.entity.CategoryEntity;
import com.trandieu.moneymanager.entity.ExpenseEntity;
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

   // Retrieves all incomes for current month/base on the start date and end date
   public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
      ProfileEntity profile = profileService.getCurrentProfile();
      LocalDate now = LocalDate.now();
      LocalDate startDate = now.withDayOfMonth(1);
      LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
      List<IncomeEntity> incomes = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate,
            endDate);

      return incomes.stream().map(this::toDTO).toList();

   }

   // delete income by id
   public void deleteIncomeById(Long id) {
      ProfileEntity profile = profileService.getCurrentProfile();
      IncomeEntity income = incomeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Income not found"));

      if (!income.getProfile().getId().equals(profile.getId())) {
         throw new RuntimeException("Unauthorized to delete this income");
      }
      incomeRepository.delete(income);

   }

   // get lastest 5 incomes for current user
   public List<IncomeDTO> getLastest5IncomesForCurrentUser() {
      ProfileEntity profile = profileService.getCurrentProfile();
      List<IncomeEntity> incomes = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
      return incomes.stream().map(this::toDTO).toList();
   }

   // get total incomes for current user
   public BigDecimal getTotalIncomesForCurrentUser() {
      ProfileEntity profile = profileService.getCurrentProfile();
      BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profile.getId());
      return total != null ? total : BigDecimal.ZERO;
   }

   // filter incomes by date range
   public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
      ProfileEntity profile = profileService.getCurrentProfile();

      List<IncomeEntity> incomes = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            profile.getId(), startDate, endDate, keyword, sort);

      return incomes.stream().map(this::toDTO).toList();

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
