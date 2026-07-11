package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class MessageRequestDTO {
    private String recipientId; // Internal staff-দের জন্য লাগবে, Driver/Customer/Supplier-দের জন্য null থাকবে
    private String subject;
    private String body;
    private String priority;
}