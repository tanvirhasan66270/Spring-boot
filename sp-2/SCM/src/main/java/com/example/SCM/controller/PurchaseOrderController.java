package com.example.SCM.controller;

import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import com.example.SCM.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders/") // 💡 কারেকশন: এন্টারপ্রাইজ কনভেনশন অনুযায়ী শেষের "/" বাদ দেওয়া হয়েছে
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Frontend (Angular/React) থেকে কল করার সময় CORS পলিসি জনিত ব্লকিং এড়াতে
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    /**
     * 1. Create/Issue New Purchase Order (POST)
     */
    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> save(@RequestBody PurchaseOrderRequestDTO dto) {
        PurchaseOrderResponseDTO response = purchaseOrderService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 2. Update Existing Purchase Order (PUT)
     */
    @PutMapping("{id}") // 💡 কারেকশন: শুরুতে স্ল্যাশ (/{id}) যুক্ত করা হয়েছে
    public ResponseEntity<PurchaseOrderResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PurchaseOrderRequestDTO dto) {
        PurchaseOrderResponseDTO response = purchaseOrderService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 3. Get All Purchase Orders (GET)
     */
    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAll() {
        List<PurchaseOrderResponseDTO> list = purchaseOrderService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 4. Get Purchase Order By ID (GET)
     */
    @GetMapping("{id}") // 💡 কারেকশন: শুরুতে স্ল্যাশ (/{id}) যুক্ত করা হয়েছে
    public ResponseEntity<PurchaseOrderResponseDTO> getById(@PathVariable Long id) {
        return purchaseOrderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 5. Delete Purchase Order (DELETE)
     */
    @DeleteMapping("{id}") // 💡 কারেকশন: শুরুতে স্ল্যাশ (/{id}) যুক্ত করা হয়েছে
    public ResponseEntity<String> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.ok("Purchase Order deleted successfully from the procurement system!");
    }
}