package com.example.SCM.controller;

import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import com.example.SCM.entity.User;
import com.example.SCM.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> create(@RequestBody PurchaseOrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PurchaseOrderRequestDTO dto) {
        return ResponseEntity.ok(purchaseOrderService.update(id, dto));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getOrdersBySupplier(@PathVariable Long supplierId) {
        List<PurchaseOrderResponseDTO> allOrders = purchaseOrderService.findAll();

        List<PurchaseOrderResponseDTO> supplierOrders = allOrders.stream()
                .filter(po -> po.getSupplierId() != null && po.getSupplierId().equals(supplierId))
                .collect(Collectors.toList());

        if (supplierOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(supplierOrders);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAll() {
        // ১. ডাটাবেজ থেকে সমস্ত PO তুলে আনা
        List<PurchaseOrderResponseDTO> allOrders = purchaseOrderService.findAll();

        // ২. স্প্রিং সিকিউরিটি কন্টেক্সট থেকে কারেন্ট সেশন অবজেক্ট রিড করা
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User currentUser) {
            // ৩. রোল যদি SUPPLIER হয়, তবে ডাটাবেজ লেভেলে ফিল্টার লক সক্রিয় হবে
            if ("SUPPLIER".equalsIgnoreCase(currentUser.getRole().name())) {
                List<PurchaseOrderResponseDTO> filteredOrders = allOrders.stream()
                        .filter(po -> po.getSupplierId() != null && po.getSupplierId().equals(currentUser.getId()))
                        .collect(Collectors.toList());

                return ResponseEntity.ok(filteredOrders);
            }
            // 🎯 রোল ADMIN, MANAGER বা PROCUREMENT হলে সরাসরি সব ডাটা রিটার্ন করবে
            return ResponseEntity.ok(allOrders);
        }

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            // সিকিউরিটি ফিল্টার চেইন ব্যাকআপ (যদি প্রিন্সিপাল UserDetails রিটার্ন করে)
            String role = userDetails.getAuthorities().stream()
                    .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                    .findFirst().orElse("");

            if (role.contains("SUPPLIER")) {
                String email = userDetails.getUsername();
                List<PurchaseOrderResponseDTO> filteredOrders = allOrders.stream()
                        .filter(po -> po.getSupplierEmail() != null && po.getSupplierEmail().equalsIgnoreCase(email))
                        .collect(Collectors.toList());

                return ResponseEntity.ok(filteredOrders);
            }
            return ResponseEntity.ok(allOrders);
        }

        // ডিফল্ট সেফটি রেসপন্স
        if (allOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allOrders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> getById(@PathVariable Long id) {
        return purchaseOrderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.ok("Deleted Successfully");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam com.example.SCM.enumClass.PurchaseOrderStatus status) {
        PurchaseOrderResponseDTO response = purchaseOrderService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<PurchaseOrderResponseDTO> approve(@PathVariable Long id) {
        PurchaseOrderResponseDTO response = purchaseOrderService.approveOrder(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email-issue")
    public ResponseEntity<String> emailIssueOrder(@RequestParam String token) {
        try {
            purchaseOrderService.managerIssuedOrderByToken(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorHtml(e.getMessage()));
        }

        String html = """
            <!DOCTYPE html>
            <html>
            <head><title>Purchase Order Issued</title></head>
            <body style="font-family:Arial;text-align:center;padding:60px;background:#f5f7fa;">
                <div style="background:#fff;padding:40px;border-radius:8px;max-width:600px;margin:auto;">
                    <h2 style="color:#2b6cb0;">✔ Purchase Order Issued Successfully</h2>
                    <p>Purchase Order has been successfully issued. The supplier has been notified by email.</p>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.ok(html);
    }

    @GetMapping("/email-receive")
    public ResponseEntity<String> emailReceiveOrder(@RequestParam String token) {
        try {
            purchaseOrderService.supplierReceivedOrder(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorHtml(e.getMessage()));
        }

        String html = """
            <!DOCTYPE html>
            <html>
            <head><title>Purchase Order Received</title></head>
            <body style="font-family:Arial;text-align:center;padding:60px;background:#f5f7fa;">
                <div style="background:#fff;padding:40px;border-radius:8px;max-width:600px;margin:auto;">
                    <h2 style="color:#38a169;">✔ Purchase Order Received</h2>
                    <p>Thank you. Your acknowledgement has been recorded successfully.</p>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.ok(html);
    }

    private String errorHtml(String message) {
        return """
            <!DOCTYPE html>
            <html>
            <head><title>Link Invalid</title></head>
            <body style="font-family:Arial;text-align:center;padding:60px;background:#fff5f5;">
                <div style="background:#fff;padding:40px;border-radius:8px;max-width:600px;margin:auto;border:1px solid #FEB2B2;">
                    <h2 style="color:#C53030;">✖ This Link Could Not Be Processed</h2>
                    <p>%s</p>
                </div>
            </body>
            </html>
            """.formatted(message);
    }
}