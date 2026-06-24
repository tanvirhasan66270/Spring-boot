package com.example.SCM.repository;

import com.example.SCM.entity.ActivityLog;
import com.example.SCM.enumClass.ActionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findAllByOrderByPerformedAtDesc();
    List<ActivityLog> findByModuleOrderByPerformedAtDesc(String module);
    List<ActivityLog> findByUserIdOrderByPerformedAtDesc(String userId);
    List<ActivityLog> findByUserEmailOrderByPerformedAtDesc(String userEmail);

    // 💡 এনাম স্ট্যাটাস ভিত্তিক ফিল্টারিং কুয়েরি
    List<ActivityLog> findByActionStatusOrderByPerformedAtDesc(ActionStatus actionStatus);
}