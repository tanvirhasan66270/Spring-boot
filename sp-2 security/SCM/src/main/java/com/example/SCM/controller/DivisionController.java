package com.example.SCM.controller;

import com.example.SCM.dto.request.DivisionRequestDTO;
import com.example.SCM.dto.response.DivisionResponseDTO;
import com.example.SCM.service.DivisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/divisions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DivisionController {

    private final DivisionService divisionService;

    @PostMapping
    public ResponseEntity<DivisionResponseDTO> create(@RequestBody DivisionRequestDTO dto) {
        return new ResponseEntity<>(divisionService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DivisionResponseDTO> update(@PathVariable Long id, @RequestBody DivisionRequestDTO dto) {
        return ResponseEntity.ok(divisionService.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<DivisionResponseDTO>> getAll(
            @RequestParam(value = "onlyActive", defaultValue = "true") boolean onlyActive) {
        List<DivisionResponseDTO> list = divisionService.findAll(onlyActive);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    // নির্দিষ্ট দেশের আইডি পাস করে তার আন্ডারে থাকা সমস্ত স্টেট বা ডিভিশন ফিল্টার করার এন্ডপয়েন্ট।

    @GetMapping("/country/{countryId}")
    public ResponseEntity<List<DivisionResponseDTO>> getByCountryId(@PathVariable Long countryId) {
        List<DivisionResponseDTO> list = divisionService.getByCountryId(countryId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DivisionResponseDTO> getById(@PathVariable Long id) {
        return divisionService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        divisionService.delete(id);
        return ResponseEntity.ok("Division deleted successfully");
    }
}