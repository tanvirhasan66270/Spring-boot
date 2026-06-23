package com.example.SCM.controller;

import com.example.SCM.entity.ActivityLog;
import com.example.SCM.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/logs/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // এডমিন ড্যাশবোর্ড প্যানেল কানেক্টিভিটি
public class ActivityLogController {

    private final ActivityLogRepository logRepository;

    /**
     * 📊 1. Fetch All Activity Logs (For Admin System Audit)
     * GET http://localhost:8085/api/admin/logs/
     */
    @GetMapping
    public ResponseEntity<List<ActivityLog>> getAllLogs() {
        // ডাটাবেজের সব লগ অডিট ট্রেইল হিসেবে এডমিনকে রিটার্ন করবে
        return ResponseEntity.ok(logRepository.findAll());
    }

    /**
     * 🔍 2. Filter Audit Trail By Module Name (e.g., LC, PO, QC)
     * GET http://localhost:8085/api/admin/logs/module/{moduleName}
     */
    @GetMapping("module/{moduleName}")
    public ResponseEntity<List<ActivityLog>> getLogsByModule(@PathVariable String moduleName) {
        return ResponseEntity.ok(logRepository.findByModuleOrderByPerformedAtDesc(moduleName.toUpperCase()));
    }

    /**
     * 👥 3. Track Specific Employee / User Activity Footprints
     * GET http://localhost:8085/api/admin/logs/user/{userId}
     */
    @GetMapping("user/{userId}")
    public ResponseEntity<List<ActivityLog>> getLogsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(logRepository.findByUserIdOrderByPerformedAtDesc(userId));
    }
}