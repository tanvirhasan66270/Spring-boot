package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class PoliceStationRequestDTO {
    private Long id;
    private String name;
    private String nameBn;
    private String postalCode;
    private Boolean active;
    private Long districtId; // 🔗 প্যারেন্ট ডিস্ট্রিক্ট আইডি
}