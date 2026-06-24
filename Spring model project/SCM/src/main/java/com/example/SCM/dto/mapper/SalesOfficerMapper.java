package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.SalesOfficerRequestDTO;
import com.example.SCM.dto.response.SalesOfficerResponseDTO;
import com.example.SCM.entity.SalesOfficer;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;
import com.example.SCM.enumClass.GenderStatus;
import com.example.SCM.enumClass.LanguageStatus;
import com.example.SCM.role.Role;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class SalesOfficerMapper {

    public User toUserEntity(SalesOfficerRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(dto.getPassword());

        //  আপনার Role এনামের সাথে সিঙ্ক। (যদি এনামে সরাসরি SALES_OFFICER না থাকে, তবে ব্যবসার লজিক অনুযায়ী ADMIN/MANAGER/LOGISTICS_OFFICER ম্যাপ করতে পারেন)
        try {
            user.setRole(Role.valueOf("SALES_OFFICER"));
        } catch (IllegalArgumentException e) {
            user.setRole(Role.MANAGER); // Fallback security setup
        }
        return user;
    }

    public SalesOfficer toOfficerEntity(SalesOfficerRequestDTO dto, User user, PoliceStation policeStation) {
        SalesOfficer officer = new SalesOfficer();
        officer.setUser(user);
        officer.setAddress(dto.getAddress());
        officer.setNidNumber(dto.getNidNumber());
        officer.setDesignation(dto.getDesignation());
        officer.setActive(dto.isActive());

        if (dto.getDob() != null && !dto.getDob().isBlank()) {
            officer.setDob(LocalDate.parse(dto.getDob()));
        }
        if (dto.getJoiningDate() != null && !dto.getJoiningDate().isBlank()) {
            officer.setJoiningDate(LocalDate.parse(dto.getJoiningDate()));
        }
        if (dto.getGender() != null) {
            officer.setGender(GenderStatus.valueOf(dto.getGender().toUpperCase()));
        }
        if (dto.getLanguage() != null) {
            officer.setLanguage(LanguageStatus.valueOf(dto.getLanguage().toUpperCase()));
        }

        officer.setPoliceStation(policeStation);
        return officer;
    }

    public SalesOfficerResponseDTO toResponseDTO(SalesOfficer entity) {
        if (entity == null) return null;

        SalesOfficerResponseDTO dto = new SalesOfficerResponseDTO();
        dto.setId(entity.getId());
        dto.setAddress(entity.getAddress());
        dto.setNidNumber(entity.getNidNumber());
        dto.setDob(entity.getDob());
        dto.setGender(entity.getGender() != null ? entity.getGender().name() : null);
        dto.setImage(entity.getImage());
        dto.setActive(entity.isActive());
        dto.setJoiningDate(entity.getJoiningDate());
        dto.setDesignation(entity.getDesignation());
        dto.setLanguage(entity.getLanguage() != null ? entity.getLanguage().name() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getPoliceStation() != null) {
            dto.setPoliceStationId(entity.getPoliceStation().getId());
            dto.setPoliceStationName(entity.getPoliceStation().getName());
        }

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setName(entity.getUser().getName());
            dto.setEmail(entity.getUser().getEmail());
            dto.setPhone(entity.getUser().getPhoneNumber());
            if (entity.getUser().getRole() != null) {
                dto.setRole(entity.getUser().getRole().name());
            }
        }
        return dto;
    }
}