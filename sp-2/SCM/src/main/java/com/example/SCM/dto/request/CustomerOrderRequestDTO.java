package com.example.SCM.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CustomerOrderRequestDTO {

    // ── Master Order Metadata ──
    private Long customerId;
    private String deliveryAddress;
    private String estimatedDelivery; // জাভাতে LocalDate.parse() করার জন্য 'YYYY-MM-DD' ফরম্যাটে পাঠাতে হবে
    private String serviceType;       // STANDARD, EXPRESS, etc.
    private double codAmount;

    // ── Optional Initial States ──
    private String currency;          // অপশনাল, ডিফল্ট "BDT"
    private String status;            // অপশনাল, ডিফল্ট "PENDING"

    // ── Child Cart Items Matrix ──
    // 🎯 আপনার তৈরি করা ইন্ডিপেন্ডেন্ট OrderLineItemRequestDTO ক্লাসটি এখানে লিস্ট আকারে লিঙ্ক করা হলো
    private List<OrderLineItemRequestDTO> items;
}