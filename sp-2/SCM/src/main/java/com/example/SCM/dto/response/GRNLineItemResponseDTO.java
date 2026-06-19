package com.example.SCM.dto.response;

import lombok.Data;

@Data
public class GRNLineItemResponseDTO {
    private Long id;
    private int quantityOrdered;
    private int quantityReceived;

    // --- 📦 Flattened GRN Details ---
    private Long grnId;
    private String grnNumber;       // ট্র্যাকিং ডিসপ্লের জন্য

    // --- 🧵 Flattened Product Details ---
    private Long productId;
    private String productName;     // প্রোডাক্টের নাম সরাসরি গ্রিডে দেখানোর জন্য
}