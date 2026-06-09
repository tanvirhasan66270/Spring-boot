package com.example.SCM.service;


import com.example.SCM.dto.Response.DistrictResponseDTO;
import com.example.SCM.entity.District;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface DistrictService {
    District save(District d);
    List<District> findAll();
    Optional<District> getById(Long id);
    void delete(Long id);

    List<DistrictResponseDTO> findByDivisionId(Long  divisionId);

    List<DistrictResponseDTO> findByDivisionName(String divisionName);
}
