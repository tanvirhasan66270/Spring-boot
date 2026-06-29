package com.example.SCM.dto.response;

import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String address;
    private String gender;
    private String dob;
    private String nidNumber;
    private String image;
    private String createdAt;

    // --- Location Nodes ---
    private Long policeStationId;
    private String policeStationName;
    private String districtName;
    private String divisionName;
}