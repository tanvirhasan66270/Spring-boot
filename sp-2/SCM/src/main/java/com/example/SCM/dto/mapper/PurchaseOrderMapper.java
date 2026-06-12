package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import com.example.SCM.entity.PurchaseOrder;
import com.example.SCM.entity.PurchaseRequisition;
import com.example.SCM.entity.Supplier;
import com.example.SCM.enumClass.PurchaseOrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PurchaseOrderMapper {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

   // RequestDTO, Supplier, এবং PurchaseRequisition অবজেক্ট থেকে নতুন PurchaseOrder Entity-তে রূপান্তর (Create Operation)

    public PurchaseOrder toEntity(PurchaseOrderRequestDTO dto, Supplier supplier, PurchaseRequisition purchaseRequisition) {
        if (dto == null) {
            return null;
        }

        PurchaseOrder po = new PurchaseOrder();

        // যদি ফ্রন্টএন্ড থেকে poNumber পাঠানো হয়, তবে সেটি সেট হবে, না হলে এনটিটির @PrePersist অটো জেনারেট করবে
        if (dto.getPoNumber() != null && !dto.getPoNumber().trim().isEmpty()) {
            po.setPoNumber(dto.getPoNumber());
        }

        po.setIssuedBy(dto.getIssuedBy());
        po.setTotalAmount(dto.getTotalAmount());
        po.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "USD");

        // Expected Delivery Date (String -> LocalDate)
        if (dto.getExpectedDeliveryDate() != null && !dto.getExpectedDeliveryDate().trim().isEmpty()) {
            po.setExpectedDeliveryDate(LocalDate.parse(dto.getExpectedDeliveryDate(), dateFormatter));
        }

        // Status Mapping (String -> Enum)
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            po.setStatus(PurchaseOrderStatus.valueOf(dto.getStatus().toUpperCase()));
        } else {
            po.setStatus(PurchaseOrderStatus.DRAFT); // ডিফল্ট স্ট্যাটাস
        }

        // রিলেশনাল ফরেন অবজেক্টগুলো ইনজেক্ট করা
        po.setSupplier(supplier);
        po.setPurchaseRequisition(purchaseRequisition);

        return po;
    }

    /**
     * PurchaseOrder Entity থেকে PurchaseOrderResponseDTO-তে রূপান্তর (Read Operations)
     * এই মেথডটি রিলেশনাল অবজেক্ট চেইন ভেঙে ফ্ল্যাট জেসন ডেটা তৈরি করে ফ্রন্টএন্ডের জন্য।
     */
    public PurchaseOrderResponseDTO toResponseDTO(PurchaseOrder po) {
        if (po == null) {
            return null;
        }

        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();
        dto.setId(po.getId());
        dto.setPoNumber(po.getPoNumber());
        dto.setIssuedBy(po.getIssuedBy());
        dto.setTotalAmount(po.getTotalAmount());
        dto.setCurrency(po.getCurrency());
        dto.setExpectedDeliveryDate(po.getExpectedDeliveryDate());
        dto.setStatus(po.getStatus());
        dto.setCreatedAt(po.getCreatedAt());
        dto.setUpdatedAt(po.getUpdatedAt());

        // Supplier Details Flattening
        if (po.getSupplier() != null) {
            Supplier sup = po.getSupplier();
            dto.setSupplierId(sup.getId());
            dto.setSupplierName(sup.getName());

        }

        // Purchase Requisition Details Flattening
        if (po.getPurchaseRequisition() != null) {
            PurchaseRequisition pr = po.getPurchaseRequisition();
            dto.setPurchaseRequisitionId(pr.getId());

            if (pr.getUrgencyLevel() != null) {
                dto.setRequisitionUrgencyLevel(pr.getUrgencyLevel().name()); // এনাম থেকে স্ট্রিং কনভার্ট
            }
        }

        return dto;
    }

    // এক্সিস্টিং PurchaseOrder Entity-কে RequestDTO এবং নতুন অবজেক্ট দিয়ে আপডেট করা (Update Operation)

    public void updateEntity(PurchaseOrderRequestDTO dto, PurchaseOrder po, Supplier supplier, PurchaseRequisition purchaseRequisition) {
        if (dto == null || po == null) {
            return;
        }

        if (dto.getPoNumber() != null) po.setPoNumber(dto.getPoNumber());
        if (dto.getIssuedBy() != null) po.setIssuedBy(dto.getIssuedBy());

        po.setTotalAmount(dto.getTotalAmount());

        if (dto.getCurrency() != null) po.setCurrency(dto.getCurrency());

        if (dto.getExpectedDeliveryDate() != null && !dto.getExpectedDeliveryDate().trim().isEmpty()) {
            po.setExpectedDeliveryDate(LocalDate.parse(dto.getExpectedDeliveryDate(), dateFormatter));
        }

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            po.setStatus(PurchaseOrderStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        // রিলেশন আপডেট
        if (supplier != null) {
            po.setSupplier(supplier);
        }
        if (purchaseRequisition != null) {
            po.setPurchaseRequisition(purchaseRequisition);
        }
    }
}