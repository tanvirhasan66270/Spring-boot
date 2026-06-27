package com.example.SCM.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class CustomerOrderResponseDTO {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private double itemSubtotal;
    private double weight;
    private String serviceType;
    private double codAmount;
    private double deliveryCharge;
    private double totalAmount;
    private String paidAmount;
    private String currency;
    private String status;
    private String deliveryAddress;
    private String estimatedDelivery;
    private String createdAt;

    private List<OrderLineItemResponseDTO> lineItems;
}