package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import com.example.SCM.dto.response.OrderLineItemResponseDTO;
import com.example.SCM.entity.CustomerOrder;
import com.example.SCM.entity.OrderLineItem;
import com.example.SCM.entity.User;
import com.example.SCM.enumClass.CustomerOrderStatus;
import com.example.SCM.enumClass.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerOrderMapper {

    // 💡 আপনার তৈরি করা ইন্ডিপেন্ডেন্ট চাইল্ড ম্যাপার ইনজেক্ট করা হলো
    private final OrderLineItemMapper lineItemMapper;

    /**
     * 📥 Request DTO থেকে Master Entity-তে কনভার্ট (SRP Optimized)
     */
    public CustomerOrder toEntity(CustomerOrderRequestDTO dto, User customer) {
        if (dto == null) return null;

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);

        // ১. সেফটি গেটওয়ে: ইউজারের প্রোফাইল থেকে কারেন্ট ডেটা অর্ডারে ক্যাশ করা
        if (customer != null) {
            order.setCustomerName(customer.getName());
            order.setCustomerEmail(customer.getEmail());
        }

        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setCodAmount(dto.getCodAmount());

        // ২. মেটাডাটা ও এনাম পার্সিং
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

        // ৩. ডেডিকেটেড চাইল্ড ম্যাপার নোড ব্যবহার করে কার্ট আইটেম প্রসেসিং
        if (dto.getItems() != null) {
            dto.getItems().forEach(itemDto -> {
                // সরাসরি চাইল্ড ম্যাপার থেকে এনটিটি তৈরি করে মাস্টারে বাইন্ড করা হচ্ছে
                OrderLineItem item = lineItemMapper.toEntity(itemDto);
                if (item != null) {
                    order.addLineItem(item); // Bidirectional Helper Method কল
                }
            });
        }

        return order;
    }

    /**
     * 📤 Entity থেকে Response DTO-তে কনভার্ট (Clean Output Schema)
     */
    public CustomerOrderResponseDTO toResponseDTO(CustomerOrder entity) {
        if (entity == null) return null;

        CustomerOrderResponseDTO dto = new CustomerOrderResponseDTO();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setCustomerId(entity.getCustomerId());

        // ওল্ড রিলেশনশিপ ক্যাশ ট্র্যাপ এড়াতে সরাসরি অর্ডারের কলাম নোড থেকে রিড করা হচ্ছে
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

        // ৪. চাইল্ড ম্যাপার ব্যবহার করে রেসপন্স ম্যাট্রিক্স তৈরি
        if (entity.getLineItems() != null) {
            List<OrderLineItemResponseDTO> itemDTOs = entity.getLineItems().stream()
                    .map(lineItemMapper::toResponseDTO) // ওয়ান-লাইনার মেথড রেফারেন্স কল
                    .collect(Collectors.toList());
            dto.setLineItems(itemDTOs);
        }

        return dto;
    }
}