package com.example.SCM.service;

import com.example.SCM.dto.DivisionDTO;
import com.example.SCM.entity.Division;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface DivisionService {
    Division save(Division d);
    List<Division> findAll();
    Optional<Division> getById(Long id);
    void delete(Long id);

    List<DivisionDTO> getDivisionsByCountryId(Long countryId);

    List<DivisionDTO> getDivisionsByCountryName(String countryName);

}
