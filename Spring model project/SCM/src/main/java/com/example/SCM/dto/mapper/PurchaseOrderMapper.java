package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import com.example.SCM.entity.PurchaseOrder;
import com.example.SCM.entity.PurchaseRequisition;
import com.example.SCM.entity.Quotation;
import com.example.SCM.entity.Supplier;
import com.example.SCM.enumClass.PurchaseOrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PurchaseOrderMapper {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 1. PurchaseOrder Entity -> PurchaseOrderResponseDTO (Flattening Operations)
     * অবজেক্ট চেইন ভেঙে ফ্ল্যাট জেসন ডেটা তৈরি করে যাতে ফ্রন্টঅ্যান্ড গ্রিডে সরাসরি বাইন্ড করা যায়।
     */
    public PurchaseOrderResponseDTO toResponseDTO(PurchaseOrder po) {
        if (po == null) {
            return null;
        }

        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();

        // কোর প্রোফাইল ফিল্ডস ম্যাপিং
        dto.setId(po.getId());
        dto.setPoNumber(po.getPoNumber());
        dto.setQuantity(po.getQuantity());
        dto.setTotalAmount(po.getTotalAmount());
        dto.setCurrency(po.getCurrency());
        dto.setExpectedDeliveryDate(po.getExpectedDeliveryDate());
        dto.setStatus(po.getStatus());
        dto.setIssuedBy(po.getIssuedBy());
        dto.setCreatedAt(po.getCreatedAt());
        dto.setUpdatedAt(po.getUpdatedAt());

        // 🔗 Supplier Details Flattening (কোটিশন টেবিল হয়ে আসা সাপ্লায়ার নাম)
        if (po.getSupplier() != null) {
            dto.setSupplierId(po.getSupplier().getId());
            // যদি সাপ্লায়ারের নাম User টেবিল থেকে আসে তবে po.getSupplier().getUser().getName() ব্যবহার করবেন
            dto.setSupplierName(po.getSupplier().getName());
        }

        // 🔗 Purchase Requisition Details Flattening
        if (po.getPurchaseRequisition() != null) {
            dto.setPurchaseRequisitionId(po.getPurchaseRequisition().getId());
        }

        // 🔗 Original Quotation ID Flattening
        if (po.getQuotation() != null) {
            dto.setQuotationId(po.getQuotation().getId());
        }

        return dto;
    }

    /**
     * 2. Request DTO এবং অটো-লোডেড রিলেশনাল Entities থেকে নতুন PurchaseOrder Entity তৈরি (Create Operation)
     */
    public PurchaseOrder toEntity(PurchaseOrderRequestDTO dto, Quotation quotation, Supplier supplier, PurchaseRequisition pr) {
        if (dto == null) {
            return null;
        }

        PurchaseOrder po = new PurchaseOrder();

        // DTO থেকে ইনপুট ফিল্ডস সেট করা

        po.setTotalAmount(dto.getTotalAmount());
        po.setIssuedBy(dto.getIssuedBy());
        po.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "USD");

        // 💡 অটো-লোডিং মেকানিজম: কোটেশন টেবিল থেকে কোয়ান্টিটি সরাসরি এসাইন করা হলো
        if (quotation != null && quotation.getQuantity() != null) {
            // আপনার Quotation এনটিটির quantity টাইপ যদি Integer না হয়ে String হয়, তবে Integer.parseInt() করে নেবেন
            po.setQuantity(quotation.getQuantity());
        }

        // String -> LocalDate রূপান্তর
        if (dto.getExpectedDeliveryDate() != null && !dto.getExpectedDeliveryDate().trim().isEmpty()) {
            po.setExpectedDeliveryDate(LocalDate.parse(dto.getExpectedDeliveryDate(), dateFormatter));
        }

        // String -> Enum কাস্টিং (নাল সেফটিসহ)
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            po.setStatus(PurchaseOrderStatus.valueOf(dto.getStatus().toUpperCase()));
        } else {
            po.setStatus(PurchaseOrderStatus.DRAFT); // ডিফল্ট ডাফট স্ট্যাটাস
        }

        // সার্ভিস লেয়ার থেকে অটো-লোড হয়ে আসা অবজেক্টগুলোর ম্যাপিং লিঙ্ক
        po.setQuotation(quotation);
        po.setSupplier(supplier);
        po.setPurchaseRequisition(pr);

        return po;
    }

    /**
     * 3. এক্সিস্টিং PurchaseOrder Entity-কে RequestDTO এবং নতুন অবজেক্ট চেইন দিয়ে আপডেট করা (Update Operation)
     */
    public void updateEntity(PurchaseOrderRequestDTO dto, PurchaseOrder po, Quotation quotation, Supplier supplier, PurchaseRequisition pr) {
        if (dto == null || po == null) {
            return;
        }

        po.setTotalAmount(dto.getTotalAmount());
        po.setIssuedBy(dto.getIssuedBy());

        if (dto.getCurrency() != null) {
            po.setCurrency(dto.getCurrency());
        }

        // কোটেশন চেঞ্জ হলে কোয়ান্টিটি রি-লোডিং হ্যান্ডেলিং
        if (quotation != null && quotation.getQuantity() != null) {
            po.setQuantity(quotation.getQuantity());
        }

        // ডেট আপডেট
        if (dto.getExpectedDeliveryDate() != null && !dto.getExpectedDeliveryDate().trim().isEmpty()) {
            po.setExpectedDeliveryDate(LocalDate.parse(dto.getExpectedDeliveryDate(), dateFormatter));
        }

        // স্ট্যাটাস এনাম আপডেট
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            po.setStatus(PurchaseOrderStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        // ফরেন অবজেক্ট আপডেট চেইন
        if (quotation != null) po.setQuotation(quotation);
        if (supplier != null) po.setSupplier(supplier);
        if (pr != null) po.setPurchaseRequisition(pr);
    }
}