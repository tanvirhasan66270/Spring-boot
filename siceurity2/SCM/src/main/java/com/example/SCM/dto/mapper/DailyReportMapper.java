package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.DailyReportRequestDTO;
import com.example.SCM.dto.response.DailyReportResponseDTO;
import com.example.SCM.entity.DailyReport;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * DailyReportMapper
 *
 * Responsible for converting:
 * 1. DailyReportRequestDTO -> DailyReport Entity
 * 2. DailyReport Entity -> DailyReportResponseDTO
 *
 * This class helps separate API models (DTOs)
 * from database entities.
 */
@Component
public class DailyReportMapper {

    /**
     * Convert DailyReportRequestDTO to DailyReport Entity.
     *
     * Used during report creation.
     *
     * @param dto Incoming request data from client
     * @return DailyReport entity ready for persistence
     */
    public DailyReport toEntity(DailyReportRequestDTO dto) {

        DailyReport report = new DailyReport();
        report.setWarehouseId(dto.getWarehouseId());
        report.setTotalTasksDone(dto.getTotalTasksDone());
        report.setIssuesLogged(dto.getIssuesLogged());
        report.setSummary(dto.getSummary());
        report.setAttachmentUrl(dto.getAttachmentUrl());

        if (dto.getReportDate() != null && !dto.getReportDate().isBlank()) {
            report.setReportDate(LocalDate.parse(dto.getReportDate()));
        }

        return report;
    }

    /**
     * Convert DailyReport Entity to DailyReportResponseDTO.
     *
     * Used when sending report information back to the client.
     *
     * @param entity DailyReport entity from database
     * @return DailyReportResponseDTO
     */
    public DailyReportResponseDTO convertTOResponseDTO(DailyReport entity) {

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