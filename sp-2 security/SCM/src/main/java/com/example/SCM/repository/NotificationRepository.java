package com.example.SCM.repository;

import com.example.SCM.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // নির্দিষ্ট ইউজারের সমস্ত নোটিফিকেশন টাইমলাইন অনুযায়ী সাজানো
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);

    // কাউন্টার ড্যাশবোর্ডের জন্য আনরিড নোটিফিকেশন কাউন্ট
    long countByRecipientIdAndIsReadFalse(String recipientId);
}