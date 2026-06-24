package com.example.SCM.controller;



import com.example.SCM.dto.response.SupplierResponseDTO;
import com.example.SCM.dto.request.SupplierRequestDTO;
import com.example.SCM.service.SupplierService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Frontend বা Angular/React থেকে কল করার সময় CORS এরর এড়াতে
public class SupplierController {


    private final SupplierService supplierService;


    @PostMapping
    public ResponseEntity<SupplierResponseDTO> save(
            @RequestPart("suppliers") String supplierJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
       SupplierRequestDTO dto = mapper.readValue(supplierJson, SupplierRequestDTO.class);

        return new ResponseEntity<>(
                supplierService.save(dto, image),
                HttpStatus.CREATED
        );
    }
    @Transactional
    @PutMapping("{id}")
    public SupplierResponseDTO update(
            @PathVariable Long id,
            @RequestPart("suppliers") SupplierRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return supplierService.update(id, dto, image);
    }




    @GetMapping
    public ResponseEntity<List<SupplierResponseDTO>> getAll() {
        List<SupplierResponseDTO> list = supplierService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }


    @GetMapping("{id}")
    public ResponseEntity<SupplierResponseDTO> getById(@PathVariable Long id) {
        return supplierService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.ok("Supplier profile and associated auth account deleted successfully!");
    }
}