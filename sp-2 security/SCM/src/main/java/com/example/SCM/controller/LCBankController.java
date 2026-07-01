package com.example.SCM.controller;

import com.example.SCM.dto.request.LCBankRequestDTO;
import com.example.SCM.dto.response.LCBankResponseDTO;
import com.example.SCM.service.LCBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LCBankController {

    private final LCBankService bankService;

    @PostMapping
    public ResponseEntity<LCBankResponseDTO> createBank(@RequestBody LCBankRequestDTO dto) {
        LCBankResponseDTO response = bankService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LCBankResponseDTO> updateBank(
            @PathVariable Long id,
            @RequestBody LCBankRequestDTO dto) {
        LCBankResponseDTO response = bankService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LCBankResponseDTO>> getAllBanks() {
        List<LCBankResponseDTO> list = bankService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LCBankResponseDTO> getBankById(@PathVariable Long id) {
        return bankService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBank(@PathVariable Long id) {
        bankService.delete(id);
        return ResponseEntity.ok("LC Bank mapping profile wiped successfully.");
    }
}