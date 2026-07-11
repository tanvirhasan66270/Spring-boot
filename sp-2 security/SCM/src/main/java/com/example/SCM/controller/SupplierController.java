package com.example.SCM.controller;

import com.example.SCM.dto.response.ManagerResponseDTO;
import com.example.SCM.dto.response.SupplierResponseDTO;
import com.example.SCM.dto.request.SupplierRequestDTO;
import com.example.SCM.service.SupplierService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SupplierController {

    private final SupplierService supplierService;
    private final ObjectMapper objectMapper; // 🌟 কনস্ট্রাক্টর ইনজেকশনের মাধ্যমে মেমরিতে থাকা বিন ব্যবহার করা হলো (নিচে বারবার নতুন নিউ করতে হবে না)

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<SupplierResponseDTO> save(
            @RequestPart("suppliers") String supplierJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            SupplierRequestDTO dto = objectMapper.readValue(supplierJson, SupplierRequestDTO.class);
            return new ResponseEntity<>(supplierService.save(dto, image), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException("Supplier profile node generation failed: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<SupplierResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("suppliers") String supplierJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            SupplierRequestDTO dto = objectMapper.readValue(supplierJson, SupplierRequestDTO.class);
            return ResponseEntity.ok(supplierService.update(id, dto, image));
        } catch (Exception e) {
            throw new RuntimeException("Supplier profile data mutation rejected: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAll() {
        List<SupplierResponseDTO> list = supplierService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> getById(@PathVariable Long id) {
        return supplierService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.ok("Supplier profile and associated auth account deleted successfully!");
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<SupplierResponseDTO> getByUserId(@PathVariable Long id) {
        return supplierService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}