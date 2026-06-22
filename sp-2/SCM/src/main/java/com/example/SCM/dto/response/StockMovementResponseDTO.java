package com.example.SCM.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockMovementResponseDTO {
    private Long id;
    private Long productId;
    private String productName;     // 💡 TS ইন্টারফেসের সাথে অটো-ফিলআপ সিঙ্কড
    private Long warehouseId;
    private String warehouseName;   // 💡 TS ইন্টারফেসের সাথে অটো-ফিলআপ সিঙ্কড
    private String sendWarehouse;   // Accepts null
    private String movementType;
    private int quantity;
    private String referenceId;
    private Long performedBy;
    private LocalDateTime movedAt;
    private String remarks;
}