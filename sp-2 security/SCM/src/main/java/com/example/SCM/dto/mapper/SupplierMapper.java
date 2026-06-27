package com.example.SCM.dto.mapper;

import com.example.SCM.dto.response.SupplierResponseDTO;
import com.example.SCM.dto.request.SupplierRequestDTO;

import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.Supplier;
import com.example.SCM.entity.User;
import com.example.SCM.role.Role;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    // Supplier Entity থেকে SupplierResponseDTO-তে রূপান্তর (Flattening Relation)

    public SupplierResponseDTO toResponseDTO(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        SupplierResponseDTO dto = new SupplierResponseDTO();

        // Supplier প্রোফাইল ফিল্ডস ম্যাপিং
        dto.setId(supplier.getId());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setAddress(supplier.getAddress());
        dto.setNidNumber(supplier.getNidNumber());
        dto.setPassportNumber(supplier.getPassportNumber());
        dto.setGender(supplier.getGender());


        dto.setDob(supplier.getDob() != null
                ? supplier.getDob().toString() : null);


        dto.setImage(supplier.getImage());
        dto.setRating(supplier.getRating());
        dto.setAverageLeadTimeDays(supplier.getAverageLeadTimeDays());
        dto.setActive(supplier.isActive());
        dto.setCreatedAt(supplier.getCreatedAt());
        dto.setUpdatedAt(supplier.getUpdatedAt());

        // Auth Account (User) থেকে ডেটা ম্যাপিং
        User user = supplier.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhoneNumber());
            dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        }

        // Location Hierarchy (PoliceStation -> District -> Division) ম্যাপিং
        PoliceStation policeStation = supplier.getPoliceStation();

        if (policeStation != null) {
            dto.setPoliceStationId(policeStation.getId());
            dto.setPoliceStationName(policeStation.getName());

            if (policeStation.getDistrict() != null) {
                dto.setDistrictName(policeStation.getDistrict().getName());

                if (policeStation.getDistrict().getDivision() != null) {
                    dto.setDivisionName(policeStation.getDistrict().getDivision().getName());
                }
            }
        }

        return dto;
    }

    // Request DTO থেকে প্রধান Auth User অ্যাকাউন্ট এনটিটি তৈরি

    public User toUserEntity(SupplierRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(dto.getPassword());
//        user.setConfirmPassword(dto.getPassword()); // official value set for password conformation
        user.setRole(Role.SUPPLIER); // রোল অটোমেটিক SUPPLIER হিসেবে সেট হবে

        return user;
    }

    // Request DTO থেকে মূল Supplier প্রোফাইল এনটিটি তৈরি (রwith relation objectলেশন অবজেক্টসহ)

    public Supplier toSupplierEntity(SupplierRequestDTO dto, User user, PoliceStation policeStation) {
        if (dto == null) {
            return null;
        }

        Supplier supplier = new Supplier();

        // DTO থেকে ইনপুট ফিল্ডস সেট করা
        supplier.setName(dto.getName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setNidNumber(dto.getNidNumber());
        supplier.setPassportNumber(dto.getPassportNumber());
        supplier.setGender(dto.getGender());

        if (dto.getDob() != null && !dto.getDob().trim().isEmpty()) {
            try {
                supplier.setDob(String.valueOf(java.sql.Date.valueOf(dto.getDob())));
            } catch (IllegalArgumentException e) {
                try {
                    supplier.setDob(String.valueOf(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dto.getDob())));
                } catch (Exception ex) {
                    // Ignore parsing failure
                }
            }
        }

        supplier.setImage(dto.getImage());
        supplier.setRating(dto.getRating());
        supplier.setAverageLeadTimeDays(dto.getAverageLeadTimeDays());

        // ফরেন কি/রিলেশন অবজেক্ট ইনজেক্ট করা
        supplier.setUser(user);
        supplier.setPoliceStation(policeStation);

        return supplier;
    }
}