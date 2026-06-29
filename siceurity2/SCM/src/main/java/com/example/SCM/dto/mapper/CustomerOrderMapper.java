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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CustomerOrderMapper
 *
 * Responsible for converting:
 * 1. CustomerOrderRequestDTO -> CustomerOrder Entity
 * 2. CustomerOrder Entity -> CustomerOrderResponseDTO
 *
 * This class helps separate API models (DTOs)
 * from database entities.
 */
@Component
@RequiredArgsConstructor
public class CustomerOrderMapper {

    private final OrderLineItemMapper lineItemMapper;

    /**
     * Convert CustomerOrderRequestDTO to CustomerOrder Entity.
     *
     * Used during order creation.
     *
     * @param dto Incoming request data from client
     * @param customer Associated Customer account user
     * @return CustomerOrder entity ready for persistence
     */
    public CustomerOrder toEntity(CustomerOrderRequestDTO dto, User customer) {

        CustomerOrder order = CustomerOrder.builder()
                .customer(customer)
                .customerName(customer != null ? customer.getName() : null)
                .customerEmail(customer != null ? customer.getEmail() : null)
                .deliveryAddress(dto.getDeliveryAddress())
                .codAmount(dto.getCodAmount())
                .currency("BDT")
                .serviceType(dto.getServiceType() != null && !dto.getServiceType().isBlank() ?
                        ServiceType.valueOf(dto.getServiceType().toUpperCase()) : ServiceType.STANDARD)
                .status(dto.getStatus() != null && !dto.getStatus().isBlank() ?
                        CustomerOrderStatus.valueOf(dto.getStatus().toUpperCase()) : CustomerOrderStatus.PENDING)
                .estimatedDelivery(dto.getEstimatedDelivery() != null && !dto.getEstimatedDelivery().isBlank() ?
                        LocalDate.parse(dto.getEstimatedDelivery()) : null)
                .lineItems(new ArrayList<>())
                .build();

        if (dto.getItems() != null) {
            dto.getItems().forEach(itemDto -> {
                OrderLineItem item = lineItemMapper.toEntity(itemDto);
                if (item != null) {
                    order.addLineItem(item);
                }
            });
        }

        return order;
    }

    /**
     * Convert CustomerOrder Entity to CustomerOrderResponseDTO.
     *
     * Used when sending order information back to the client.
     *
     * @param entity CustomerOrder entity from database
     * @return CustomerOrderResponseDTO
     */
    public CustomerOrderResponseDTO convertTOResponseDTO(CustomerOrder entity) {

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
        dto.setServiceType(entity.getServiceType() != null ? entity.getServiceType().name() : null);
        dto.setCodAmount(entity.getCodAmount());
        dto.setDeliveryCharge(entity.getDeliveryCharge());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setDeliveryAddress(entity.getDeliveryAddress());
        dto.setEstimatedDelivery(entity.getEstimatedDelivery() != null ? entity.getEstimatedDelivery().toString() : null);
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);

        if (entity.getLineItems() != null) {
            List<OrderLineItemResponseDTO> itemDTOs = entity.getLineItems().stream()
                    .map(lineItemMapper::convertTOResponseDTO)
                    .collect(Collectors.toList());
            dto.setLineItems(itemDTOs);
        }

        return dto;
    }

}