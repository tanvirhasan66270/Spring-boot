package com.example.SCM.controller;

import com.example.SCM.dto.request.PurchaseRequisitionRequestDTO;
import com.example.SCM.dto.response.PurchaseRequisitionResponseDTO;
import com.example.SCM.service.PurchaseRequisitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-requisitions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PurchaseRequisitionController {

    private final PurchaseRequisitionService purchaseRequisitionService;

    @PostMapping
    public ResponseEntity<PurchaseRequisitionResponseDTO> create(@RequestBody PurchaseRequisitionRequestDTO dto) {
        PurchaseRequisitionResponseDTO response = purchaseRequisitionService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<PurchaseRequisitionResponseDTO> approve(@PathVariable Long id) {
        PurchaseRequisitionResponseDTO response = purchaseRequisitionService.approveRequisition(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject-or-cancel")
    public ResponseEntity<PurchaseRequisitionResponseDTO> rejectOrCancel(
            @PathVariable Long id,
            @RequestParam String actionType
    ) {
        PurchaseRequisitionResponseDTO response = purchaseRequisitionService.rejectOrCancelRequisition(id, actionType);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseRequisitionResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PurchaseRequisitionRequestDTO dto) {
        PurchaseRequisitionResponseDTO response = purchaseRequisitionService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseRequisitionResponseDTO>> getAll() {
        List<PurchaseRequisitionResponseDTO> list = purchaseRequisitionService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseRequisitionResponseDTO> getById(@PathVariable Long id) {
        return purchaseRequisitionService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        purchaseRequisitionService.delete(id);
        return ResponseEntity.ok("Purchase Requisition deleted successfully from procurement records!");
    }
}