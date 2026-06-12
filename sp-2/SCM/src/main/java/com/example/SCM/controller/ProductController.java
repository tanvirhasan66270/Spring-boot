package com.example.SCM.controller;

import com.example.SCM.dto.response.ProductResponseDTO;
import com.example.SCM.dto.request.ProductRequestDTO;
import com.example.SCM.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper; // আপনার প্রজেক্টের জ্যাকসন লাইব্রেরি অনুযায়ী ইম্পোর্ট

import java.util.List;

@RestController
@RequestMapping("/api/products/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Frontend (Angular/React) থেকে কল করার সময় CORS এরর এড়াতে
public class ProductController {

    private final ProductService productService;

  // 1. Create New Product (POST)

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @RequestPart("productJson") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        // JSON String-কে ObjectMapper দিয়ে ProductRequestDTO-তে রূপান্তর করা হলো
        ObjectMapper mapper = new ObjectMapper();
        ProductRequestDTO dto = mapper.readValue(productJson, ProductRequestDTO.class);

        ProductResponseDTO response = productService.save(dto, image);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. Update Existing Product (PUT)

    @PutMapping("{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("productJson") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        ProductRequestDTO dto = mapper.readValue(productJson, ProductRequestDTO.class);

        ProductResponseDTO response = productService.update(id, dto, image);
        return ResponseEntity.ok(response);
    }

    // 3. Get All Products (GET)

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        List<ProductResponseDTO> list = productService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    // 4. Get Product By ID (GET)

    @GetMapping("{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return productService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //5. Delete Product (DELETE)

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok("Product deleted successfully!");
    }
}