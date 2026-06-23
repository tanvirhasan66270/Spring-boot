package com.example.SCM.dto.response;

import lombok.Data;

@Data
public class DeliveryTripResponseDTO {
    private Long id;
    private String sendBy;
    private String status;
    private String startedAt;
    private String completedAt;
    private String recipientSignature;
    private String deliveryPhotoUrl;
    private String customerAddress;
    private String vehicleInfo;
    private String destinationInfo;
    private String scheduleInfo;
    private String tripInfo;
    private String createdAt;
    private String updatedAt;

    //  Flattened Relations (Auto Fillup Match with Angular) ---
    private Long customerId;
    private String recipientName; // Maps to customerName from Customer table

    private Long driverId;
    private String driverName;
    private String driverEmail;

    private Long vehicleId;
    private String vehiclePlateNumber;
}