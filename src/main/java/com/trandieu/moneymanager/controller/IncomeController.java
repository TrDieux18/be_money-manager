package com.trandieu.moneymanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trandieu.moneymanager.dto.IncomeDTO;
import com.trandieu.moneymanager.service.IncomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

   private final IncomeService incomeService;

   @PostMapping
   public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO) {
      IncomeDTO createDto = incomeService.addIncome(incomeDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(createDto);
   }

   @GetMapping
   public ResponseEntity<List<IncomeDTO>> getIncomes() {
      List<IncomeDTO> incomes = incomeService.getCurrentMonthIncomesForCurrentUser();
      return ResponseEntity.ok(incomes);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<String> deleteIncome(@PathVariable Long id) {
      incomeService.deleteIncomeById(id);
      return ResponseEntity.status(HttpStatus.ACCEPTED).body("Income deleted successfully");
   }
}
