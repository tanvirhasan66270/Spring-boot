package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.enumClass.ServiceType;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class CustomerOrderMapper {

    public CustomerOrderResponseDTO toResponseDTO(CustomerOrder entity) {
        if (entity == null) return null;

        CustomerOrderResponseDTO dto = new CustomerOrderResponseDTO();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setLineTotal(entity.getLineTotal());
        dto.setWeight(entity.getWeight());
        if (entity.getServiceType() != null) dto.setServiceType(entity.getServiceType().name());
        dto.setCodAmount(entity.getCodAmount());
        dto.setDeliveryCharge(entity.getDeliveryCharge());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setStatus(entity.getStatus());
        dto.setDeliveryAddress(entity.getDeliveryAddress());
        if (entity.getEstimatedDelivery() != null) dto.setEstimatedDelivery(entity.getEstimatedDelivery().toString());
        if (entity.getCreatedAt() != null) dto.setCreatedAt(entity.getCreatedAt().toString());

        // 👥 Customer Profile Mapping Safely
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
            dto.setCustomerName(entity.getCustomer().getName()); // ধরে নেওয়া হচ্ছে User এনটিটিতে name ফিল্ড আছে
            dto.setCustomerEmail(entity.getCustomer().getEmail());
        }

        // 📦 Product Details Mapping Safely
        if (entity.getProduct() != null) {
            dto.setProductId(entity.getProduct().getId());
            dto.setProductName(entity.getProduct().getName());   // ধরে নেওয়া হচ্ছে Product এনটিটিতে name ফিল্ড আছে
            dto.setProductCode(entity.getProduct().getProductCode());
        }

        return dto;
    }

    public CustomerOrder toEntity(CustomerOrderRequestDTO dto, User customer, Product product) {
        if (dto == null) return null;

        CustomerOrder entity = new CustomerOrder();
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setWeight(dto.getWeight());
        entity.setServiceType(dto.getServiceType() != null ? ServiceType.valueOf(dto.getServiceType().toUpperCase()) : ServiceType.STANDARD);
        if (dto.getCodAmount() > 0) entity.setCodAmount(dto.getCodAmount());
        if (dto.getCurrency() != null) entity.setCurrency(dto.getCurrency());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus().toUpperCase());
        entity.setDeliveryAddress(dto.getDeliveryAddress());
        if (dto.getEstimatedDelivery() != null) entity.setEstimatedDelivery(LocalDate.parse(dto.getEstimatedDelivery()));

        entity.setCustomer(customer);
        entity.setProduct(product);

        return entity;
    }

    public void updateEntity(CustomerOrderRequestDTO dto, CustomerOrder entity, User customer, Product product) {
        if (dto == null || entity == null) return;

        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setWeight(dto.getWeight());
        if (dto.getServiceType() != null) entity.setServiceType(ServiceType.valueOf(dto.getServiceType().toUpperCase()));
        entity.setCodAmount(dto.getCodAmount());
        if (dto.getCurrency() != null) entity.setCurrency(dto.getCurrency());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus().toUpperCase());
        if (dto.getDeliveryAddress() != null) entity.setDeliveryAddress(dto.getDeliveryAddress());
        if (dto.getEstimatedDelivery() != null) entity.setEstimatedDelivery(LocalDate.parse(dto.getEstimatedDelivery()));

        if (customer != null) entity.setCustomer(customer);
        if (product != null) entity.setProduct(product);
    }
}