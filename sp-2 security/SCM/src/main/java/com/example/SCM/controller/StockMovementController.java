package com.example.SCM.controller;

import com.example.SCM.dto.request.StockMovementRequestDTO;
import com.example.SCM.dto.response.StockMovementResponseDTO;
import com.example.SCM.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-movements/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class StockMovementController {

    private final StockMovementService service;


    @PostMapping
    public ResponseEntity<StockMovementResponseDTO> logMovement(@RequestBody StockMovementRequestDTO dto) {
        StockMovementResponseDTO response = service.logMovement(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<StockMovementResponseDTO>> findAll() {
        List<StockMovementResponseDTO> list = service.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }


    @GetMapping("{id}")
    public ResponseEntity<StockMovementResponseDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}