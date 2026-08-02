package com.example.SCM.controller;

import com.example.SCM.dto.mapper.CustomerOrderMapper;
import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;
import com.example.SCM.repository.CustomerOrderRepository;
import com.example.SCM.service.CustomerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/customerOrders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService orderService;
    private final CustomerOrderRepository orderRepository;
    private final CustomerOrderMapper orderMapper;

    // 1. Place a New Order
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
    @PostMapping
    public ResponseEntity<CustomerOrderResponseDTO> createOrder(@RequestBody CustomerOrderRequestDTO dto) {
        CustomerOrderResponseDTO response = orderService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. General Update Order Metadata (এডমিন/ম্যানেজার অথবা অর্ডারের প্রকৃত মালিক)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @customerOrderSecurity.isOwner(#id, authentication)")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody CustomerOrderRequestDTO dto
    ) {
        CustomerOrderResponseDTO response = orderService.update(id, dto);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_OFFICER', 'LOGISTICS_OFFICER', 'CUSTOMER')")
    @GetMapping
    public ResponseEntity<List<CustomerOrderResponseDTO>> getAllOrders(Authentication authentication) {
        String currentUsername = authentication.getName();
        System.out.println(currentUsername);

        // রোল চেক করার সময় 'ROLE_' প্রিফিক্সসহ বা ছাড়া উভয়টিই হ্যান্ডেল করার ব্যবস্থা
        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "CUSTOMER") || Objects.equals(a.getAuthority(), "ROLE_CUSTOMER"));

        List<CustomerOrderResponseDTO> responseList;

        if (isCustomer) {
            // কাস্টমার হলে শুধু তার নিজের অর্ডারগুলোই দেখাবে
            responseList = orderService.findByCustomerUsername(currentUsername);
        } else {
            // এডমিন বা স্টাফ হলে সবার অর্ডার দেখতে পাবে
            responseList = orderService.findAll();
        }

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/customer")
    public ResponseEntity<List<CustomerOrderResponseDTO>> getByEmail(Authentication auth){
        List<CustomerOrderResponseDTO> listC = orderRepository.findByCustomerEmail(auth.getName())
                .stream().map(orderMapper::convertTOResponseDTO)
                .toList();
        return ResponseEntity.ok(listC);
    }


    // 4. Find Single Order Context By ID (স্টাফ অথবা অর্ডারের নিজস্ব মালিক)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_OFFICER', 'LOGISTICS_OFFICER') or @customerOrderSecurity.isOwner(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrderById(@PathVariable Long id) {
        return orderService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 5. Delete Order Record (শুধু এডমিন বা ম্যানেজার)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok("Customer order instance purged successfully from cluster cache mapping.");
    }

    // 6. Live Track Package via Order Number
    @PreAuthorize("permitAll()")
    @GetMapping("/track")
    public ResponseEntity<CustomerOrderResponseDTO> trackOrderByNumber(@RequestParam String orderNumber) {
        return orderService.trackOrder(orderNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 8. Dedicated Status Lifecycle Update Endpoint
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_OFFICER') or @customerOrderSecurity.isOwner(#id, authentication)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerOrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        CustomerOrderResponseDTO response = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(response);
    }

    // 7. Two-Step Email Link Verification Webhook
    @PreAuthorize("permitAll()")
    @GetMapping("/verify-link")
    public ResponseEntity<String> executeVerificationAndTriggerEmail(
            @RequestParam Long orderId,
            @RequestParam double amountPaid,
            @RequestParam String method) {

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