package com.trandieu.moneymanager.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trandieu.moneymanager.dto.ExpenseDTO;
import com.trandieu.moneymanager.dto.FilterDTO;
import com.trandieu.moneymanager.dto.IncomeDTO;
import com.trandieu.moneymanager.service.ExpenseService;
import com.trandieu.moneymanager.service.IncomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")

public class FilterController {

   private final ExpenseService expenseService;

   private final IncomeService incomeService;

   @PostMapping
   public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filterDTO) {

      // Preparing the data or
      LocalDate startDate = filterDTO.getStartDate() != null ? filterDTO.getStartDate() : LocalDate.MIN;
      LocalDate endDate = filterDTO.getEndDate() != null ? filterDTO.getEndDate() : LocalDate.now();

      String keyword = filterDTO.getKeyword() != null ? filterDTO.getKeyword() : "";

      String sortField = filterDTO.getSortField() != null ? filterDTO.getSortField() : "date";

      Sort.Direction direction = "desc".equalsIgnoreCase(filterDTO.getSortOrder()) ? Sort.Direction.DESC
            : Sort.Direction.ASC;

      Sort sort = Sort.by(direction, sortField);

      if ("income".equalsIgnoreCase(filterDTO.getType())) {
         List<IncomeDTO> filteredIncomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
         return ResponseEntity.ok(filteredIncomes);
      } else if ("expense".equalsIgnoreCase(filterDTO.getType())) {
         List<ExpenseDTO> filteredExpenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
         return ResponseEntity.ok(filteredExpenses);
      } else {
         return ResponseEntity.badRequest().body("Invalid type. Must be 'income' or 'expense'.");
      }
   }
}
