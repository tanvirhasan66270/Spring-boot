package com.example.SCM.controller;

import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import com.example.SCM.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PROCUREMENT')")
    public ResponseEntity<PurchaseOrderResponseDTO> create(@RequestBody PurchaseOrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrderService.save(dto));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PROCUREMENT')")
    public ResponseEntity<PurchaseOrderResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PurchaseOrderRequestDTO dto) {
        return ResponseEntity.ok(purchaseOrderService.update(id, dto));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getOrdersBySupplier(@PathVariable Long supplierId) {
        // সার্ভিস লেয়ার থেকে সমস্ত PO নিয়ে আসা
        List<PurchaseOrderResponseDTO> allOrders = purchaseOrderService.findAll();

        // জাভা ৮ স্ট্রিম ব্যবহার করে শুধুমাত্র লগইন করা সাপ্লায়ারের PO গুলো ফিল্টার করা
        List<PurchaseOrderResponseDTO> supplierOrders = allOrders.stream()
                .filter(po -> po.getSupplierId() != null && po.getSupplierId().equals(supplierId))
                .collect(Collectors.toList());

        if (supplierOrders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(supplierOrders);
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PROCUREMENT')")
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAll() {
        List<PurchaseOrderResponseDTO> list = purchaseOrderService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PROCUREMENT')")
    public ResponseEntity<PurchaseOrderResponseDTO> getById(@PathVariable Long id) {
        return purchaseOrderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.ok("Deleted Successfully");
    }

    /**
     * 🚀 এন্ডপয়েন্ট ১: সরাসরি স্ট্যাটাস পরিবর্তন করার জন্য (RECEIVED/CANCELLED)
     * যা ফ্রন্টএন্ড এঙ্গুলার সার্ভিসের changeStatus() থেকে কল হচ্ছে।
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam com.example.SCM.enumClass.PurchaseOrderStatus status) {

        // আমাদের কাস্টম সার্ভিস মেথড যা DRAFT লক বাইপাস করবে
        PurchaseOrderResponseDTO response = purchaseOrderService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ এন্ডপয়েন্ট ২: পারচেজ অর্ডার ম্যানেজার লেভেল থেকে সরাসরি অ্যাপ্রুভ করার জন্য
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<PurchaseOrderResponseDTO> approve(@PathVariable Long id) {
        // আপনার PurchaseOrderService এর ভেতরের এপ্রুভ লজিকটি কল করুন
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