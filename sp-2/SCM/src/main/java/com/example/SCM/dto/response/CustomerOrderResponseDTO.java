package com.example.SCM.dto.response;

import lombok.Data;

@Data
public class CustomerOrderResponseDTO {
    private Long id;
    private String orderNumber;
    private int quantity;
    private double unitPrice;
    private double lineTotal;
    private double weight;
    private String serviceType;
    private double codAmount;
    private double deliveryCharge;
    private double totalAmount;
    private String currency;
    private String status;
    private String deliveryAddress;
    private String estimatedDelivery;
    private String createdAt;

    // --- 👥 Flattened Relations for UI ---
    private Long customerId;
    private String customerName;
    private String customerEmail;

    private Long productId;
    private String productName;
    private String productCode;
}