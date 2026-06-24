package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class DeliveryTripRequestDTO {
    private Long id;
    private String sendBy;
    private Long customerId;
    private Long driverId;
    private String status;
    private String recipientSignature;
    private String deliveryPhotoUrl;
    private String customerAddress;
    private Long vehicleId;
    private String vehicleInfo;
    private String destinationInfo;
    private String scheduleInfo;
    private String tripInfo;
}