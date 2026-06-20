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
@RequestMapping("/api/customer-orders/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 💡 এঙ্গুলার ড্যাশবোর্ডের সাথে CORS পলিসি ব্লকিং এড়াতে
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    /**
     * 1. Create New Customer Order (POST)
     * URL: POST http://localhost:8080/api/customer-orders
     */
    @PostMapping
    public ResponseEntity<CustomerOrderResponseDTO> create(@RequestBody CustomerOrderRequestDTO dto) {
        CustomerOrderResponseDTO response = orderService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 2. Update Existing Order Metadata (PUT)
     * URL: PUT http://localhost:8080/api/customer-orders/{id}
     */
    @PutMapping("{id}")
    public ResponseEntity<CustomerOrderResponseDTO> update(
            @PathVariable Long id,
            @RequestBody CustomerOrderRequestDTO dto) {
        return ResponseEntity.ok(orderService.update(id, dto));
    }

    /**
     * 3. Get All Orders with Details (GET)
     * URL: GET http://localhost:8080/api/customer-orders
     */
    @GetMapping
    public ResponseEntity<List<CustomerOrderResponseDTO>> getAll() {
        List<CustomerOrderResponseDTO> list = orderService.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    /**
     * 4. Get Order By ID (GET)
     * URL: GET http://localhost:8080/api/customer-orders/{id}
     */
    @GetMapping("{id}")
    public ResponseEntity<CustomerOrderResponseDTO> getById(@PathVariable Long id) {
        return orderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 5. Quick Update Status Node (PATCH)
     * URL: PATCH http://localhost:8080/api/customer-orders/{id}/status?status=SHIPPED
     */
    @PatchMapping("{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateStatus(id, status);
        return ResponseEntity.ok("Customer order execution status matrix updated successfully.");
    }

    /**
     * 6. Cancel or Delete Order Record (DELETE)
     * URL: DELETE http://localhost:8080/api/customer-orders/{id}
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok("Customer order cluster deployment record purged successfully.");
    }
}