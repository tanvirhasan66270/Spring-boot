package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.LetterOfCreditRequestDTO;
import com.example.SCM.dto.response.LetterOfCreditResponseDTO;
import com.example.SCM.entity.LetterOfCredit;
import com.example.SCM.entity.PurchaseOrder;
import com.example.SCM.entity.Supplier;
import com.example.SCM.enumClass.LcStatus;
import com.example.SCM.repository.PurchaseOrderRepository;
import com.example.SCM.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class LetterOfCreditMapper {

    private final PurchaseOrderRepository poRepository;
    private final SupplierRepository supplierRepository;

    public LetterOfCredit toEntity(LetterOfCreditRequestDTO dto) {

        LetterOfCredit lc = new LetterOfCredit();
        lc.setIssuingBank(dto.getIssuingBank());
        lc.setShipmentIncoTerms(dto.getShipmentIncoTerms());
        lc.setPortOfLoading(dto.getPortOfLoading());
        lc.setPortOfDischarge(dto.getPortOfDischarge());
        lc.setAmount(dto.getAmount());
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier record missing for ID: " + dto.getSupplierId()));
            lc.setSupplier(supplier);
        }

        if (dto.getCurrency() != null && !dto.getCurrency().isBlank()) lc.setCurrency(dto.getCurrency());
        if (dto.getDocumentVaultUrl() != null) lc.setDocumentVaultUrl(dto.getDocumentVaultUrl());

        if (dto.getLatestShipmentDate() != null && !dto.getLatestShipmentDate().isBlank()) lc.setLatestShipmentDate(LocalDate.parse(dto.getLatestShipmentDate()));
        if (dto.getExpiryDate() != null && !dto.getExpiryDate().isBlank()) lc.setExpiryDate(LocalDate.parse(dto.getExpiryDate()));

        if (dto.getLcStatus() != null && !dto.getLcStatus().isBlank()) {
            lc.setLcStatus(LcStatus.valueOf(dto.getLcStatus().toUpperCase()));
        }

        if (dto.getPurchaseOrderId() != null) {
            PurchaseOrder po = poRepository.findById(dto.getPurchaseOrderId())
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found for ID: " + dto.getPurchaseOrderId()));
            lc.setPurchaseOrder(po);
            lc.setPoNumber(po.getPoNumber());
        }

        return lc;
    }

    public LetterOfCreditResponseDTO convertTOResponseDTO(LetterOfCredit entity) {

        LetterOfCreditResponseDTO dto = new LetterOfCreditResponseDTO();
        dto.setId(entity.getId());
        dto.setLcNumber(entity.getLcNumber());
        dto.setPoNumber(entity.getPoNumber());
        dto.setIssuingBank(entity.getIssuingBank());
        dto.setShipmentIncoTerms(entity.getShipmentIncoTerms());
        dto.setAmount(entity.getAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setAmendmentCount(entity.getAmendmentCount());
        dto.setPortOfLoading(entity.getPortOfLoading());
        dto.setPortOfDischarge(entity.getPortOfDischarge());
        dto.setDocumentVaultUrl(entity.getDocumentVaultUrl());
        dto.setLcStatus(entity.getLcStatus().name());
        if (entity.getSupplier() != null) {
            dto.setSupplierId(entity.getSupplier().getId());
            dto.setSupplierName(entity.getSupplier().getName());
            dto.setSupplierEmail(entity.getSupplier().getEmail());
        }

        if (entity.getPurchaseOrder() != null) dto.setPurchaseOrderId(entity.getPurchaseOrder().getId());
        if (entity.getLatestShipmentDate() != null) dto.setLatestShipmentDate(entity.getLatestShipmentDate().toString());
        if (entity.getExpiryDate() != null) dto.setExpiryDate(entity.getExpiryDate().toString());
        if (entity.getOpenedAt() != null) dto.setOpenedAt(entity.getOpenedAt().toString());
        if (entity.getCreatedAt() != null) dto.setCreatedAt(entity.getCreatedAt().toString());
        if (entity.getUpdatedAt() != null) dto.setUpdatedAt(entity.getUpdatedAt().toString());

        return dto;
    }
}