package com.trandieu.moneymanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
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

   // Retrieves all expenses for current month/base on the start date and end date
   public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
      ProfileEntity profile = profileService.getCurrentProfile();
      LocalDate now = LocalDate.now();
      LocalDate startDate = now.withDayOfMonth(1);
      LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
      List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate,
            endDate);

      return expenses.stream().map(this::toDTO).toList();

   }

   // delete expense by id
   public void deleteExpenseById(Long id) {
      ProfileEntity profile = profileService.getCurrentProfile();
      ExpenseEntity expense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));

      if (!expense.getProfile().getId().equals(profile.getId())) {
         throw new RuntimeException("Unauthorized to delete this expense");
      }
      expenseRepository.delete(expense);
   }

   // get lastest 5 expenses for current user
   public List<ExpenseDTO> getLastest5ExpensesForCurrentUser() {
      ProfileEntity profile = profileService.getCurrentProfile();
      List<ExpenseEntity> expenses = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
      return expenses.stream().map(this::toDTO).toList();
   }

   // get total expenses for current user
   public BigDecimal getTotalExpensesForCurrentUser() {
      ProfileEntity profile = profileService.getCurrentProfile();
      BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
      return total != null ? total : BigDecimal.ZERO;
   }

   // filter expenses by date range
   public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
      ProfileEntity profile = profileService.getCurrentProfile();

      List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            profile.getId(), startDate, endDate, keyword, sort);

      return expenses.stream().map(this::toDTO).toList();

   }

   // notification
   public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date) {
    
      List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDate(profileId, date);
      return expenses.stream().map(this::toDTO).toList();
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
