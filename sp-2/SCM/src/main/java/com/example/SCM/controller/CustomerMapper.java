package com.example.SCM.controller;

import com.example.SCM.dto.request.CustomerRequestDTO;
import com.example.SCM.dto.response.CustomerResponseDTO;
import com.example.SCM.entity.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

/**
 * CustomerMapper
 *
 * Responsible for converting:
 * 1. CustomerRequestDTO -> Customer Entity Updates
 * 2. Customer Entity -> CustomerResponseDTO
 *
 * This class helps separate API models (DTOs)
 * from database entities.
 */
@Component
public class CustomerMapper {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Convert Customer Entity to CustomerResponseDTO.
     *
     * Used when sending customer information back to the client.
     *
     * @param entity Customer entity from database
     * @return CustomerResponseDTO
     */
    public CustomerResponseDTO toResponseDTO(Customer entity) {
        if (entity == null) return null;

        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(entity.getId());
        dto.setAddress(entity.getAddress());
        dto.setGender(entity.getGender());
        dto.setNidNumber(entity.getNidNumber());
        dto.setImage(entity.getImage());
        if (entity.getDob() != null) dto.setDob(dateFormat.format(entity.getDob()));
        if (entity.getCreatedAt() != null) dto.setCreatedAt(entity.getCreatedAt().toString());

        /*
         * User (Source of truth) mapping
         */
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setName(entity.getUser().getName());
            dto.setEmail(entity.getUser().getEmail());
            dto.setPhone(entity.getUser().getPhoneNumber());
            dto.setRole(entity.getUser().getRole() != null ? entity.getUser().getRole().toString() : null);
        }

        /*
         * Location Hierarchy flattening
         */
        if (entity.getPoliceStation() != null) {
            PoliceStation ps = entity.getPoliceStation();
            dto.setPoliceStationId(ps.getId());
            dto.setPoliceStationName(ps.getName());
            if (ps.getDistrict() != null) {
                dto.setDistrictName(ps.getDistrict().getName());
                if (ps.getDistrict().getDivision() != null) {
                    dto.setDivisionName(ps.getDistrict().getDivision().getName());
                }
            }
        }
        return dto;
    }

    /**
     * Update existing Customer and User Entity with request data.
     *
     * @param dto Incoming request data containing updates
     * @param entity Existing entity instance to be modified
     * @param policeStation Assigned corporate location station node
     */
    public void updateEntity(CustomerRequestDTO dto, Customer entity, PoliceStation policeStation) {
        if (dto == null || entity == null) return;

        // ১. কাস্টমার টেবিল সিঙ্ক
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getGender() != null) entity.setGender(dto.getGender());
        if (dto.getNidNumber() != null) entity.setNidNumber(dto.getNidNumber());
        if (dto.getImage() != null) entity.setImage(dto.getImage());

        if (dto.getDob() != null) {
            try {
                entity.setDob(dateFormat.parse(dto.getDob()));
            } catch (Exception e) {
                System.err.println("DOB sync fail: " + e.getMessage());
            }
        }
        if (policeStation != null) entity.setPoliceStation(policeStation);

        // ইউজার টেবিল (Source of truth) সিঙ্ক
        if (entity.getUser() != null) {
            User user = entity.getUser();
            if (dto.getName() != null) user.setName(dto.getName());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            if (dto.getPhone() != null) user.setPhoneNumber(dto.getPhone());
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                user.setPassword(dto.getPassword());
            }
        }
    }

}