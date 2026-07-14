package com.example.SCM.controller;

import com.example.SCM.entity.Notification;
import com.example.SCM.entity.User;
import com.example.SCM.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.getNotificationsForUser(currentUser.getId().toString()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getCount(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.getUnreadCount(currentUser.getId().toString()));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("@notificationSecurity.isOwner(#id, authentication)")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal User currentUser) {
        service.markAllAsRead(currentUser.getId().toString());
        return ResponseEntity.ok().build();
    }
}