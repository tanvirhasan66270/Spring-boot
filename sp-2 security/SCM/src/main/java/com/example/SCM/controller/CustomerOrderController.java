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
@CrossOrigin(origins = "*")
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    @PostMapping
    public ResponseEntity<CustomerOrderResponseDTO> createOrder(@RequestBody CustomerOrderRequestDTO dto) {
        CustomerOrderResponseDTO response = orderService.save(dto);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @PutMapping("{id}")
    public ResponseEntity<CustomerOrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody CustomerOrderRequestDTO dto
    ) {
        CustomerOrderResponseDTO response = orderService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerOrderResponseDTO>> getAllOrders() {
        List<CustomerOrderResponseDTO> responseList = orderService.findAll();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("{id}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrderById(@PathVariable Long id) {
        return orderService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok("Customer order instance purged successfully from cluster cache mapping.");
    }

    @GetMapping("track")
    public ResponseEntity<CustomerOrderResponseDTO> trackOrderByNumber(@RequestParam String orderNumber) {
        return orderService.trackOrder(orderNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}