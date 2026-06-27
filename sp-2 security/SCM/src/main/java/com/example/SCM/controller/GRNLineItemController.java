package com.example.SCM.controller;

import com.example.SCM.dto.request.GRNLineItemRequestDTO;
import com.example.SCM.dto.response.GRNLineItemResponseDTO;
import com.example.SCM.service.GRNLineItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grn-line-items/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GRNLineItemController {

    private final GRNLineItemService grnLineItemService;

   // এটি সিঙ্গেল লাইন আইটেম আলাদাভাবে ইনভেন্টরিতে অ্যাড করার জন্য ব্যবহৃত হবে।

    @PostMapping
    public ResponseEntity<GRNLineItemResponseDTO> create(@RequestBody GRNLineItemRequestDTO dto) {
        GRNLineItemResponseDTO response = grnLineItemService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Update Existing GRN Line Item (PUT)

    @PutMapping("{id}")
    public ResponseEntity<GRNLineItemResponseDTO> update(
            @PathVariable Long id,
            @RequestBody GRNLineItemRequestDTO dto) {

        GRNLineItemResponseDTO response = grnLineItemService.update(id, dto);
        return ResponseEntity.ok(response);
    }

   // আপনার কাস্টম রিপোজিটরি মেথড থাকলে সার্ভিস ইমপ্লিমেন্টেশনে সেটি যুক্ত করে কুয়েরি অপ্টিমাইজ করে নিতে পারেন।

    @GetMapping
    public ResponseEntity<List<GRNLineItemResponseDTO>> getAll() {
        List<GRNLineItemResponseDTO> list = grnLineItemService.findAll();

        if (list.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 Content
        }

        return ResponseEntity.ok(list);
    }

    // 4. Get GRN Line Item By ID (GET)

    @GetMapping("{id}")
    public ResponseEntity<GRNLineItemResponseDTO> getById(@PathVariable Long id) {
        return grnLineItemService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete GRN Line Item By ID (DELETE)

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        grnLineItemService.delete(id);
        return ResponseEntity.ok("GRN Line Item deleted successfully with ID: " + id);
    }
}