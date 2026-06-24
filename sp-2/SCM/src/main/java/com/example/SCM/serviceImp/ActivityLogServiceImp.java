package com.example.SCM.serviceImp;

import com.example.SCM.entity.ActivityLog;
import com.example.SCM.enumClass.ActionStatus;
import com.example.SCM.repository.ActivityLogRepository;
import com.example.SCM.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImp implements ActivityLogService {

    private final ActivityLogRepository logRepository;

    @Override
    @Transactional
    public void log(String userId, String userEmail, String action, String module,
                    String referenceId, String description, String oldValue,
                    String newValue, ActionStatus actionStatus, String ipAddress) {

        ActivityLog auditLog = ActivityLog.builder()
                .userId(userId)
                .userEmail(userEmail)
                .action(action)
                .module(module != null ? module.toUpperCase() : "UNKNOWN")
                .referenceId(referenceId)
                .description(description)
                .oldValue(oldValue)
                .newValue(newValue)
                .actionStatus(actionStatus != null ? actionStatus : ActionStatus.SUCCESS)
                .ipAddress(ipAddress != null ? ipAddress : "UNKNOWN_IP")
                .build();

        logRepository.save(auditLog);
    }
}