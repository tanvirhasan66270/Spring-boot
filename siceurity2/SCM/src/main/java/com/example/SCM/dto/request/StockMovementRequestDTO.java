package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class StockMovementRequestDTO {
    private Long productId;
    private Long warehouseId;
    private String sendWarehouse; // Can be null
    private String movementType;  // INWARD, OUTWARD, TRANSFER, ADJUSTMENT
    private int quantity;
    private String referenceId;
    private Long performedBy;
    private String remarks;
}