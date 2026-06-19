package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class WarehouseRequestDTO {
    private String name;
    private String email; // ➕ নতুন যুক্ত করা ফিল্ড
    private String location;
    private double capacity;
    private Long managerId;
    private boolean isActive;
    private Long policeStationId;
}