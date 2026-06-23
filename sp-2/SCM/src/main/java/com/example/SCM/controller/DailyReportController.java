package com.example.SCM.controller;

import com.example.SCM.dto.request.DailyReportRequestDTO;
import com.example.SCM.dto.response.DailyReportResponseDTO;
import com.example.SCM.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DailyReportController {

    private final DailyReportService reportService;

    //  1. Log New Report (By Logistics Officer)

    @PostMapping
    public ResponseEntity<DailyReportResponseDTO> createReport(@RequestBody DailyReportRequestDTO dto) {
        return new ResponseEntity<>(reportService.save(dto), HttpStatus.CREATED);
    }

    // 2. Modify Pending Report (Only for DRAFT states)

    @PutMapping("{id}")
    public ResponseEntity<DailyReportResponseDTO> updateReport(
            @PathVariable Long id,
            @RequestBody DailyReportRequestDTO dto) {
        return ResponseEntity.ok(reportService.update(id, dto));
    }

    // 3. Standard API Lock & Official Approval Node

    @PatchMapping("approve/{id}")
    public ResponseEntity<DailyReportResponseDTO> approveReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.approveReport(id, null));
    }

    // 4. One-Click Email Approval Gateway (Called when Manager clicks button inside Gmail)

    @GetMapping("email-approve")
    public ResponseEntity<String> emailApproveReport(
            @RequestParam Long id,
            @RequestParam String approverId) {

        reportService.approveReport(id, approverId);

        // বাটন ক্লিকে ব্রাউজারে এই সাকসেস মেসেজ রেসপন্সটি স্ক্রিনে ভেসে উঠবে
        return ResponseEntity.ok("""
            <html>
            <body style="font-family: Arial, sans-serif; text-align: center; margin-top: 50px;">
                <div style="display: inline-block; padding: 30px; border: 1px solid #38A169; border-radius: 8px; background-color: #F0FFF4;">
                    <h2 style="color: #38A169; margin: 0 0 10px 0;">✔ Report Approved Successfully!</h2>
                    <p style="color: #2D3748; margin: 0;">Daily Report ID: #%d has been officially locked and marked as APPROVED in SCM Cluster Nodes.</p>
                </div>
            </body>
            </html>
            """.formatted(id));
    }

  // 5. Fetch All Reports

    @GetMapping
    public ResponseEntity<List<DailyReportResponseDTO>> getAllReports() {
        return ResponseEntity.ok(reportService.findAll());
    }

   // 6. Filter by Warehouse Location Node

    @GetMapping("warehouse/{warehouseId}")
    public ResponseEntity<List<DailyReportResponseDTO>> getByWarehouse(@PathVariable String warehouseId) {
        return ResponseEntity.ok(reportService.getByWarehouse(warehouseId));
    }
}