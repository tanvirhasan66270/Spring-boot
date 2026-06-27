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
@RequestMapping("/api/purchase-requisitions/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PurchaseRequisitionController {

    private final PurchaseRequisitionService purchaseRequisitionService;

    //1. Create New Purchase Requisition (POST)

    @PostMapping
    public ResponseEntity<PurchaseRequisitionResponseDTO> create(@RequestBody PurchaseRequisitionRequestDTO dto) {
        PurchaseRequisitionResponseDTO response = purchaseRequisitionService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Update Existing Purchase Requisition (PUT)

    @PutMapping("{id}")
    public ResponseEntity<PurchaseRequisitionResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PurchaseRequisitionRequestDTO dto) {
        PurchaseRequisitionResponseDTO response = purchaseRequisitionService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    // 3. Get All Purchase Requisitions (GET)

    @GetMapping
    public ResponseEntity<List<PurchaseRequisitionResponseDTO>> getAll() {
        List<PurchaseRequisitionResponseDTO> list = purchaseRequisitionService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    // 4. Get Purchase Requisition By ID (GET)

    @GetMapping("{id}")
    public ResponseEntity<PurchaseRequisitionResponseDTO> getById(@PathVariable Long id) {
        return purchaseRequisitionService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

   // 5. Delete Purchase Requisition (DELETE)

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        purchaseRequisitionService.delete(id);
        return ResponseEntity.ok("Purchase Requisition deleted successfully from procurement records!");
    }
}