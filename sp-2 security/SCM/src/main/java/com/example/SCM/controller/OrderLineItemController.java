package com.example.SCM.controller;

import com.example.SCM.dto.response.OrderLineItemResponseDTO;
import com.example.SCM.service.OrderLineItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderLineItemController {

    private final OrderLineItemService lineItemService;

    //  Get All Items Under a Specific Order ID
    // URL: GET http://localhost:8080/api/order-items/order/{orderId}

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_OFFICER', 'COMMERCIAL_OFFICER', 'LOGISTICS_OFFICER') " +
            "or @customerOrderSecurity.isOwner(#orderId, authentication)")
    public ResponseEntity<List<OrderLineItemResponseDTO>> getItemsByOrderId(@PathVariable Long orderId) {
        List<OrderLineItemResponseDTO> list = lineItemService.findByOrderId(orderId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    //Get Single Line Item Specifications By ID

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_OFFICER', 'COMMERCIAL_OFFICER', 'LOGISTICS_OFFICER') " +
            "or @orderLineItemSecurity.isOwner(#id, authentication)")
    public ResponseEntity<OrderLineItemResponseDTO> getItemById(@PathVariable Long id) {
        return lineItemService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Remove/Delete Single Item from Order Cart Node

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_OFFICER', 'COMMERCIAL_OFFICER')")
    public ResponseEntity<String> removeLineItem(@PathVariable Long id) {
        lineItemService.deleteItem(id);
        return ResponseEntity.ok("Target line item node removed and order subtotal recalculated.");
    }
}