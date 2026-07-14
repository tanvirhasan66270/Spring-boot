package com.example.SCM.controller;

import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import com.example.SCM.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> create(
            @RequestBody PurchaseOrderRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseOrderService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PurchaseOrderRequestDTO dto) {

        return ResponseEntity.ok(
                purchaseOrderService.update(id, dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAll() {

        List<PurchaseOrderResponseDTO> list =
                purchaseOrderService.findAll();

        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> getById(
            @PathVariable Long id) {

        return purchaseOrderService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        purchaseOrderService.delete(id);

        return ResponseEntity.ok("Deleted Successfully");
    }

    // ===========================
    // Manager Issue Purchase Order
    // ===========================

    @GetMapping("/email-issue")
    public ResponseEntity<String> emailIssueOrder(
            @RequestParam Long id,
            @RequestParam Long managerId) {

        purchaseOrderService.managerIssuedOrder(id, managerId);

        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Purchase Order Issued</title>
            </head>
            <body style="font-family:Arial;text-align:center;padding:60px;background:#f5f7fa;">
                <div style="background:#fff;padding:40px;border-radius:8px;max-width:600px;margin:auto;">
                    <h2 style="color:#2b6cb0;">✔ Purchase Order Issued Successfully</h2>
                    <p>
                        Purchase Order has been successfully issued.
                        The supplier has been notified by email.
                    </p>
                </div>
            </body>
            </html>
            """;

        return ResponseEntity.ok(html);
    }

    // ===========================
    // Supplier Receive Purchase Order
    // ===========================

    @GetMapping("/email-receive")
    public ResponseEntity<String> emailReceiveOrder(
            @RequestParam Long id) {

        purchaseOrderService.supplierReceivedOrder(id);

        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Purchase Order Received</title>
            </head>
            <body style="font-family:Arial;text-align:center;padding:60px;background:#f5f7fa;">
                <div style="background:#fff;padding:40px;border-radius:8px;max-width:600px;margin:auto;">
                    <h2 style="color:#38a169;">✔ Purchase Order Received</h2>
                    <p>
                        Thank you.
                        Your acknowledgement has been recorded successfully.
                    </p>
                </div>
            </body>
            </html>
            """;

        return ResponseEntity.ok(html);
    }
}