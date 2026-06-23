package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class DailyReportRequestDTO {
    private String warehouseId;
    private String reportDate;
    private int totalTasksDone;
    private int issuesLogged;
    private String summary;
    private String reportStatus; // ফ্রন্টএন্ড থেকে SUBMITTED বা DRAFT পাঠাবে
    private String attachmentUrl;
}