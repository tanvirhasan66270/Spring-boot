package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.StockMovementRequestDTO;
import com.example.SCM.dto.response.StockMovementResponseDTO;
import com.example.SCM.entity.StockMovement;
import com.example.SCM.enumClass.StockMovementType;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

    public StockMovement toEntity(StockMovementRequestDTO dto) {
        if (dto == null) return null;

        StockMovement entity = new StockMovement();
        entity.setProductId(dto.getProductId());
        entity.setWarehouseId(dto.getWarehouseId());
        entity.setSendWarehouse(dto.getSendWarehouse()); // Handles null perfectly
        entity.setQuantity(dto.getQuantity());
        entity.setReferenceId(dto.getReferenceId());
        entity.setPerformedBy(dto.getPerformedBy());
        entity.setRemarks(dto.getRemarks());

        if (dto.getMovementType() != null) {
            entity.setMovementType(StockMovementType.valueOf(dto.getMovementType().toUpperCase()));
        }

        return entity;
    }

    public StockMovementResponseDTO toResponseDTO(StockMovement entity, String fetchedProductName, String fetchedWarehouseName) {
        if (entity == null) return null;

        StockMovementResponseDTO dto = new StockMovementResponseDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId());
        dto.setProductName(fetchedProductName);         // auto fillup
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setWarehouseName(fetchedWarehouseName);     // auto fillup
        dto.setSendWarehouse(entity.getSendWarehouse());
        dto.setQuantity(entity.getQuantity());
        dto.setReferenceId(entity.getReferenceId());
        dto.setPerformedBy(entity.getPerformedBy());
        dto.setMovedAt(entity.getMovedAt());
        dto.setRemarks(entity.getRemarks());

        if (entity.getMovementType() != null) {
            dto.setMovementType(entity.getMovementType().name());
        }

        return dto;
    }
}