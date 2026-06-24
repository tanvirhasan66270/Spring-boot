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
     * 1. Create New Purchase Order (POST)
     *  ফ্রন্টঅ্যান্ড টাইপস্ক্রিপ্ট ইন্টারফেস থেকে আসা পিওর JSON অবজেক্ট রিসিভ করার জন্য @RequestBody ব্যবহার করা হয়েছে।
     * লজিক অনুযায়ী, এখানে শুধুমাত্র quotationId পাঠালেই বাকি সব রিলেশন ব্যাকঅ্যান্ডে অটো-লোড হবে।
     */
    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> create(@RequestBody PurchaseOrderRequestDTO dto) {
        PurchaseOrderResponseDTO response = purchaseOrderService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 2. Update Existing Purchase Order (PUT)
     */
    @PutMapping("{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PurchaseOrderRequestDTO dto) {

        PurchaseOrderResponseDTO response = purchaseOrderService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 3. Get All Purchase Orders (GET)
     *  এটি কাস্টম Fetch Join কুয়েরি ব্যবহার করে এক ট্রিপে সব ডাটা অপ্টিমাইজড উপায়ে নিয়ে আসবে।
     */
    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAll() {
        List<PurchaseOrderResponseDTO> list = purchaseOrderService.findAll();

        if (list.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 Content
        }

        return ResponseEntity.ok(list);
    }

    /**
     * 4. Get Purchase Order By ID (GET)
     */
    @GetMapping("{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> getById(@PathVariable Long id) {
        return purchaseOrderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 5. Delete Purchase Order By ID (DELETE)
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}