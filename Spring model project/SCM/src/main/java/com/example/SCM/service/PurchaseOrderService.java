package com.example.SCM.service;



import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PurchaseOrderService {
    PurchaseOrderResponseDTO save(PurchaseOrderRequestDTO dto);
    PurchaseOrderResponseDTO update(Long id, PurchaseOrderRequestDTO dto);
    List<PurchaseOrderResponseDTO> findAll();
    Optional<PurchaseOrderResponseDTO> getById(Long id);
    void delete(Long id);

}
