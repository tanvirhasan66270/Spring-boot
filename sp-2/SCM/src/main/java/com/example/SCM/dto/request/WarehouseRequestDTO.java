package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class WarehouseRequestDTO {
    private String name;
    private String location;
    private double capacity;
    private Long managerId;
    private boolean isActive;

    // পুলিশ স্টেশনের জন্য ফরেন-কি রেফারেন্স আইডি
    private Long policeStationId;

}
