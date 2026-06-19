package com.example.SCM.controller;

import com.example.SCM.dto.request.GoodsReceivedNoteRequestDTO;
import com.example.SCM.dto.response.GoodsReceivedNoteResponseDTO;
import com.example.SCM.service.GoodsReceivedNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goods-received-notes/") // প্রজেক্ট স্ট্যান্ডার্ড অনুযায়ী এন্ডপয়েন্ট বেস পাথ
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Frontend CORS ব্লকিং এড়াতে
public class GoodsReceivedNoteController {

    private final GoodsReceivedNoteService goodsReceivedNoteService;

    /**
     * 1. Create New Goods Received Note (POST)
     * 💡 ফ্রন্টঅ্যান্ড বা পোস্টম্যান থেকে সরাসরি JSON অবজেক্ট রিসিভ করার জন্য @RequestBody ব্যবহার করা হয়েছে।
     */
    @PostMapping
    public ResponseEntity<GoodsReceivedNoteResponseDTO> create(@RequestBody GoodsReceivedNoteRequestDTO dto) {
        GoodsReceivedNoteResponseDTO response = goodsReceivedNoteService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 2. Update Existing Goods Received Note (PUT)
     */
    @PutMapping("{id}")
    public ResponseEntity<GoodsReceivedNoteResponseDTO> update(
            @PathVariable Long id,
            @RequestBody GoodsReceivedNoteRequestDTO dto) {

        GoodsReceivedNoteResponseDTO response = goodsReceivedNoteService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 3. Get All Goods Received Notes (GET)
     */
    @GetMapping
    public ResponseEntity<List<GoodsReceivedNoteResponseDTO>> getAll() {
        List<GoodsReceivedNoteResponseDTO> list = goodsReceivedNoteService.findAll();

        if (list.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 Content
        }

        return ResponseEntity.ok(list);
    }

    /**
     * 4. Get Goods Received Note By ID (GET)
     */
    @GetMapping("{id}")
    public ResponseEntity<GoodsReceivedNoteResponseDTO> getById(@PathVariable Long id) {
        return goodsReceivedNoteService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 5. Delete Goods Received Note (DELETE)
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        goodsReceivedNoteService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}