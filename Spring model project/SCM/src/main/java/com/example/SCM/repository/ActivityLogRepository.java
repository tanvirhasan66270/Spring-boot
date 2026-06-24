package com.example.SCM.repository;

import com.example.SCM.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    // অডিট করার সময় মডিউল বা ইউজার ধরে লগ ফিল্টার করার জন্য কাস্টম কুয়েরি গেটওয়ে
    List<ActivityLog> findByModuleOrderByPerformedAtDesc(String module);
    List<ActivityLog> findByUserIdOrderByPerformedAtDesc(String userId);
}