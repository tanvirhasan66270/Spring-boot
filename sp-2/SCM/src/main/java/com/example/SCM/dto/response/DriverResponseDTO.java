package com.example.SCM.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class DriverResponseDTO {
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
    private Double rating;
    private Integer totalDeliveries;
    private Double totalEarnings;
    private Boolean active;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;

    // 🎯 আপনার রিকোয়ারমেন্ট অনুযায়ী এই ফিল্ডে User থেকে আসা Role সেট হবে
    private String role;

    private Set<String> warehouseNames;
}