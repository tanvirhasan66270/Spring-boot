package com.example.SCM.controller;

import com.example.SCM.dto.Response.CustomerResponseDTO;
import com.example.SCM.dto.request.CustomerRequestDTO;
import com.example.SCM.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/customer/")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerResponseDTO> save(
            @RequestPart("dto") CustomerRequestDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        CustomerResponseDTO savedCustomer = customerService.save(dto, file);
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<CustomerResponseDTO> getById(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.getById(id)
                .orElseThrow(() -> new RuntimeException("Customer profile not found with ID: " + id));
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.ok("Customer profile deleted successfully");
    }
}
