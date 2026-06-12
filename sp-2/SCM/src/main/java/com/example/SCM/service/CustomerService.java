package com.example.SCM.service;

import com.example.SCM.dto.response.CustomerResponseDTO;
import com.example.SCM.dto.request.CustomerRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public interface CustomerService {
    CustomerResponseDTO save(CustomerRequestDTO dto, MultipartFile file);
    List<CustomerResponseDTO> findAll();
    Optional<CustomerResponseDTO> getById(Long id);
    void delete(Long id);
}
