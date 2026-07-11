package com.example.SCM.controller;

import com.example.SCM.entity.Notification;
import com.example.SCM.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService service;
    private final HttpServletRequest request;

    private String resolveUser() {
        String uid = request.getHeader("X-User-Id");
        return (uid != null && !uid.isBlank()) ? uid : "16"; // ডিফল্ট মক ইউজার টোকেন
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications() {
        return ResponseEntity.ok(service.getNotificationsForUser(resolveUser()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getCount() {
        return ResponseEntity.ok(service.getUnreadCount(resolveUser()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        service.markAllAsRead(resolveUser());
        return ResponseEntity.ok().build();
    }
}