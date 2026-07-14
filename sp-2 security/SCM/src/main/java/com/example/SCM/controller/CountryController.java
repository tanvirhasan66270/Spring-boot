package com.example.SCM.controller;

import com.example.SCM.dto.request.CountryRequestDTO;
import com.example.SCM.dto.response.CountryResponseDTO;
import com.example.SCM.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/country")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CountryResponseDTO> create(@RequestBody CountryRequestDTO dto) {
        CountryResponseDTO response = countryService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CountryResponseDTO> update(
            @PathVariable Long id,
            @RequestBody CountryRequestDTO dto) {
        CountryResponseDTO response = countryService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    //ড্রপডাউনের জন্য শুধুমাত্র একটিভ দেশগুলো ফিল্টার করার ব্যবস্থা রাখা হয়েছে।

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CountryResponseDTO>> getAll(
            @RequestParam(value = "onlyActive", defaultValue = "true") boolean onlyActive) {
        List<CountryResponseDTO> list = countryService.findAll(onlyActive);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CountryResponseDTO> getById(@PathVariable Long id) {
        return countryService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        countryService.delete(id);
        return ResponseEntity.ok("Country deleted successfully with ID: " + id);
    }
}