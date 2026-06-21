package com.example.SCM.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class CustomerOrderResponseDTO {
    private Long id;
    private String orderNumber;
    private double itemSubtotal;
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
    private Long customerId;
    private String customerName;
    private List<OrderLineItemResponseDTO> lineItems;
}