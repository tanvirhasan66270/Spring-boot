package com.example.SCM.controller;

import com.example.SCM.dto.request.MessageRequestDTO;
import com.example.SCM.dto.response.MessageResponseDTO;
import com.example.SCM.entity.User;
import com.example.SCM.serviceImp.MessageServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class MessageController {

    private final MessageServiceImp service;

    @PostMapping
    public ResponseEntity<List<MessageResponseDTO>> composeMessage(
            @RequestBody MessageRequestDTO dto,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.sendMessage(dto, currentUser));
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<MessageResponseDTO>> getInbox(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.getInbox(currentUser.getId().toString()));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("@messageSecurity.isRecipient(#id, authentication)")
    public ResponseEntity<Void> toggleReadStatus(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}