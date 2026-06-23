package com.example.SCM.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CustomerOrderRequestDTO {


    private Long customerId;
    private String deliveryAddress;
    private String estimatedDelivery;
    private String serviceType;
    private double codAmount;


    private String status;

    private List<OrderLineItemRequestDTO> items;
}