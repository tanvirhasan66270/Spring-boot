package com.example.SCM.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupplierResponseDTO {
    private Long id;

    // From User
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;

    // Supplier profile

    private String contactPerson;
    private String address;
    private String nidNumber;
    private String passportNumber;
    private String gender;
    private String dob;
    private String image;
    private double rating;
    private int averageLeadTimeDays;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Location
    private Long policeStationId;
    private String policeStationName;
    private String districtName;
    private String divisionName;
}
