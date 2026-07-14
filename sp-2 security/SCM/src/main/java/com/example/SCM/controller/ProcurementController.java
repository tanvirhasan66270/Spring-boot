package com.example.SCM.controller;

import com.example.SCM.dto.request.ProcurementRequestDTO;
import com.example.SCM.dto.response.ManagerResponseDTO;
import com.example.SCM.dto.response.ProcurementResponseDTO;
import com.example.SCM.service.ProcurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/procurements")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ProcurementController {

    private final ProcurementService procurementService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ProcurementResponseDTO> save(
            @RequestPart("procurement") String procurementJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ProcurementRequestDTO dto = objectMapper.readValue(procurementJson, ProcurementRequestDTO.class);
            return new ResponseEntity<>(procurementService.save(dto, file), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException("Procurement integration block interrupted: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProcurementResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("procurement") String procurementJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ProcurementRequestDTO dto = objectMapper.readValue(procurementJson, ProcurementRequestDTO.class);
            return ResponseEntity.ok(procurementService.update(id, dto, file));
        } catch (Exception e) {
            throw new RuntimeException("Procurement data mutation failed at lifecycle handler: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ProcurementResponseDTO>> findAll() {
        return ResponseEntity.ok(procurementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcurementResponseDTO> getById(@PathVariable Long id) {
        return procurementService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        procurementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @procurementSecurity.isSelf(#id, authentication)")
    public ResponseEntity<ProcurementResponseDTO> getByUserId(@PathVariable Long id) {
        return procurementService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}