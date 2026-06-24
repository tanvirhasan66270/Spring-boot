package com.example.SCM.controller;

import com.example.SCM.dto.request.QCInspectionRequestDTO;
import com.example.SCM.dto.response.QCInspectionResponseDTO;
import com.example.SCM.service.QCInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/qc-inspections/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QCInspectionController {

    private final QCInspectionService qcInspectionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QCInspectionResponseDTO> create(
            @RequestPart("inspection") String inspectionJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        QCInspectionRequestDTO dto = mapper.readValue(inspectionJson, QCInspectionRequestDTO.class);
        return new ResponseEntity<>(qcInspectionService.save(dto, file), HttpStatus.CREATED);
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QCInspectionResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("inspection") String inspectionJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        QCInspectionRequestDTO dto = mapper.readValue(inspectionJson, QCInspectionRequestDTO.class);
        return ResponseEntity.ok(qcInspectionService.update(id, dto, file));
    }

    @GetMapping
    public ResponseEntity<List<QCInspectionResponseDTO>> getAll() {
        List<QCInspectionResponseDTO> list = qcInspectionService.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("{id}")
    public ResponseEntity<QCInspectionResponseDTO> getById(@PathVariable Long id) {
        return qcInspectionService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        qcInspectionService.delete(id);
        return ResponseEntity.ok("QC Record with checklist chain deleted successfully");
    }
}