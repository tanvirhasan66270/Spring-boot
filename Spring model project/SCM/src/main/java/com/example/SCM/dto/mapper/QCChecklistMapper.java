package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.QCChecklistRequestDTO;
import com.example.SCM.dto.response.QCChecklistResponseDTO;
import com.example.SCM.entity.QCChecklist;
import com.example.SCM.entity.QCInspection;
import org.springframework.stereotype.Component;

@Component
public class QCChecklistMapper {

    /**
     * Entity -> Response DTO (Flattening Operation)
     */
    public QCChecklistResponseDTO toResponseDTO(QCChecklist entity) {
        if (entity == null) {
            return null;
        }

        QCChecklistResponseDTO dto = new QCChecklistResponseDTO();
        dto.setId(entity.getId());
        dto.setCheckpointName(entity.getCheckpointName());
        dto.setPassed(entity.isPassed());
        dto.setRemarks(entity.getRemarks());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getQcInspection() != null) {
            dto.setInspectionId(entity.getQcInspection().getId());
            dto.setInspectionType(entity.getQcInspection().getInspectionType());
        }

        return dto;
    }

    /**
     * Request DTO -> Entity (Create Operation)
     */
    public QCChecklist toEntity(QCChecklistRequestDTO dto, QCInspection inspection) {
        if (dto == null) {
            return null;
        }

        QCChecklist entity = new QCChecklist();
        entity.setCheckpointName(dto.getCheckpointName());
        entity.setPassed(dto.isPassed());
        entity.setRemarks(dto.getRemarks());

        entity.setQcInspection(inspection);

        return entity;
    }

    /**
     * Update Existing Entity
     */
    public void updateEntity(QCChecklistRequestDTO dto, QCChecklist entity, QCInspection inspection) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setCheckpointName(dto.getCheckpointName());
        entity.setPassed(dto.isPassed());
        entity.setRemarks(dto.getRemarks());

        if (inspection != null) {
            entity.setQcInspection(inspection);
        }
    }
}