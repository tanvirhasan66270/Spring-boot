package com.example.SCM.controller;

import com.example.SCM.dto.response.WarehouseResponseDTO;
import com.example.SCM.dto.request.WarehouseRequestDTO;
import com.example.SCM.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses/") // বহুবচন বা Plural নাম ব্যবহার করা স্ট্যান্ডার্ড REST API কনভেনশন
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ফ্রন্টএন্ড থেকে কল করার সময় ব্রাউজারের CORS পলিসি জনিত ব্লকিং এড়াতে
public class WarehouseController {

    private final WarehouseService warehouseService;

   // 1. Create New Warehouse (POST)

    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> create(@RequestBody WarehouseRequestDTO dto) {
        WarehouseResponseDTO response = warehouseService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Update Existing Warehouse (PUT)

    @PutMapping("{id}")
    public ResponseEntity<WarehouseResponseDTO> update(
            @PathVariable Long id,
            @RequestBody WarehouseRequestDTO dto) {
        WarehouseResponseDTO response = warehouseService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    // 3. Get All Warehouses (GET)

    @GetMapping
    public ResponseEntity<List<WarehouseResponseDTO>> getAll() {
        List<WarehouseResponseDTO> list = warehouseService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    //4. Get Warehouse By ID (GET)

    @GetMapping("{id}")
    public ResponseEntity<WarehouseResponseDTO> getById(@PathVariable Long id) {
        return warehouseService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


     //5. Delete Warehouse (DELETE)

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.ok("Warehouse location and its active configurations deleted successfully!");
    }
}