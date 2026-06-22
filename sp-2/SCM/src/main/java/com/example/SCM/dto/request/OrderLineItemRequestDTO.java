package com.example.SCM.dto.request;



import lombok.Data;



@Data

public class OrderLineItemRequestDTO {

    private Long id; // Update করার সময় প্রয়োজন হতে পারে, অন্যথায় অপশনাল

    private Long productId;

    private int quantity;

    private double unitPrice; // অপশনাল, ফ্রন্টএন্ড থেকে না পাঠালে প্রোডাক্ট মাস্টার থেকে অটো লক হবে

    private String remarks;

}