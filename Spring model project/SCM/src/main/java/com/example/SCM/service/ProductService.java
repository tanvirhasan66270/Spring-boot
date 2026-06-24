package com.example.SCM.service;

import com.example.SCM.dto.response.ProductResponseDTO;
import com.example.SCM.dto.request.ProductRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {

    ProductResponseDTO save(ProductRequestDTO dto, MultipartFile image);
    ProductResponseDTO update(Long id, ProductRequestDTO dto ,MultipartFile image);
    List<ProductResponseDTO> findAll();
    Optional<ProductResponseDTO> getById(Long id);
    void delete(Long id);
}
