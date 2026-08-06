package com.example.SCM.controller;

import com.example.SCM.dto.request.InvoiceRequestDTO;
import com.example.SCM.dto.response.InvoiceResponseDTO;
import com.example.SCM.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService service;

    // 1. Create New Invoice Ledger Node (POST)

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL_OFFICER', 'SALES_OFFICER', 'PROCUREMENT')")
    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL_OFFICER', 'PROCUREMENT')")
    public ResponseEntity<InvoiceResponseDTO> create(@RequestBody InvoiceRequestDTO dto) {
        InvoiceResponseDTO response = service.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Mutate/Update Existing Invoice State Matrix (PUT)

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL_OFFICER', 'SALES_OFFICER', 'PROCUREMENT')")
    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL_OFFICER', 'PROCUREMENT')")
    public ResponseEntity<InvoiceResponseDTO> update(@PathVariable Long id, @RequestBody InvoiceRequestDTO dto) {
        InvoiceResponseDTO response = service.update(id, dto);
        return ResponseEntity.ok(response);
    }
    // Fetch All Invoices Register Dataset (GET)

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL_OFFICER', 'SALES_OFFICER', 'CUSTOMER', 'PROCUREMENT', 'LOGISTICS_OFFICER', 'DRIVER', 'QC_INSPECTOR', 'SUPPLIER')")
    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL_OFFICER', 'SALES_OFFICER', 'PROCUREMENT')")
    public ResponseEntity<List<InvoiceResponseDTO>> findAll() {
        List<InvoiceResponseDTO> list = service.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    // Fetch Invoice Instance Details By Unique Record ID (GET)

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL_OFFICER', 'SALES_OFFICER', 'CUSTOMER', 'PROCUREMENT', 'LOGISTICS_OFFICER', 'DRIVER', 'QC_INSPECTOR', 'SUPPLIER')")
    @GetMapping("/{id}")

    public ResponseEntity<InvoiceResponseDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL_OFFICER', 'SALES_OFFICER', 'CUSTOMER', 'PROCUREMENT', 'LOGISTICS_OFFICER', 'DRIVER', 'QC_INSPECTOR', 'SUPPLIER')")
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<InvoiceResponseDTO> getByOrderId(@PathVariable Long id) {
        InvoiceResponseDTO response = service.getByOrderNumberOrId(id);
        return ResponseEntity.ok(response);
    }
    // Wipe/Drop Invoice Lifecycle Instance Pointer (DELETE)

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'COMMERCIAL_OFFICER', 'SALES_OFFICER', 'PROCUREMENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
