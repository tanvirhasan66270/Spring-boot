package com.example.SCM.controller;

import com.example.SCM.dto.response.CategoryResponseDTO;
import com.example.SCM.dto.request.CategoryRequestDTO;
import com.example.SCM.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;


     // 1. Create New Category (POST)

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> save(@RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO response = categoryService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


     // 2. Update Existing Category (PUT)

    @PutMapping("{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO response = categoryService.update(id, dto);
        return ResponseEntity.ok(response);
    }


     // 3. Get All Categories (GET)


    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAll() {
        List<CategoryResponseDTO> list = categoryService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    // 4. Get Category By ID (GET)

    @GetMapping("{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        return categoryService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

   // 5. Delete Category (DELETE)

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok("Category and its cascades deleted successfully!");
    }
}