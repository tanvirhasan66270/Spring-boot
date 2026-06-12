package com.example.SCM.controller;

import com.example.SCM.dto.response.CustomerResponseDTO;
import com.example.SCM.dto.request.CustomerRequestDTO;
import com.example.SCM.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Frontend-এর CORS সমস্যা এড়ানোর জন্য
public class CustomerController {

    private final CustomerService customerService;

    // ১. কাস্টমার তৈরি করা (Create / Save)
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> save(
            @ModelAttribute CustomerRequestDTO dto, // form-data হ্যান্ডেল করার জন্য @ModelAttribute
            @RequestParam(value = "file", required = false) MultipartFile file) {

        CustomerResponseDTO response = customerService.save(dto, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ২. সব কাস্টমারের লিস্ট দেখা (Get All)
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        List<CustomerResponseDTO> list = customerService.findAll();
        return ResponseEntity.ok(list);
    }

    // ৩. নির্দিষ্ট আইডি দিয়ে কাস্টমার খোঁজা (Get By ID)
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> findById(@PathVariable Long id) {
        CustomerResponseDTO response = customerService.getById(id)
                .orElseThrow(() -> new RuntimeException("Customer Not Found with ID: " + id));
        return ResponseEntity.ok(response);
    }

    // ৪. কাস্টমার প্রোফাইল ডিলিট করা (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.ok("Customer and associated User account deleted successfully.");
    }
}