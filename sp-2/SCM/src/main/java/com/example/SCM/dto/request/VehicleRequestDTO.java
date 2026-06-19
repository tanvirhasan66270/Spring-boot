package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class VehicleRequestDTO {
    private Long id;
    private String plateNumber;
    private String type;         // এঙ্গুলার থেকে আসা "TRUCK", "VAN" ইত্যাদি স্ট্রিং
    private Double capacity;
    private String status;       // এঙ্গুলার থেকে আসা "AVAILABLE" ইত্যাদি স্ট্রিং
    private String lastServiceDate; // "YYYY-MM-DD" ফরম্যাটে আসবে
    private Integer fuelLevel;
    private Long driverId;       // FK Driver ID (Optional)
}