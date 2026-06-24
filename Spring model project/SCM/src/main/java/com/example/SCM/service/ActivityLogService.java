package com.example.SCM.service;

public interface ActivityLogService {
    void log(String userId, String action, String module, String referenceId, String description, String ipAddress);
}