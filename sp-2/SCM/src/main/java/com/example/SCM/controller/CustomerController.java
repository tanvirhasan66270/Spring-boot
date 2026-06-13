package com.example.SCM.controller;

import com.example.SCM.dto.mapper.CustomerMapper;
import com.example.SCM.dto.request.CustomerRequestDTO;
import com.example.SCM.dto.response.CustomerResponseDTO;
import com.example.SCM.service.CustomerService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/customer/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Frontend বা Angular/React থেকে CORS পলিসি জনিত এরর এড়াতে
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    // ১. কাস্টমার তৈরি করা (POST - Handles both JSON and Image File)
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<CustomerResponseDTO> save(
            @RequestPart("dto") String customerJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {

        // JSON String-কে ObjectMapper দিয়ে CustomerRequestDTO অবজেক্টে রূপান্তর
        ObjectMapper mapper = new ObjectMapper();
        CustomerRequestDTO dto = mapper.readValue(customerJson, CustomerRequestDTO.class);

        CustomerResponseDTO response = customerService.save(dto, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ২. সব কাস্টমারের লিস্ট দেখা (GET)
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        List<CustomerResponseDTO> list = customerService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    // ৩. নির্দিষ্ট আইডি দিয়ে কাস্টমার খোঁজা (GET By ID)
    @GetMapping("{id}")
    public ResponseEntity<CustomerResponseDTO> findById(@PathVariable Long id) {
        return customerService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ৪. কাস্টমার প্রোফাইল এবং অ্যাসোসিয়েটেড ইউজার ডিলিট করা (DELETE)
    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.ok("Customer and associated User account deleted successfully.");
    }
}