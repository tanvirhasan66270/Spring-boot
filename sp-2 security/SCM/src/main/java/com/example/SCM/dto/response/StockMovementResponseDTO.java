package com.example.SCM.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockMovementResponseDTO {
    private Long id;

    // Product Details Flattened
    private Long productId;
    private String productName;

    // Target/Destination Warehouse Details Flattened
    private Long warehouseId;
    private String warehouseName;

    // Source Warehouse Details (Only for TRANSFER type)
    private Long sourceWarehouseId; //  ফিক্স: আইডি ট্র্যাকিং সেফটি
    private String sourceWarehouseName; //  ফিক্স: ফ্রন্টএন্ড টেবিলে সোর্স গুদামের নাম দেখানোর জন্য

    private String movementType;
    private int quantity;
    private String referenceId;

    // Performed By Personnel Flattened
    private Long performedBy;
    private String performedByName; //  ফিক্স: কোন অফিসার এটি এন্ট্রি করেছে তার নাম সরাসরি UI-তে পুশ করার জন্য

    private LocalDateTime movedAt;
    private String remarks;
}