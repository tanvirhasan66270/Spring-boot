package com.example.SCM.serviceImp;

import com.example.SCM.dto.response.DistrictResponseDTO;
import com.example.SCM.entity.District;
import com.example.SCM.entity.Division;
import com.example.SCM.repository.DistrictRepository;
import com.example.SCM.repository.DivisionRepository;
import com.example.SCM.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictServiceImp implements DistrictService {

    private final DistrictRepository districtRepository;

    private final DivisionRepository divisionRepository;



    @Override
    public District save(District d) {
        Long divisionId= d.getDivision().getId();
        Division dv =divisionRepository.findById(divisionId)
                .orElseThrow(()-> new RuntimeException("Division Not found With this id"));

        d.setDivision(dv);
        return districtRepository.save(d);
    }


    @Override
    public List<District> findAll() {
        return districtRepository.findAll();
    }

    @Override
    public Optional<District> getById(Long id) {
        return districtRepository.findById(id);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<DistrictResponseDTO> findByDivisionId(Long divisionId) {
        List<District> list =  districtRepository.findByDivisionId(divisionId);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<DistrictResponseDTO> findByDivisionName(String divisionName) {
        List<District> list =  districtRepository.findByDivisionName(divisionName);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private DistrictResponseDTO convertToDTO(District district) {

        return new DistrictResponseDTO(
                district.getId(),
                district.getName(),
                district.getDivision().getId(),
                district.getDivision().getName(),
                district.getDivision().getCountry().getName(),
                district.getDivision().getCountry().getCode(),
                district.getDivision().getCountry().getId()
        );
    }

}
