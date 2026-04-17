package com.trandieu.moneymanager.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.trandieu.moneymanager.dto.ExpenseDTO;
import com.trandieu.moneymanager.dto.IncomeDTO;
import com.trandieu.moneymanager.dto.RecentTransactionDTO;
import com.trandieu.moneymanager.entity.ProfileEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class DashboardService {
   private final IncomeService incomeService;
   private final ExpenseService expenseService;
   private final ProfileService profileService;

   public Map<String, Object> getDashboardData() {
      ProfileEntity profile = profileService.getCurrentProfile();
      Map<String, Object> returnValue = new LinkedHashMap<>();
      List<IncomeDTO> lastestIncomes = incomeService.getLastest5IncomesForCurrentUser();
      List<ExpenseDTO> lastestExpenses = expenseService.getLastest5ExpensesForCurrentUser();
      List<RecentTransactionDTO> recentTransactionDTOs = Stream.concat(lastestIncomes.stream()
            .map(income -> RecentTransactionDTO.builder()
                  .id(income.getId())
                  .profileId(profile.getId())
                  .icon(income.getIcon())
                  .name(income.getName())
                  .amount(income.getAmount())
                  .date(income.getDate())
                  .createdAt(income.getCreatedAt())
                  .updatedAt(income.getUpdatedAt())
                  .type("income")
                  .build()),
            lastestExpenses.stream().map(expense -> RecentTransactionDTO.builder()
                  .id(expense.getId())
                  .profileId(profile.getId())
                  .icon(expense.getIcon())
                  .name(expense.getName())
                  .amount(expense.getAmount())
                  .date(expense.getDate())
                  .createdAt(expense.getCreatedAt())
                  .updatedAt(expense.getUpdatedAt())
                  .type("expense")
                  .build()))
            .sorted((a, b) -> {
               int cmp = b.getDate().compareTo(a.getDate());

               if (cmp == 0 && b.getCreatedAt() != null && a.getCreatedAt() != null) {
                  return b.getCreatedAt().compareTo(a.getCreatedAt());
               }
               return cmp;
            }).collect(Collectors.toList());
      returnValue.put("totalBalance",
            incomeService.getTotalIncomesForCurrentUser()
                  .subtract(expenseService.getTotalExpensesForCurrentUser()));

      returnValue.put("totalIncome", incomeService.getTotalIncomesForCurrentUser());
      returnValue.put("totalExpenses", expenseService.getTotalExpensesForCurrentUser());
      returnValue.put("recent5Expenses", lastestExpenses);
      returnValue.put("recent5Incomes", lastestIncomes);
      returnValue.put("recentTransactions", recentTransactionDTOs);
      return returnValue;
   }

}
