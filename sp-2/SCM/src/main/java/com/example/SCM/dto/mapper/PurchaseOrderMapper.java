package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import com.example.SCM.entity.PurchaseOrder;
import com.example.SCM.entity.PurchaseRequisition;
import com.example.SCM.entity.Supplier;
import com.example.SCM.enumClass.PurchaseOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor // 💡 চাইল্ড POLineItemMapper-কে অটোমেটিক ইনজেক্ট করার জন্য
public class PurchaseOrderMapper {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final POLineItemMapper poLineItemMapper; // 💡 চাইল্ড কনভার্সনের জন্য ইনজেকশন

    /**
     * RequestDTO, Supplier, এবং PurchaseRequisition অবজেক্ট থেকে নতুন PurchaseOrder Entity-তে রূপান্তর (Create Operation)
     */
    public PurchaseOrder toEntity(PurchaseOrderRequestDTO dto, Supplier supplier, PurchaseRequisition purchaseRequisition) {
        if (dto == null) {
            return null;
        }

        PurchaseOrder po = new PurchaseOrder();

        if (dto.getPoNumber() != null && !dto.getPoNumber().trim().isEmpty()) {
            po.setPoNumber(dto.getPoNumber());
        }

        po.setIssuedBy(dto.getIssuedBy());
        po.setCurrency("USD");

        // 💡 নতুন ফিল্ড ও রোল-আপ লজিকের বেস ডেটা সেটআপ
        po.setGrandTotal(0.0);
        po.setTotalAmount(0.0);

        // Expected Delivery Date (String -> LocalDate)
        if (dto.getExpectedDeliveryDate() != null && !dto.getExpectedDeliveryDate().trim().isEmpty()) {
            po.setExpectedDeliveryDate(LocalDate.parse(dto.getExpectedDeliveryDate(), dateFormatter));
        }

        // Status Mapping (String -> Enum)
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            po.setStatus(PurchaseOrderStatus.valueOf(dto.getStatus().toUpperCase()));
        } else {
            po.setStatus(PurchaseOrderStatus.DRAFT);
        }

        // রিলেশনাল ফরেন অবজেক্টগুলো ইনজেক্ট করা
        po.setSupplier(supplier);
        po.setPurchaseRequisition(purchaseRequisition);

        // 💡 যদি রিকোয়েস্টের সাথেই চাইল্ড লাইন আইটেমগুলো আসে, তবে সেগুলোকে ম্যাপ করে প্যারেন্টে সেট করা
        if (dto.getLineItems() != null && !dto.getLineItems().isEmpty()) {
            po.setLineItems(dto.getLineItems().stream()
                    .map(itemDto -> {
                        var item = poLineItemMapper.toEntity(itemDto, po, null); // প্রোডাক্ট সার্ভিস লেয়ার থেকে পরে অ্যাসাইন হবে
                        item.setPurchaseOrder(po); // Bi-directional Link স্থাপন
                        return item;
                    }).collect(Collectors.toList()));
        }

        return po;
    }

    /**
     * PurchaseOrder Entity থেকে PurchaseOrderResponseDTO-তে রূপান্তর (Read Operations)
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
        dto.setGrandTotal(po.getGrandTotal()); // 💡 নতুন যুক্ত হওয়া গ্র্যান্ড টোটাল ফিল্ড ম্যাপ করা হলো
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

            // আপনার ResponseDTO-তে ফিল্ডের নাম অনুযায়ী ভ্যালু সেটআপ
            // নোট: আপনার ResponseDTO-তে যদি requisitionUrgencyLevel ফিল্ডটি থেকে থাকে
        }

        // 💡 চাইল্ড লাইন আইটেমগুলোর লিস্ট রূপান্তর (যা আপনার DTO-তে ছিল কিন্তু ম্যাপারে বাদ পড়েছিল)
        if (po.getLineItems() != null && !po.getLineItems().isEmpty()) {
            dto.setLineItems(po.getLineItems().stream()
                    .map(poLineItemMapper::toResponseDTO) // চাইল্ড ম্যাপার দিয়ে ওয়ান-বাই-ওয়ান রূপান্তর
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * এক্সিস্টিং PurchaseOrder Entity-কে RequestDTO এবং নতুন অবজেক্ট দিয়ে আপডেট করা (Update Operation)
     */
    public void updateEntity(PurchaseOrderRequestDTO dto, PurchaseOrder po, Supplier supplier, PurchaseRequisition purchaseRequisition) {
        if (dto == null || po == null) {
            return;
        }

        if (dto.getPoNumber() != null) po.setPoNumber(dto.getPoNumber());
        if (dto.getIssuedBy() != null) po.setIssuedBy(dto.getIssuedBy());

        po.setCurrency("USD");

        if (dto.getExpectedDeliveryDate() != null && !dto.getExpectedDeliveryDate().trim().isEmpty()) {
            po.setExpectedDeliveryDate(LocalDate.parse(dto.getExpectedDeliveryDate(), dateFormatter));
        }

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            po.setStatus(PurchaseOrderStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        if (supplier != null) {
            po.setSupplier(supplier);
        }
        if (purchaseRequisition != null) {
            po.setPurchaseRequisition(purchaseRequisition);
        }

        // নোট: গ্র্যান্ড টোটাল ও লাইন আইটেমের অ্যামাউন্ট সার্ভিস লেয়ারের রোল-আপ মেথডের মাধ্যমে ডাইনামিকালি আপডেট হবে।
    }
}