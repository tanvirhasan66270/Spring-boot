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
@RequestMapping("/api/purchase-orders") // 💡 বেস্ট প্র্যাকটিস: শেষের "/" বাদ দেওয়া হয়েছে
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;


    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> create(@RequestBody PurchaseOrderRequestDTO dto) {
        PurchaseOrderResponseDTO response = purchaseOrderService.save(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping("/{id}") // 💡 বেস্ট প্র্যাকটিস: শুরুতে "/" যোগ করা হয়েছে
    public ResponseEntity<PurchaseOrderResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PurchaseOrderRequestDTO dto) {
        PurchaseOrderResponseDTO response = purchaseOrderService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAll() {
        List<PurchaseOrderResponseDTO> list = purchaseOrderService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
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
        return ResponseEntity.ok("Deleted successfully");
    }

   //  অপ্টিমাইজেশন: ইমেইল থ্রু-তে সফলভাবে সাবমিট হলে ম্যানেজার যেন একটি সুন্দর এবং পরিচ্ছন্ন স্ক্রিন দেখতে পান

    @GetMapping("/email-issue")
    public ResponseEntity<String> emailIssueOrder(@RequestParam Long id, @RequestParam Long managerId) {
        purchaseOrderService.managerIssuedOrder(id, managerId);

        String htmlResponse = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; text-align: center; background-color: #F7FAFC; padding: 50px; }
                    .card { max-width: 500px; margin: 0 auto; background: white; padding: 40px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-top: 5px solid #3182CE; }
                    h1 { color: #2B6CB0; margin-bottom: 10px; font-size: 24px; }
                    p { color: #4A5568; font-size: 15px; line-height: 1.6; }
                </style>
            </head>
            <body>
                <div class='card'>
                    <h1>✔ Order Dispatched Successfully!</h1>
                    <p>The Purchase Order status has been updated to <b>ISSUED</b> in SCM core matrix, and a live notification tracking stack has been transmitted to the Supplier's profile.</p>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.ok(htmlResponse);
    }

    /**
     * 7. 🤝 Supplier One-Click Email / Dashboard Gateway (GET)
     */
    @GetMapping("/email-receive")
    public ResponseEntity<String> emailReceiveOrder(@RequestParam Long id) {
        purchaseOrderService.supplierReceivedOrder(id);

        String htmlResponse = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; text-align: center; background-color: #F7FAFC; padding: 50px; }
                    .card { max-width: 500px; margin: 0 auto; background: white; padding: 40px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-top: 5px solid #38A169; }
                    h1 { color: #2F855A; margin-bottom: 10px; font-size: 24px; }
                    p { color: #4A5568; font-size: 15px; line-height: 1.6; }
                </style>
            </head>
            <body>
                <div class='card'>
                    <h1>🤝 Order Acknowledged!</h1>
                    <p>Thank you for your business confirmation. The target procurement order status is now updated to <b>RECEIVED</b> inside our SCM hub.</p>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.ok(htmlResponse);
    }
}