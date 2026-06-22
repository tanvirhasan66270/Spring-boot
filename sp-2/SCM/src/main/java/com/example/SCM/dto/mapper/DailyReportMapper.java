package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.DailyReportRequestDTO;
import com.example.SCM.dto.response.DailyReportResponseDTO;
import com.example.SCM.entity.DailyReport;
import com.example.SCM.enumClass.ReportStatus;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DailyReportMapper {

    public DailyReport toEntity(DailyReportRequestDTO dto) {
        if (dto == null) return null;

        DailyReport report = new DailyReport();
        report.setWarehouseId(dto.getWarehouseId());
        report.setTotalTasksDone(dto.getTotalTasksDone());
        report.setIssuesLogged(dto.getIssuesLogged());
        report.setSummary(dto.getSummary());
        report.setAttachmentUrl(dto.getAttachmentUrl());

        if (dto.getReportDate() != null && !dto.getReportDate().isBlank()) {
            report.setReportDate(LocalDate.parse(dto.getReportDate()));
        }
        if (dto.getReportStatus() != null && !dto.getReportStatus().isBlank()) {
            report.setReportStatus(ReportStatus.valueOf(dto.getReportStatus().toUpperCase()));
        }

        return report;
    }

    public DailyReportResponseDTO toResponseDTO(DailyReport entity) {
        if (entity == null) return null;

        DailyReportResponseDTO dto = new DailyReportResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setSummary(entity.getSummary());
        dto.setTotalTasksDone(entity.getTotalTasksDone());
        dto.setIssuesLogged(entity.getIssuesLogged());
        dto.setAttachmentUrl(entity.getAttachmentUrl());
        dto.setReportStatus(entity.getReportStatus().name());

        if (entity.getReportDate() != null) dto.setReportDate(entity.getReportDate().toString());
        if (entity.getGeneratedAt() != null) dto.setGeneratedAt(entity.getGeneratedAt().toString());
        if (entity.getUpdatedAt() != null) dto.setUpdatedAt(entity.getUpdatedAt().toString());

        return dto;
    }
}