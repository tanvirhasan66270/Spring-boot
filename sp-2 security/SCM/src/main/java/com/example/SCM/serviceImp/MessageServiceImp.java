package com.example.SCM.serviceImp;

import com.example.SCM.dto.request.MessageRequestDTO;
import com.example.SCM.dto.response.MessageResponseDTO;
import com.example.SCM.entity.Message;
import com.example.SCM.entity.User;
import com.example.SCM.role.Role;
import com.example.SCM.repository.MessageRepository;
import com.example.SCM.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImp {

    private final MessageRepository repository;
    private final UserRepository userRepository;

    @Transactional
    public List<MessageResponseDTO> sendMessage(MessageRequestDTO dto, User currentUser) {
        List<Message> messagesToSave = new ArrayList<>();

        String senderId = currentUser.getId().toString();
        String senderName = currentUser.getName(); // আপনার User Entity-র নাম রিটার্ন মেথড
        Role senderRole = currentUser.getRole();       // ইউজারের রোল

        // 🎯 রুল ১: DRIVER -> Auto-Route to MANAGER & LOGISTICS_OFFICER
        if (senderRole == Role.DRIVER) {
            List<User> recipients = userRepository.findUsersByRoles(List.of(Role.MANAGER, Role.LOGISTICS_OFFICER));
            for (User u : recipients) {
                messagesToSave.add(buildMessageObject(dto, senderId, senderName, u.getId().toString()));
            }
        }
        // 🎯 রুল ২: CUSTOMER -> Auto-Route to MANAGER & SALES_OFFICER
        else if (senderRole == Role.CUSTOMER) {
            List<User> recipients = userRepository.findUsersByRoles(List.of(Role.MANAGER, Role.SALES_OFFICER));
            for (User u : recipients) {
                messagesToSave.add(buildMessageObject(dto, senderId, senderName, u.getId().toString()));
            }
        }
        // 🎯 রুল ৩: SUPPLIER -> Auto-Route to MANAGER & PROCUREMENT
        else if (senderRole == Role.SUPPLIER) {
            List<User> recipients = userRepository.findUsersByRoles(List.of(Role.MANAGER, Role.PROCUREMENT));
            for (User u : recipients) {
                messagesToSave.add(buildMessageObject(dto, senderId, senderName, u.getId().toString()));
            }
        }
        // 🎯 রুল ৪: INTERNAL STAFF -> নির্দিষ্ট সিলেক্টেড ইউজার
        else {
            if (dto.getRecipientId() == null || dto.getRecipientId().isBlank()) {
                throw new IllegalArgumentException("Internal staff must specify a target recipient user.");
            }
            messagesToSave.add(buildMessageObject(dto, senderId, senderName, dto.getRecipientId()));
        }

        List<Message> savedMessages = repository.saveAll(messagesToSave);
        return savedMessages.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private Message buildMessageObject(MessageRequestDTO dto, String senderId, String senderName, String recipientId) {
        return Message.builder()
                .senderId(senderId)
                .senderName(senderName)
                .recipientId(recipientId)
                .subject(dto.getSubject())
                .body(dto.getBody())
                .priority(dto.getPriority().toUpperCase())
                .build();
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getInbox(String userId) {
        return repository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long id) {
        repository.findById(id).ifPresent(m -> {
            m.setStatus("READ");
            repository.save(m);
        });
    }

    private MessageResponseDTO toResponseDTO(Message entity) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(entity.getId());
        dto.setSenderId(entity.getSenderId());
        dto.setSenderName(entity.getSenderName());
        dto.setSubject(entity.getSubject());
        dto.setBody(entity.getBody());
        dto.setPriority(entity.getPriority());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt().toString());
        return dto;
    }
}