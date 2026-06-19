package com.example.SCM.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class QCInspectionRequestDTO {
    private Long id;
    private Long grnId;
    private Long productId;
    private String inspectionType;
    private Long inspectedBy;
    private int sampleSize;
    private int defectsFound;
    private String defectDescription;
    private String result;
    private String certificateRef;
    private String labTestReport;
    private String inspectedAt;

    // 🔗 চাইল্ড চেকলিস্টের রিকোয়েস্ট অ্যারে
    private List<QCChecklistRequestDTO> checklists;
}