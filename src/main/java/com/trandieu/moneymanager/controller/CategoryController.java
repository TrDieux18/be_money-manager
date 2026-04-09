package com.trandieu.moneymanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trandieu.moneymanager.dto.CategoryDTO;
import com.trandieu.moneymanager.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

   private final CategoryService categoryService;

   @PostMapping
   public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
      CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
      return ResponseEntity.ok(savedCategory);

   }
}
