package com.example.SCM.controller;

import com.example.SCM.dto.request.WarehouseRequestDTO;
import com.example.SCM.dto.response.WarehouseResponseDTO;
import com.example.SCM.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> create(@RequestBody WarehouseRequestDTO dto) {
        return new ResponseEntity<>(warehouseService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> update(@PathVariable Long id, @RequestBody WarehouseRequestDTO dto) {
        return ResponseEntity.ok(warehouseService.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<WarehouseResponseDTO>> getAll() {
        List<WarehouseResponseDTO> list = warehouseService.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> getById(@PathVariable Long id) {
        return warehouseService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.ok("Warehouse deleted successfully from system tracking");
    }
}