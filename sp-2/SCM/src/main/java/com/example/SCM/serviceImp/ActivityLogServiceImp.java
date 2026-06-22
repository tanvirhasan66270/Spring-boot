package com.example.SCM.serviceImp;

import com.example.SCM.entity.ActivityLog;
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
    public void log(String userId, String action, String module, String referenceId, String description, String ipAddress) {
        ActivityLog auditLog = ActivityLog.builder()
                .userId(userId)
                .action(action)
                .module(module)
                .referenceId(referenceId)
                .description(description)
                .ipAddress(ipAddress != null ? ipAddress : "UNKNOWN_IP")
                .build();

        logRepository.save(auditLog);
    }
}