package com.example.SCM.service;

import com.example.SCM.dto.response.PoliceStationResponseDTO;
import com.example.SCM.entity.PoliceStation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PoliceStationService {

    PoliceStation save(PoliceStation p);
    List<PoliceStation> findAll();
    Optional<PoliceStation> getById(Long id);
    void delete(Long id);

    List<PoliceStationResponseDTO>findByDistrictId(Long districtId);
    List<PoliceStationResponseDTO>findByDistrictName(String districtName);
}
