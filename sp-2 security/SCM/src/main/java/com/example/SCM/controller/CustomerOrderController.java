package com.example.SCM.controller;

import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import com.example.SCM.service.CustomerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customerOrders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    // 1. Place a New Order (Initial calculations & appropriate email trigger)
    @PostMapping
//    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MANAGER', 'SALES_OFFICER', 'COMMERCIAL_OFFICER')")
    public ResponseEntity<CustomerOrderResponseDTO> createOrder(@RequestBody CustomerOrderRequestDTO dto) {
        CustomerOrderResponseDTO response = orderService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //  2. General Update Order Metadata
    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_OFFICER', 'COMMERCIAL_OFFICER')")
    public ResponseEntity<CustomerOrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody CustomerOrderRequestDTO dto
    ) {
        CustomerOrderResponseDTO response = orderService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    //  3. Fetch All Logistics Orders
    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_OFFICER', 'COMMERCIAL_OFFICER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<List<CustomerOrderResponseDTO>> getAllOrders() {
        List<CustomerOrderResponseDTO> responseList = orderService.findAll();
        return ResponseEntity.ok(responseList);
    }

    //  4. Find Single Order Context By ID
    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_OFFICER', 'COMMERCIAL_OFFICER', 'LOGISTICS_OFFICER') " +
//            "or @customerOrderSecurity.isOwner(#id, authentication)")
    public ResponseEntity<CustomerOrderResponseDTO> getOrderById(@PathVariable Long id) {
        return orderService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    //  5. Delete Order Record
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok("Customer order instance purged successfully from cluster cache mapping.");
    }

    // 6. Live Track Package via Order Number
    @GetMapping("/track")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CustomerOrderResponseDTO> trackOrderByNumber(@RequestParam String orderNumber) {
        return orderService.trackOrder(orderNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 7. Two-Step Email Link Verification Webhook (Synchronized Route)
    @GetMapping("/verify-link")
    public ResponseEntity<String> executeVerificationAndTriggerEmail(
            @RequestParam Long orderId,
            @RequestParam double amountPaid,
            @RequestParam String method) {

        // এটি আপনার নতুন পেমেন্ট কন্ডিশন ও ২য় ফাইনাল ইমেইল ইনভয়েস ইঞ্জিন সাকসেসফুলি রান করবে
        orderService.processFinalPaymentConfirmation(orderId, amountPaid, method);

        return ResponseEntity.ok("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset='UTF-8'>
                <title>Payment Verified</title>
            </head>
            <body style='font-family: Arial, sans-serif; text-align: center; padding: 50px; background-color: #f7fafc;'>
                <div style='max-width: 500px; margin: auto; padding: 30px; border: 1px solid #e2e8f0; border-radius: 10px; background-color: #fff; box-shadow: 0 4px 6px rgba(0,0,0,0.05);'>
                    <h2 style='color: #2F855A; margin-bottom: 10px;'>Payment Matrix Verified!</h2>
                    <p style='color: #4A5568; line-height: 1.5;'>Thank you. Your order confirmation invoice and tracking metrics have been successfully transmitted via email system.</p>
                </div>
            </body>
            </html>
        """);
    }
}