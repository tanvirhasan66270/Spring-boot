package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class CustomerOrderRequestDTO {
    private Long id;
    private Long customerId;   // FK → User (Customer)
    private Long productId;    // FK → Product
    private int quantity;
    private double unitPrice;
    private double weight;
    private String serviceType; // STANDARD, EXPRESS, etc.
    private double codAmount;
    private String currency;    // optional, default 'USD'
    private String status;      // optional, default 'PENDING'
    private String deliveryAddress;
    private String estimatedDelivery; // ISO Format (yyyy-MM-dd)
}