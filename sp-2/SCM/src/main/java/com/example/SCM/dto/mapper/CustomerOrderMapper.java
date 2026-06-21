package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import com.example.SCM.dto.response.OrderLineItemResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.enumClass.ServiceType;
import com.example.SCM.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerOrderMapper {

    private final ProductRepository productRepository;

    public CustomerOrderResponseDTO toResponseDTO(CustomerOrder entity) {
        if (entity == null) return null;

        CustomerOrderResponseDTO dto = new CustomerOrderResponseDTO();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setItemSubtotal(entity.getItemSubtotal());
        dto.setWeight(entity.getWeight());
        if (entity.getServiceType() != null) dto.setServiceType(entity.getServiceType().name());
        dto.setCodAmount(entity.getCodAmount());
        dto.setDeliveryCharge(entity.getDeliveryCharge());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setCurrency(entity.getCurrency());
        if (entity.getStatus() != null) dto.setStatus(entity.getStatus().name());
        dto.setDeliveryAddress(entity.getDeliveryAddress());
        if (entity.getEstimatedDelivery() != null) dto.setEstimatedDelivery(entity.getEstimatedDelivery().toString());
        if (entity.getCreatedAt() != null) dto.setCreatedAt(entity.getCreatedAt().toString());

        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
            dto.setCustomerName(entity.getCustomer().getName());
        }

        if (entity.getLineItems() != null) {
            dto.setLineItems(entity.getLineItems().stream().map(item -> {
                OrderLineItemResponseDTO itemDto = new OrderLineItemResponseDTO();
                itemDto.setId(item.getId());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnitPrice(item.getUnitPrice());
                itemDto.setLineTotal(item.getLineTotal());
                itemDto.setItemWeightTotal(item.getItemWeightTotal());
                if (item.getProduct() != null) {
                    itemDto.setProductId(item.getProduct().getId());
                    itemDto.setProductName(item.getProduct().getName());
                    itemDto.setProductCode(item.getProduct().getProductCode());
                }
                return itemDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }

    public CustomerOrder toEntity(CustomerOrderRequestDTO dto, User customer) {
        if (dto == null) return null;

        CustomerOrder entity = new CustomerOrder();
        entity.setCustomer(customer);
        entity.setServiceType(dto.getServiceType() != null ? ServiceType.valueOf(dto.getServiceType().toUpperCase()) : ServiceType.STANDARD);
        entity.setCodAmount(dto.getCodAmount());
        if (dto.getCurrency() != null) entity.setCurrency(dto.getCurrency());
        entity.setDeliveryAddress(dto.getDeliveryAddress());
        if (dto.getEstimatedDelivery() != null) entity.setEstimatedDelivery(LocalDate.parse(dto.getEstimatedDelivery()));

        if (dto.getItems() != null) {
            dto.getItems().forEach(itemDto -> {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product node not found with ID: " + itemDto.getProductId()));

                OrderLineItem item = new OrderLineItem();
                item.setProduct(product);
                item.setQuantity(itemDto.getQuantity());
                item.setUnitPrice(product.getSellingPrice()); // 🔒 সিকিউর সেলিং প্রাইস লক
                item.setLineTotal(item.getQuantity() * item.getUnitPrice());
                item.setItemWeightTotal(product.getWeight() * itemDto.getQuantity()); // মোট আইটেম ওজন

                entity.addLineItem(item); // মাস্টার বাইন্ডিং হেল্পার মেথড কল
            });
        }
        return entity;
    }
}