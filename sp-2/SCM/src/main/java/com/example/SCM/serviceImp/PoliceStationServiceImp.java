package com.example.SCM.serviceImp;

import com.example.SCM.dto.response.PoliceStationResponseDTO;
import com.example.SCM.entity.District;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.repository.DistrictRepository;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.service.PoliceStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PoliceStationServiceImp implements PoliceStationService {
  private final PoliceStationRepository policeStationRepository;
  private final DistrictRepository districtRepository;


    @Override
    public PoliceStation save(PoliceStation p) {

        Long districtId=p.getDistrict().getId();
        District d=districtRepository.findById(districtId)
                .orElseThrow(()-> new RuntimeException("District not found with this id"));
        p.setDistrict(d);

        return policeStationRepository.save(p);
    }

    @Override
    public List<PoliceStation> findAll() {
        return policeStationRepository.findAll();
    }

    @Override
    public Optional<PoliceStation> getById(Long id) {
        return policeStationRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        policeStationRepository.deleteById(id);

    }

    @Override
    public List<PoliceStationResponseDTO> findByDistrictId(Long districtId) {
        List<PoliceStation> list= policeStationRepository.findByDistrictId(districtId);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PoliceStationResponseDTO> findByDistrictName(String districtName) {
        List<PoliceStation> list= policeStationRepository.findByDistrictName(districtName);
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }



    private PoliceStationResponseDTO convertToDTO(PoliceStation p) {

        return new PoliceStationResponseDTO(
                p.getId(),
                p.getName(),
                p.getDistrict().getId(),
                p.getDistrict().getName(),
                p.getDistrict().getDivision().getId(),
                p.getDistrict().getDivision().getName(),
                p.getDistrict().getDivision().getCountry().getName(),
                p.getDistrict().getDivision().getCountry().getCode(),
                p.getDistrict().getDivision().getCountry().getId()
        );
    }

}
