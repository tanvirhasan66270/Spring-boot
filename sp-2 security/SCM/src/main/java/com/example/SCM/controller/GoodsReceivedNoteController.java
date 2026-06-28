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
@RequestMapping("/api/goods-received-notes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoodsReceivedNoteController {

    private final GoodsReceivedNoteService goodsReceivedNoteService;

    @PostMapping
    public ResponseEntity<GoodsReceivedNoteResponseDTO> create(@RequestBody GoodsReceivedNoteRequestDTO dto) {
        GoodsReceivedNoteResponseDTO response = goodsReceivedNoteService.save(dto);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoodsReceivedNoteResponseDTO> update(
            @PathVariable Long id,
            @RequestBody GoodsReceivedNoteRequestDTO dto
    ) {
        GoodsReceivedNoteResponseDTO response = goodsReceivedNoteService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GoodsReceivedNoteResponseDTO>> getAll() {
        List<GoodsReceivedNoteResponseDTO> list = goodsReceivedNoteService.findAll();

        if (list.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoodsReceivedNoteResponseDTO> getById(@PathVariable Long id) {
        return goodsReceivedNoteService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        goodsReceivedNoteService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

}