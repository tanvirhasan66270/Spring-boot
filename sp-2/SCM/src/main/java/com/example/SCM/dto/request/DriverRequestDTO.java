package com.example.SCM.dto.request;

import lombok.Data;
import java.util.Set;

@Data
public class DriverRequestDTO {
    private Long id;
    private String driverName;
    private String phone;
    private String address;
    private String nidNumber;
    private String gender;
    private String email;
    private String vehicleType;
    private String vehicleNumber;
    private String dob;
    private Double rating = 0.0;
    private Integer totalDeliveries = 0;
    private Double totalEarnings = 0.0;
    private Boolean active = true;
    private String image;

    // Auth Platform Security
    private String password;

    // Many-to-Many Relational Bindings
    private Set<Long> warehouseIds;
}