package com.example.SCM.repository;

import com.example.SCM.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // নির্দিষ্ট ইউজারের সমস্ত নোটিফিকেশন টাইমলাইন অনুযায়ী সাজানো
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);

    // কাউন্টার ড্যাশবোর্ডের জন্য আনরিড নোটিফিকেশন কাউন্ট
    long countByRecipientIdAndIsReadFalse(String recipientId);

    @Query("SELECT n FROM Notification n WHERE n.recipientId = :userId OR n.recipientId = :role OR n.recipientId = 'ALL' ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsForUserOrRole(@Param("userId") String userId, @Param("role") String role);

    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.recipientId = :userId OR n.recipientId = :role OR n.recipientId = 'ALL') AND n.isRead = false")
    long countUnreadForUserOrRole(@Param("userId") String userId, @Param("role") String role);
}