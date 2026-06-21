package com.example.SCM.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CustomerOrderRequestDTO {
    private Long customerId;
    private String serviceType; // STANDARD, EXPRESS, OVERNIGHT, SAME_DAY
    private String status;
    private double codAmount;
    private String currency;
    private String deliveryAddress;
    private String estimatedDelivery; // Format: "yyyy-MM-dd"
    private List<OrderLineItemRequestDTO> items; // কার্টের প্রোডাক্ট লিস্ট
}