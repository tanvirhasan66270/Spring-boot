package com.example.SCM.service;

import com.example.SCM.dto.request.WarehouseRequestDTO;
import com.example.SCM.dto.response.WarehouseResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface WarehouseService {

    WarehouseResponseDTO save(WarehouseRequestDTO dto);
    WarehouseResponseDTO update(Long id , WarehouseRequestDTO dto);
    List<WarehouseResponseDTO> findAll();
    Optional<WarehouseResponseDTO> getById(Long id);
    void delete(Long id);

}
