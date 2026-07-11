package com.example.SCM.serviceImp;

import com.example.SCM.entity.Notification;
import com.example.SCM.repository.NotificationRepository;
import com.example.SCM.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository repository;

    @Override
    @Transactional
    public Notification send(String recipientId, String type, String title, String message) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .type(type)
                .title(title)
                .message(message)
                .build();
        return repository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(String userId) {
        return repository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        repository.findById(id).ifPresent(n -> {
            n.setRead(true);
            repository.save(n);
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        List<Notification> unread = repository.findByRecipientIdOrderByCreatedAtDesc(userId);
        unread.stream().filter(n -> !n.isRead()).forEach(n -> n.setRead(true));
        repository.saveAll(unread);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return repository.countByRecipientIdAndIsReadFalse(userId);
    }
}