package com.example.SCM.controller;

import com.example.SCM.dto.response.OrderLineItemResponseDTO;
import com.example.SCM.service.OrderLineItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderLineItemController {

    private final OrderLineItemService lineItemService;

    /**
     * 📋 1. Get All Items Under a Specific Order ID
     * URL: GET http://localhost:8080/api/order-items/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderLineItemResponseDTO>> getItemsByOrderId(@PathVariable Long orderId) {
        List<OrderLineItemResponseDTO> list = lineItemService.findByOrderId(orderId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * 🔍 2. Get Single Line Item Specifications By ID
     * URL: GET http://localhost:8080/api/order-items/{id}
     */
    @GetMapping("{id}")
    public ResponseEntity<OrderLineItemResponseDTO> getItemById(@PathVariable Long id) {
        return lineItemService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ❌ 3. Remove/Delete Single Item from Order Cart Node
     * URL: DELETE http://localhost:8080/api/order-items/{id}
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> removeLineItem(@PathVariable Long id) {
        lineItemService.deleteItem(id);
        return ResponseEntity.ok("Target line item node removed and order subtotal recalculated.");
    }
}