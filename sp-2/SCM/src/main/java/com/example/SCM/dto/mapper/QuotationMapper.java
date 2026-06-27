package com.example.SCM.mapper;

import com.example.SCM.dto.request.QuotationRequestDTO;
import com.example.SCM.dto.response.QuotationResponseDTO;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.PurchaseRequisition;
import com.example.SCM.entity.Quotation;
import com.example.SCM.entity.Supplier;
import com.example.SCM.enumClass.QuotationStatus;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class QuotationMapper {

    private final EntityManager entityManager;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor Injection
    public QuotationMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public Quotation toEntity(QuotationRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Quotation quotation = new Quotation();

        // String থেকে LocalDate-এ কনভার্সন (Null চেক সহ)
        if (dto.getValidUntil() != null) {
            quotation.setValidUntil(LocalDate.parse(dto.getValidUntil(), DATE_FORMATTER));
        }
        if (dto.getReceivedAt() != null) {
            quotation.setReceivedAt(LocalDate.parse(dto.getReceivedAt(), DATE_FORMATTER));
        }
        if (dto.getDeliveryTime() != null) {
            quotation.setDeliveryTime(LocalDate.parse(dto.getDeliveryTime(), DATE_FORMATTER));
        }

        if (dto.getSupplierId() != null) {
            quotation.setSupplier(entityManager.getReference(Supplier.class, dto.getSupplierId()));
        }
        if (dto.getProductIds() != null) {
            quotation.setProduct(entityManager.getReference(Product.class, dto.getProductIds()));
        }
        if (dto.getPurchaseRequisitionId() != null) {
            quotation.setPurchaseRequisition(entityManager.getReference(PurchaseRequisition.class, dto.getPurchaseRequisitionId()));
        }

        // সাধারণ ফিল্ড ম্যাপিং
        quotation.setProductName(dto.getProductName());
        quotation.setLeadTimeDays(dto.getLeadTimeDays());
        quotation.setSelected(dto.isSelected());
        quotation.setProductDescription(dto.getProductDescription());
        quotation.setUnitPrice(dto.getUnitPrice());
        quotation.setQuantity(dto.getQuantity());
        quotation.setWarranty(dto.getWarranty());
        quotation.setNotes(dto.getNotes());
        quotation.setAttachmentUrl(dto.getAttachmentUrl());

        // Enum হ্যান্ডলিং
        if (dto.getStatus() != null) {
            quotation.setStatus(QuotationStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        return quotation;
    }


    public QuotationResponseDTO toResponseDTO(Quotation quotation) {
        if (quotation == null) {
            return null;
        }

        QuotationResponseDTO dto = new QuotationResponseDTO();

        // বেসিক ইনফরমেশন ম্যাপিং
        dto.setId(quotation.getId());
        dto.setQuotationNumber(quotation.getQuotationNumber());
        dto.setValidUntil(quotation.getValidUntil());
        dto.setLeadTimeDays(quotation.getLeadTimeDays());
        dto.setSelected(quotation.isSelected());
        dto.setReceivedAt(quotation.getReceivedAt());
        dto.setStatus(quotation.getStatus());
        dto.setProductDescription(quotation.getProductDescription());
        dto.setUnitPrice(quotation.getUnitPrice());
        dto.setQuantity(quotation.getQuantity());
        dto.setTotalPrice(quotation.getTotalPrice()); // এন্টিটির @PrePersist/@PreUpdate থেকে ক্যালকুলেটেড ভ্যালু
        dto.setDeliveryTime(quotation.getDeliveryTime());
        dto.setWarranty(quotation.getWarranty());
        dto.setNotes(quotation.getNotes());
        dto.setAttachmentUrl(quotation.getAttachmentUrl());
        dto.setCreatedAt(quotation.getCreatedAt());

        // Supplier Details Flattening
        if (quotation.getSupplier() != null) {
            dto.setSupplierId(quotation.getSupplier().getId());
            dto.setSupplierName(quotation.getSupplier().getName()); // ধরে নেওয়া হয়েছে Supplier এন্টিটিতে 'name' ফিল্ড আছে
        }

        // Product Details Flattening
        if (quotation.getProduct() != null) {
            dto.setProductIds(quotation.getProduct().getId());
            dto.setProductName(quotation.getProductName()); // কোটেশন এন্টিটির ওয়ান-টাইম স্ন্যাপশট নেম
        }

        // Purchase Requisition Flattening
        if (quotation.getPurchaseRequisition() != null) {
            dto.setPurchaseRequisitionId(quotation.getPurchaseRequisition().getId());
        }

        return dto;
    }

    /**
     * Update Entity from DTO: বিদ্যমান কোটেশন আপডেট করার জন্য (PUT/PATCH রিকোয়েস্টের জন্য উপযোগী)
     */
    public void updateEntityFromDTO(QuotationRequestDTO dto, Quotation quotation) {
        if (dto == null || quotation == null) {
            return;
        }

        if (dto.getValidUntil() != null) {
            quotation.setValidUntil(LocalDate.parse(dto.getValidUntil(), DATE_FORMATTER));
        }
        if (dto.getReceivedAt() != null) {
            quotation.setReceivedAt(LocalDate.parse(dto.getReceivedAt(), DATE_FORMATTER));
        }
        if (dto.getDeliveryTime() != null) {
            quotation.setDeliveryTime(LocalDate.parse(dto.getDeliveryTime(), DATE_FORMATTER));
        }

        if (dto.getSupplierId() != null) {
            quotation.setSupplier(entityManager.getReference(Supplier.class, dto.getSupplierId()));
        }
        if (dto.getProductIds() != null) {
            quotation.setProduct(entityManager.getReference(Product.class, dto.getProductIds()));
        }
        if (dto.getPurchaseRequisitionId() != null) {
            quotation.setPurchaseRequisition(entityManager.getReference(PurchaseRequisition.class, dto.getPurchaseRequisitionId()));
        }

        quotation.setProductName(dto.getProductName());
        quotation.setLeadTimeDays(dto.getLeadTimeDays());
        quotation.setSelected(dto.isSelected());
        quotation.setProductDescription(dto.getProductDescription());
        quotation.setUnitPrice(dto.getUnitPrice());
        quotation.setQuantity(dto.getQuantity());
        quotation.setWarranty(dto.getWarranty());
        quotation.setNotes(dto.getNotes());
        quotation.setAttachmentUrl(dto.getAttachmentUrl());

        if (dto.getStatus() != null) {
            quotation.setStatus(QuotationStatus.valueOf(dto.getStatus().toUpperCase()));
        }
    }
}