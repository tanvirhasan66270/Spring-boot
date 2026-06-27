package com.example.SCM.controller;

import com.example.SCM.dto.request.LetterOfCreditRequestDTO;
import com.example.SCM.dto.response.LetterOfCreditResponseDTO;
import com.example.SCM.service.LetterOfCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/lc/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LetterOfCreditController {

    private final LetterOfCreditService lcService;

    // 1. Issue a New Letter of Credit (Draft Mode)

    @PostMapping
    public ResponseEntity<LetterOfCreditResponseDTO> createLC(@RequestBody LetterOfCreditRequestDTO dto) {
        LetterOfCreditResponseDTO response = lcService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ⚙️ 2. General Update LC Metadata (No Counter Increment)

    @PutMapping("{id}")
    public ResponseEntity<LetterOfCreditResponseDTO> updateLC(
            @PathVariable Long id,
            @RequestBody LetterOfCreditRequestDTO dto) {
        LetterOfCreditResponseDTO response = lcService.update(id, dto);
        return ResponseEntity.ok(response);
    }

   // Official LC Amendment Gateway (Triggers Counter & Status Change)
    // PATCH http://localhost:8085/api/lc/amend/{id}

    @PatchMapping("amend/{id}")
    public ResponseEntity<LetterOfCreditResponseDTO> amendLC(
            @PathVariable Long id,
            @RequestBody LetterOfCreditRequestDTO dto) {
        LetterOfCreditResponseDTO response = lcService.amendLC(id, dto);
        return ResponseEntity.ok(response);
    }

    //  4. Get All Letters of Credit

    @GetMapping
    public ResponseEntity<List<LetterOfCreditResponseDTO>> getAllLCs() {
        return ResponseEntity.ok(lcService.findAll());
    }

    //  5. Get LC Profile By ID

    @GetMapping("{id}")
    public ResponseEntity<LetterOfCreditResponseDTO> getLCById(@PathVariable Long id) {
        return lcService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

   // Delete LC Instance

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteLC(@PathVariable Long id) {
        lcService.delete(id);
        return ResponseEntity.ok("Letter of credit cluster mapping wiped successfully.");
    }
}