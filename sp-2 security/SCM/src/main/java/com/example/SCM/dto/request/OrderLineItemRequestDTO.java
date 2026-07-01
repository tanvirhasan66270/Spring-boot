package com.example.SCM.dto.request;



import lombok.Data;



@Data

public class OrderLineItemRequestDTO {

    private Long id; // Update করার সময় প্রয়োজন হতে পারে, অন্যথায় অপশনাল

    private Long productId;

    private int quantity;

    private String remarks;

}