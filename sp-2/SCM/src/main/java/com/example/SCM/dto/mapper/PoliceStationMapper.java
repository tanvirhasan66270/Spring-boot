package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.PoliceStationRequestDTO;
import com.example.SCM.dto.response.PoliceStationResponseDTO;
import com.example.SCM.entity.District;
import com.example.SCM.entity.PoliceStation;
import org.springframework.stereotype.Component;

@Component
public class PoliceStationMapper {

    /**
     * Entity -> Response DTO (Object Graph Flattening)
     */
    public PoliceStationResponseDTO toResponseDTO(PoliceStation entity) {
        if (entity == null) return null;

        PoliceStationResponseDTO dto = new PoliceStationResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setNameBn(entity.getNameBn());
        dto.setPostalCode(entity.getPostalCode());
        dto.setActive(entity.getActive());

        // Full Location Chain Hierarchy Resolution
        if (entity.getDistrict() != null) {
            District dist = entity.getDistrict();
            dto.setDistrictId(dist.getId());
            dto.setDistrictName(dist.getName());

            if (dist.getDivision() != null) {
                dto.setDivisionName(dist.getDivision().getName());
                if (dist.getDivision().getCountry() != null) {
                    dto.setCountryName(dist.getDivision().getCountry().getName());
                }
            }
        }
        return dto;
    }

    /**
     * Request DTO -> Entity
     */
    public PoliceStation toEntity(PoliceStationRequestDTO dto, District district) {
        if (dto == null) return null;

        PoliceStation entity = new PoliceStation();
        entity.setName(dto.getName());
        entity.setNameBn(dto.getNameBn());
        entity.setPostalCode(dto.getPostalCode());
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
        entity.setDistrict(district);
        return entity;
    }

    /**
     * Update Existing Entity
     */
    public void updateEntity(PoliceStationRequestDTO dto, PoliceStation entity, District district) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setNameBn(dto.getNameBn());
        entity.setPostalCode(dto.getPostalCode());
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
        if (district != null) {
            entity.setDistrict(district);
        }
    }
}