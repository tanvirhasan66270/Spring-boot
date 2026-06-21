package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.OrderLineItemRequestDTO;
import com.example.SCM.dto.response.OrderLineItemResponseDTO;
import com.example.SCM.entity.OrderLineItem;
import com.example.SCM.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class OrderLineItemMapper {

    public OrderLineItemResponseDTO toResponseDTO(OrderLineItem entity) {
        if (entity == null) return null;

        OrderLineItemResponseDTO dto = new OrderLineItemResponseDTO();
        dto.setId(entity.getId());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setLineTotal(entity.getLineTotal());
        dto.setItemWeightTotal(entity.getItemWeightTotal());
        dto.setRemarks(entity.getRemarks());

        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getId());
            dto.setProductName(entity.getProduct().getName());
            dto.setProductCode(entity.getProduct().getProductCode());
        }
        return dto;
    }

    public OrderLineItem toEntity(OrderLineItemRequestDTO dto, Product product) {
        if (dto == null) return null;

        OrderLineItem entity = new OrderLineItem();
        entity.setId(dto.getId());
        entity.setProduct(product);
        entity.setQuantity(dto.getQuantity());
        // যদি ডিটিও-তে প্রাইস না থাকে তবে প্রোডাক্ট মাস্টার টেবিল থেকে সেলিং প্রাইস লক হবে
        entity.setUnitPrice(dto.getUnitPrice() > 0 ? dto.getUnitPrice() : product.getSellingPrice());
        entity.setRemarks(dto.getRemarks());

        // 💡 নোট: lineTotal এবং itemWeightTotal এনটিটির @PrePersist/@PreUpdate থেকে অটো ক্যালকুলেট হবে
        return entity;
    }
}