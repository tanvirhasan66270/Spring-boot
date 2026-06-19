package com.example.SCM.controller;

import com.example.SCM.dto.request.QCInspectorRequestDTO;
import com.example.SCM.dto.response.QCInspectorResponseDTO;
import com.example.SCM.service.QCInspectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/qc-inspectors/") // আপনার কনভেনশন অনুযায়ী এন্ডপয়েন্ট বেস পাথ
@CrossOrigin(origins = "*") // Frontend CORS ব্লকিং এড়াতে
public class QCInspectorController {

    @Autowired
    private QCInspectorService qcInspectorService;

    /**
     * 1. Create New QC Inspector with Profile Image (POST)
     * 💡 আপনার রাইডার কন্ট্রোলারের মতো হুবহু @RequestPart কনভেনশনে সিঙ্ক করা হয়েছে।
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QCInspectorResponseDTO> create(
            @RequestPart("qcInspector") QCInspectorRequestDTO dto, // আপনার প্যাটার্ন অনুযায়ী নিমিং
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return new ResponseEntity<>(
                qcInspectorService.save(dto, image),
                HttpStatus.CREATED
        );
    }

    /**
     * 2. Update Existing QC Inspector Profile & User Data (PUT)
     */
    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QCInspectorResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("qcInspector") QCInspectorRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return ResponseEntity.ok(qcInspectorService.update(id, dto, image));
    }

    /**
     * 3. Get All QC Inspectors (GET)
     */
    @GetMapping
    public ResponseEntity<List<QCInspectorResponseDTO>> getAll() {
        List<QCInspectorResponseDTO> inspectors = qcInspectorService.findAll();

        if (inspectors.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 Content
        }

        return ResponseEntity.ok(inspectors);
    }

    /**
     * 4. Get QC Inspector By ID (GET)
     */
    @GetMapping("{id}")
    public QCInspectorResponseDTO getById(@PathVariable Long id) {
        return qcInspectorService.getById(id)
                .orElseThrow(() -> new RuntimeException("QC Inspector not found with ID: " + id));
    }

    /**
     * 5. Delete QC Inspector & Associated Auth User Account (DELETE)
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        qcInspectorService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}