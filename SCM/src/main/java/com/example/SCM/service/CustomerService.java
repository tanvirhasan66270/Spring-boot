package com.example.SCM.service;
import com.example.SCM.dto.request.CustomerRequestDTO;
import com.example.SCM.dto.Response.CustomerResponseDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public interface CustomerService {

    CustomerResponseDTO save(CustomerRequestDTO dto, MultipartFile file);

    List<CustomerResponseDTO> getAll();

    @Transactional(readOnly = true)
    List<CustomerResponseDTO> findAll();

    CustomerResponseDTO getById(Long id);

    CustomerResponseDTO update(Long id, CustomerRequestDTO dto, MultipartFile image);

    void delete(Long id);
}
