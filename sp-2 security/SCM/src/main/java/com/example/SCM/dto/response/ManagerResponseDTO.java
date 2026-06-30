package com.example.SCM.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ManagerResponseDTO {
    private Long id;
    private String address;
    private String nidNumber;
    private String passportNumber;
    private LocalDate dob;
    private String gender;
    private String image;
    private LocalDate joiningDate;
    private String designation;
    private String language;
    private Long policeStationId;
    private String policeStationName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Flattened Auth User Details
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;
}