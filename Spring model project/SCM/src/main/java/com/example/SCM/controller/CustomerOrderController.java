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
@RequestMapping("/api/orders/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ফ্রন্টএন্ড (Angular/React) কানেক্টিভিটির ক্রস-অরিজিন পলিসি হ্যান্ডেল করার জন্য
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    /**
     * 🛒 1. Place a New Multi-Item Order
     * POST http://localhost:8085/api/orders/
     */
    @PostMapping
    public ResponseEntity<CustomerOrderResponseDTO> createOrder(@RequestBody CustomerOrderRequestDTO dto) {
        CustomerOrderResponseDTO response = orderService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 🔄 2. Update Existing Order Metadata & Items
     * PUT http://localhost:8085/api/orders/{id}
     */
    @PutMapping("{id}")
    public ResponseEntity<CustomerOrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody CustomerOrderRequestDTO dto) {
        CustomerOrderResponseDTO response = orderService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 📋 3. Get All Customer Orders (Fetch Join Optimized)
     * GET http://localhost:8085/api/orders/
     */
    @GetMapping
    public ResponseEntity<List<CustomerOrderResponseDTO>> getAllOrders() {
        List<CustomerOrderResponseDTO> responseList = orderService.findAll();
        return ResponseEntity.ok(responseList);
    }

    /**
     * 🔍 4. Get Single Order Specifications By ID
     * GET http://localhost:8085/api/orders/{id}
     */
    @GetMapping("{id}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrderById(@PathVariable Long id) {
        return orderService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    //  5. Delete / Purge Order Instance

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok("Customer order instance purged successfully from cluster cache mapping.");
    }

    //  6. Live Track Order via Unique Order Number

    @GetMapping("track")
    public ResponseEntity<CustomerOrderResponseDTO> trackOrderByNumber(@RequestParam String orderNumber) {
        return orderService.trackOrder(orderNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}