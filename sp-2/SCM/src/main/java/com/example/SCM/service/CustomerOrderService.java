package com.example.SCM.service;

import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import java.util.List;
import java.util.Optional;

public interface CustomerOrderService {
    CustomerOrderResponseDTO save(CustomerOrderRequestDTO dto);
    CustomerOrderResponseDTO update(Long id, CustomerOrderRequestDTO dto);
    List<CustomerOrderResponseDTO> findAll();
    Optional<CustomerOrderResponseDTO> getById(Long id);
    void updateStatus(Long id, String status);
    void delete(Long id);
}