package com.example.SCM.controller;

import com.example.SCM.dto.request.POLineItemRequestDTO;
import com.example.SCM.dto.response.POLineItemResponseDTO;
import com.example.SCM.service.POLineItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/po-line-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Frontend (Angular/React) থেকে কল করার সময় CORS পলিসি জনিত ব্লকিং এড়াতে
public class POLineItemController {

    private final POLineItemService poLineItemService;


    @PostMapping
    public ResponseEntity<POLineItemResponseDTO> save(@RequestBody POLineItemRequestDTO dto) {
        POLineItemResponseDTO response = poLineItemService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<POLineItemResponseDTO> update(
            @PathVariable Long id,
            @RequestBody POLineItemRequestDTO dto) {
        POLineItemResponseDTO response = poLineItemService.update(id, dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<POLineItemResponseDTO>> getAll() {
        List<POLineItemResponseDTO> list = poLineItemService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }


    @GetMapping("/{id}")
    public ResponseEntity<POLineItemResponseDTO> getById(@PathVariable Long id) {
        return poLineItemService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        poLineItemService.delete(id);
        return ResponseEntity.ok("Purchase Order Line Item deleted and parent order total amount updated successfully!");
    }

     // Track Purchase Order Line Item Status (GET)
     //লজিস্টিকস ও ট্র্যাকিং ড্যাশবোর্ডে মার্চেন্ট বা ক্লায়েন্ট কোড দিয়ে সার্চ করার জন্য এন্ডপয়েন্ট

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<POLineItemResponseDTO> trackByNumber(@PathVariable String trackingNumber) {
        POLineItemResponseDTO response = poLineItemService.tracking(trackingNumber);
        return ResponseEntity.ok(response);
    }
}