package com.example.SCM.controller;

import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import com.example.SCM.service.CustomerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 🌐 ফ্রন্টএন্ড এঙ্গুলার পোর্টালের সাথে কানেকশনের জন্য CORS অন করা হলো
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    /**
     * 🛒 1. Place / Create a New Multi-Item Order
     * URL: POST http://localhost:8080/api/customer-orders
     */
    @PostMapping
    public ResponseEntity<CustomerOrderResponseDTO> createOrder(@RequestBody CustomerOrderRequestDTO dto) {
        CustomerOrderResponseDTO response = customerOrderService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 🔄 2. Update Existing Order Metadata & Recalculate Items
     * URL: PUT http://localhost:8080/api/customer-orders/{id}
     */
    @PutMapping("{id}")
    public ResponseEntity<CustomerOrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody CustomerOrderRequestDTO dto) {
        CustomerOrderResponseDTO response = customerOrderService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 📋 3. Get All Customer Orders (Fetch Join Optimized)
     * URL: GET http://localhost:8080/api/customer-orders
     */
    @GetMapping
    public ResponseEntity<List<CustomerOrderResponseDTO>> getAllOrders() {
        List<CustomerOrderResponseDTO> list = customerOrderService.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * 🔍 4. Get Single Order Specifications By ID
     * URL: GET http://localhost:8080/api/customer-orders/{id}
     */
    @GetMapping("{id}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrderById(@PathVariable Long id) {
        return customerOrderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ❌ 5. Cancel and Delete / Purge Order from Node Matrix
     * URL: DELETE http://localhost:8080/api/customer-orders/{id}
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> cancelAndPurgeOrder(@PathVariable Long id) {
        customerOrderService.delete(id);
        return ResponseEntity.ok("Customer order manifest index purged successfully from logistics network.");
    }
}