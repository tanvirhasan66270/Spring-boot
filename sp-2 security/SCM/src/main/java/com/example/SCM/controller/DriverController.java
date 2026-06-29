package com.example.SCM.controller;

import com.example.SCM.dto.request.DriverRequestDTO;
import com.example.SCM.dto.response.DriverResponseDTO;
import com.example.SCM.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DriverController {

    private final DriverService driverService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<DriverResponseDTO> save(
            @RequestPart("driver") String driverJson,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) {
        try {
            DriverRequestDTO dto = objectMapper.readValue(driverJson, DriverRequestDTO.class);
            DriverResponseDTO response = driverService.save(dto, file);
            return new ResponseEntity<>(
                    response,
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            throw new RuntimeException("Driver profile mapping transaction aborted: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<DriverResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("driver") String driverJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            DriverRequestDTO dto = objectMapper.readValue(driverJson, DriverRequestDTO.class);
            DriverResponseDTO response = driverService.update(id, dto, file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Driver data mutation transaction aborted: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> findAll() {
        return ResponseEntity.ok(driverService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> getById(@PathVariable Long id) {
        return driverService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }

}