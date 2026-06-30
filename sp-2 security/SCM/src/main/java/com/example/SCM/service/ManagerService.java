package com.example.SCM.service;

import com.example.SCM.dto.request.ManagerRequestDTO;
import com.example.SCM.dto.response.ManagerResponseDTO;
import com.example.SCM.entity.Manager;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface ManagerService {
    ManagerResponseDTO save(ManagerRequestDTO dto, MultipartFile file);
    ManagerResponseDTO update(Long id, ManagerRequestDTO dto, MultipartFile file);
    List<ManagerResponseDTO> findAll();
    Optional<ManagerResponseDTO> getById(Long id);
    void delete(Long id);

    }