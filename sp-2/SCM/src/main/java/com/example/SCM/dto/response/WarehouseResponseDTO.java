package com.example.SCM.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WarehouseResponseDTO {

    private Long id;
    private String name;
    private String location;
    private double capacity;
    private Long managerId;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Location/PoliceStation Details (Data Flattening)
    private Long policeStationId;
    private String policeStationName;
    private String districtName;  // ফ্রন্টএন্ড টেবিল বা ফিল্টারিংয়ে সরাসরি জেলা দেখানোর জন্য
    private String divisionName;  // সরাসরি বিভাগ দেখানোর জন্য
}
