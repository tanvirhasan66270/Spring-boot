package com.example.SCM.controller;

import com.example.SCM.dto.request.CustomerRequestDTO;
import com.example.SCM.dto.response.CustomerResponseDTO;
import com.example.SCM.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/customers/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 👤 1. Create/Register New Customer with Profile Image
     * URL: POST http://localhost:8080/api/customers
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerResponseDTO> create(
            @RequestPart("customer") CustomerRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        CustomerResponseDTO response = customerService.save(dto, image);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 🔄 2. Update Customer Metadata & Profile Picture (PUT)
     * URL: PUT http://localhost:8080/api/customers/{id}
     */
    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("customer") CustomerRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(customerService.update(id, dto, image));
    }

    /**
     * 📋 3. Get All Registered Customers (GET)
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        List<CustomerResponseDTO> list = customerService.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * 🔍 4. Get Customer Profile By ID (GET)
     */
    @GetMapping("{id}")
    public ResponseEntity<CustomerResponseDTO> getById(@PathVariable Long id) {
        return customerService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ❌ 5. Delete Customer Node and Associated Auth Account (DELETE)
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.ok("Customer matrix index and associated auth account purged successfully.");
    }
}