package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import com.example.SCM.dto.response.OrderLineItemResponseDTO; // 🔗 ইম্পোর্ট করা হলো
import com.example.SCM.entity.CustomerOrder;
import com.example.SCM.entity.OrderLineItem;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.User;
import com.example.SCM.enumClass.CustomerOrderStatus;
import com.example.SCM.enumClass.ServiceType;
import com.example.SCM.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerOrderMapper {

    private final ProductRepository productRepository;

    public CustomerOrder toEntity(CustomerOrderRequestDTO dto, User customer) {
        if (dto == null) return null;

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);

        if (customer != null) {
            order.setCustomerName(customer.getName());
            order.setCustomerEmail(customer.getEmail());
        }

        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setCodAmount(dto.getCodAmount());

        if (dto.getCurrency() != null && !dto.getCurrency().isBlank()) {
            order.setCurrency(dto.getCurrency());
        }
        if (dto.getServiceType() != null && !dto.getServiceType().isBlank()) {
            order.setServiceType(ServiceType.valueOf(dto.getServiceType().toUpperCase()));
        }
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            order.setStatus(CustomerOrderStatus.valueOf(dto.getStatus().toUpperCase()));
        }
        if (dto.getEstimatedDelivery() != null && !dto.getEstimatedDelivery().isBlank()) {
            order.setEstimatedDelivery(LocalDate.parse(dto.getEstimatedDelivery()));
        }

        if (dto.getItems() != null) {
            dto.getItems().forEach(itemDto -> {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product missing for ID: " + itemDto.getProductId()));

                OrderLineItem item = new OrderLineItem();
                item.setProduct(product);
                item.setQuantity(itemDto.getQuantity());

                // 💡 ফ্রন্টএন্ড থেকে unitPrice পাঠালে সেটা লক হবে, না পাঠালে প্রোডাক্ট টেবিল থেকে আসবে
                if (itemDto.getUnitPrice() > 0) {
                    item.setUnitPrice(itemDto.getUnitPrice());
                } else {
                    item.setUnitPrice(product.getSellingPrice());
                }

                item.setLineTotal(item.getQuantity() * item.getUnitPrice());
                item.setItemWeightTotal(product.getWeight() * itemDto.getQuantity());
                item.setRemarks(itemDto.getRemarks());

                order.addLineItem(item);
            });
        }

        return order;
    }

    public CustomerOrderResponseDTO toResponseDTO(CustomerOrder entity) {
        if (entity == null) return null;

        CustomerOrderResponseDTO dto = new CustomerOrderResponseDTO();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setCustomerId(entity.getCustomerId());

        dto.setCustomerName(entity.getCustomerName() != null ? entity.getCustomerName() :
                (entity.getCustomer() != null ? entity.getCustomer().getName() : "Walk-in Customer"));
        dto.setCustomerEmail(entity.getCustomerEmail() != null ? entity.getCustomerEmail() :
                (entity.getCustomer() != null ? entity.getCustomer().getEmail() : "no-email@scmlogistics.com"));

        dto.setItemSubtotal(entity.getItemSubtotal());
        dto.setWeight(entity.getWeight());
        dto.setServiceType(entity.getServiceType().name());
        dto.setCodAmount(entity.getCodAmount());
        dto.setDeliveryCharge(entity.getDeliveryCharge());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setStatus(entity.getStatus().name());
        dto.setDeliveryAddress(entity.getDeliveryAddress());
        dto.setEstimatedDelivery(entity.getEstimatedDelivery() != null ? entity.getEstimatedDelivery().toString() : null);
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);

        // 🎯 আপনার তৈরি করা ইন্ডিপেন্ডেন্ট OrderLineItemResponseDTO-তে রূপান্তর
        if (entity.getLineItems() != null) {
            List<OrderLineItemResponseDTO> itemDTOs = entity.getLineItems().stream()
                    .map(item -> {
                        OrderLineItemResponseDTO itemDto = new OrderLineItemResponseDTO();
                        itemDto.setId(item.getId());
                        itemDto.setProductId(item.getProduct().getId());
                        itemDto.setProductName(item.getProduct().getName());
                        itemDto.setProductCode(item.getProduct().getProductCode());
                        itemDto.setQuantity(item.getQuantity());
                        itemDto.setUnitPrice(item.getUnitPrice());
                        itemDto.setLineTotal(item.getLineTotal());
                        itemDto.setItemWeightTotal(item.getItemWeightTotal());
                        itemDto.setRemarks(item.getRemarks());
                        return itemDto;
                    }).collect(Collectors.toList());
            dto.setLineItems(itemDTOs);
        }

        return dto;
    }
}