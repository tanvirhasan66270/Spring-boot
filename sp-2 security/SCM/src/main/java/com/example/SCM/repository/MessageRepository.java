package com.example.SCM.repository;

import com.example.SCM.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Fetch only the messages targeted for this specific logged-in user
    List<Message> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
}