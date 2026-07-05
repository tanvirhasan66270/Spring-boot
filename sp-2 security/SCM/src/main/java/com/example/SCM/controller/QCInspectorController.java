package com.example.SCM.controller;

import com.example.SCM.dto.request.QCInspectorRequestDTO;
import com.example.SCM.dto.response.QCInspectorResponseDTO;
import com.example.SCM.service.QCInspectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/qc-inspectors")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // লম্বক দিয়ে ফাইনাল ফিল্ডের জন্য কনস্ট্রাক্টর তৈরি
public class QCInspectorController {

    // @Autowired বাদ দিয়ে final করা হয়েছে (কনস্ট্রাক্টর ইনজেকশন বেস্ট প্র্যাকটিস)
    private final QCInspectorService qcInspectorService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QCInspectorResponseDTO> create(
            @RequestPart("qcInspector") QCInspectorRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return new ResponseEntity<>(
                qcInspectorService.save(dto, image),
                HttpStatus.CREATED
        );
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QCInspectorResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("qcInspector") QCInspectorRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return ResponseEntity.ok(qcInspectorService.update(id, dto, image));
    }


    @GetMapping
    public ResponseEntity<List<QCInspectorResponseDTO>> getAll() {
        List<QCInspectorResponseDTO> inspectors = qcInspectorService.findAll();

        if (inspectors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(inspectors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QCInspectorResponseDTO> getById(@PathVariable Long id) {
        return qcInspectorService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        qcInspectorService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}