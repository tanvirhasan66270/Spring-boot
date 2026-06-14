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
@RequestMapping("/api/po-line-items/") // REST API কনভেনশন অনুযায়ী Plural ও Kebab-case ব্যবহার করা হয়েছে
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Frontend (Angular/React) থেকে কল করার সময় CORS ব্লকিং এড়াতে
public class POLineItemController {

    private final POLineItemService poLineItemService;

    /**
     * 1. Add New Line Item to a Purchase Order (POST)
     * 💡 নোট: এই এপিআই হিট হওয়ামাত্র ব্যাকএন্ডে ইনভেন্টরি চেক পাস হলে স্টক লক হবে এবং প্যারেন্ট PO-এর গ্র্যান্ড টোটাল বেড়ে যাবে।
     */
    @PostMapping
    public ResponseEntity<POLineItemResponseDTO> create(@RequestBody POLineItemRequestDTO dto) {
        POLineItemResponseDTO response = poLineItemService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 2. Update Existing Line Item & Trigger State Machine (PUT)
     * 💡 নোট: স্ট্যাটাস "SHIPPED" দিলে ট্র্যাকিং কোড জেনারেট হবে এবং "CANCELLED" দিলে স্টক আবার ইনভেন্টরিতে রিস্টোর হয়ে যাবে।
     */
    @PutMapping("{id}")
    public ResponseEntity<POLineItemResponseDTO> update(
            @PathVariable Long id,
            @RequestBody POLineItemRequestDTO dto) {
        POLineItemResponseDTO response = poLineItemService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 3. Get All Line Items (GET)
     */
    @GetMapping
    public ResponseEntity<List<POLineItemResponseDTO>> getAll() {
        List<POLineItemResponseDTO> list = poLineItemService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 4. Get Line Item By ID (GET)
     */
    @GetMapping("{id}")
    public ResponseEntity<POLineItemResponseDTO> getById(@PathVariable Long id) {
        return poLineItemService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 5. Delete Line Item & Re-calculate Roll-up Grand Total (DELETE)

     * 💡 নোট: এটি ডিলিট হওয়ার সাথে সাথে প্যারেন্ট পারচেজ অর্ডারের মোট টাকার পরিমাণ (Grand Total) অটোমেটিক কমে যাবে।
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        poLineItemService.delete(id);
        return ResponseEntity.ok("Purchase Order Line Item deleted and parent order grand total updated successfully!");
    }
}