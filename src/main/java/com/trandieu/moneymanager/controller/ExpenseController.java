package com.trandieu.moneymanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trandieu.moneymanager.dto.ExpenseDTO;
import com.trandieu.moneymanager.service.ExpenseService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

   private final ExpenseService expenseService;

   @PostMapping
   public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO) {
      ExpenseDTO createDto = expenseService.addExpense(expenseDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(createDto);
   }

   @GetMapping
   public ResponseEntity<List<ExpenseDTO>> getExpenses() {
      List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
      return ResponseEntity.ok(expenses);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
      expenseService.deleteExpenseById(id);
      return ResponseEntity.status(HttpStatus.ACCEPTED).body("Expense deleted successfully");
   }
}
