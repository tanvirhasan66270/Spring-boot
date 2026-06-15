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

    // ফ্রন্টএন্ড থেকে আসা "YYYY-MM-DD" স্ট্রিং ডেট পার্স করার জন্য ডেট ফরমেটার
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 1. RequestDTO, Supplier, এবং PurchaseRequisition থেকে নতুন PurchaseOrder Entity-তে রূপান্তর (Create Operation)
     */
    public PurchaseOrder toEntity(PurchaseOrderRequestDTO dto, Supplier supplier, PurchaseRequisition purchaseRequisition) {
        if (dto == null) {
            return null;
        }

        PurchaseOrder po = new PurchaseOrder();

        // ফ্রন্টএন্ড থেকে poNumber পাঠানো হলে সেট হবে, না হলে এনটিটির @PrePersist অটো জেনারেট করবে
        if (dto.getPoNumber() != null && !dto.getPoNumber().trim().isEmpty()) {
            po.setPoNumber(dto.getPoNumber());
        }

        po.setIssuedBy(dto.getIssuedBy());
        po.setCurrency("USD"); // ডিফল্ট কারেন্সি সেটআপ

        // প্রাথমিক অবস্থায় টোটাল অ্যামাউন্ট $0.0 সেট হবে, যা পরে চাইল্ড আইটেমের মাধ্যমে ক্যালকুলেট হবে
        po.setTotalAmount(0.0);

        // Expected Delivery Date (String -> LocalDate কনভার্সন)
        if (dto.getExpectedDeliveryDate() != null && !dto.getExpectedDeliveryDate().trim().isEmpty()) {
            po.setExpectedDeliveryDate(LocalDate.parse(dto.getExpectedDeliveryDate(), dateFormatter));
        }

        // Status Mapping (String -> Enum)
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            po.setStatus(PurchaseOrderStatus.valueOf(dto.getStatus().toUpperCase()));
        } else {
            po.setStatus(PurchaseOrderStatus.DRAFT); // ডিফল্ট স্ট্যাটাস DRAFT
        }

        // রিলেশনাল ফরেন অবজেক্টগুলো ইনজেক্ট করা
        po.setSupplier(supplier);
        po.setPurchaseRequisition(purchaseRequisition);

        return po;
    }

    /**
     * 2. PurchaseOrder Entity থেকে PurchaseOrderResponseDTO-তে রূপান্তর (Read Operations)
     * এটি অবজেক্ট গ্রাফ ভেঙে ফ্ল্যাট ডাটা (Flattened Data) তৈরি করে যাতে UI গ্রিডে সহজে রেন্ডার করা যায়।
     */
    public PurchaseOrderResponseDTO toResponseDTO(PurchaseOrder po) {
        if (po == null) {
            return null;
        }

        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();
        dto.setId(po.getId());
        dto.setPoNumber(po.getPoNumber());
        dto.setIssuedBy(po.getIssuedBy());
        dto.setTotalAmount(po.getTotalAmount()); // ডাটাবেজ থেকে সরাসরি মূল অ্যামাউন্ট সেট হবে
        dto.setCurrency(po.getCurrency());
        dto.setExpectedDeliveryDate(po.getExpectedDeliveryDate());
        dto.setStatus(po.getStatus());
        dto.setCreatedAt(po.getCreatedAt());
        dto.setUpdatedAt(po.getUpdatedAt());

        // Supplier Details Flattening (নাল সেফটিসহ)
        if (po.getSupplier() != null) {
            Supplier sup = po.getSupplier();
            dto.setSupplierId(sup.getId());
            dto.setSupplierName(sup.getName()); // ফ্রন্টএন্ড টেবিল সরাসরি নাম পেয়ে যাবে
        }

        // Purchase Requisition Details Flattening
        if (po.getPurchaseRequisition() != null) {
            dto.setPurchaseRequisitionId(po.getPurchaseRequisition().getId());
        }

        return dto;
    }

    /**
     * 3. এক্সিস্টিং PurchaseOrder Entity-কে RequestDTO এবং নতুন অবজেক্ট দিয়ে আপডেট করা (Update Operation)
     */
    public void updateEntity(PurchaseOrderRequestDTO dto, PurchaseOrder po, Supplier supplier, PurchaseRequisition purchaseRequisition) {
        if (dto == null || po == null) {
            return;
        }

        if (dto.getPoNumber() != null) po.setPoNumber(dto.getPoNumber());
        if (dto.getIssuedBy() != null) po.setIssuedBy(dto.getIssuedBy());

        if (dto.getExpectedDeliveryDate() != null && !dto.getExpectedDeliveryDate().trim().isEmpty()) {
            po.setExpectedDeliveryDate(LocalDate.parse(dto.getExpectedDeliveryDate(), dateFormatter));
        }

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            po.setStatus(PurchaseOrderStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        // রিলেশন মডিফিকেশন আপডেট
        if (supplier != null) {
            po.setSupplier(supplier);
        }
        if (purchaseRequisition != null) {
            po.setPurchaseRequisition(purchaseRequisition);
        }

        // নোট: টোটাল অ্যামাউন্টের ডাইনামিক আপডেট লাইন আইটেম সার্ভিসের ওল্ড/নিউ রোল-আপ কুয়েরি দ্বারা নিয়ন্ত্রিত হবে।
    }
}