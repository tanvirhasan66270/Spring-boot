package com.example.SCM.controller;

import com.example.SCM.dto.request.VehicleRequestDTO;
import com.example.SCM.dto.response.VehicleResponseDTO;
import com.example.SCM.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> create(@RequestBody VehicleRequestDTO dto) {
        return new ResponseEntity<>(vehicleService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> update(@PathVariable Long id, @RequestBody VehicleRequestDTO dto) {
        return ResponseEntity.ok(vehicleService.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAll() {
        List<VehicleResponseDTO> list = vehicleService.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getById(@PathVariable Long id) {
        return vehicleService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.ok("Vehicle successfully decommissioned from active tracking logistics");
    }
}