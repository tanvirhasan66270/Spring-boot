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

    public PurchaseOrderResponseDTO convertTOResponseDTO(PurchaseOrder po) {
        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();

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

        if (po.getSupplier() != null) {
            dto.setSupplierId(po.getSupplier().getId());
            dto.setSupplierName(po.getSupplier().getName());
            dto.setSupplierEmail(po.getSupplier().getEmail());
        }

        if (po.getPurchaseRequisition() != null) {
            dto.setPurchaseRequisitionId(po.getPurchaseRequisition().getId());
        }

        if (po.getQuotation() != null) {
            dto.setQuotationId(po.getQuotation().getId());
        }

        return dto;
    }

    public PurchaseOrder toEntity(PurchaseOrderRequestDTO dto, Quotation quotation, Supplier supplier, PurchaseRequisition pr) {
        PurchaseOrder po = new PurchaseOrder();

        po.setTotalAmount(dto.getTotalAmount());
        po.setIssuedBy(dto.getIssuedBy());
        po.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "USD");

        if (quotation != null && quotation.getQuantity() != null) {
            po.setQuantity(quotation.getQuantity());
        }

        if (dto.getExpectedDeliveryDate() != null && !dto.getExpectedDeliveryDate().trim().isEmpty()) {
            po.setExpectedDeliveryDate(LocalDate.parse(dto.getExpectedDeliveryDate(), dateFormatter));
        }

        // ⚠️ নতুন PO সবসময় DRAFT-এ তৈরি হবে — dto.getStatus() থেকে কখনোই সেট হবে না।
        // অন্যথায় কেউ চাইলে creation-এই status="ISSUED" পাঠিয়ে পুরো অ্যাপ্রুভাল ফ্লো বাইপাস করতে পারত।
        po.setStatus(PurchaseOrderStatus.DRAFT);

        po.setQuotation(quotation);
        po.setSupplier(supplier);
        po.setPurchaseRequisition(pr);

        return po;
    }

    public void updateEntity(PurchaseOrderRequestDTO dto, PurchaseOrder po, Quotation quotation, Supplier supplier, PurchaseRequisition pr) {
        if (dto == null || po == null) {
            return;
        }

        po.setTotalAmount(dto.getTotalAmount());
        po.setIssuedBy(dto.getIssuedBy());

        if (dto.getCurrency() != null) {
            po.setCurrency(dto.getCurrency());
        }

        if (quotation != null && quotation.getQuantity() != null) {
            po.setQuantity(quotation.getQuantity());
        }

        if (dto.getExpectedDeliveryDate() != null && !dto.getExpectedDeliveryDate().trim().isEmpty()) {
            po.setExpectedDeliveryDate(LocalDate.parse(dto.getExpectedDeliveryDate(), dateFormatter));
        }

        //  dto.getStatus() থেকে সরাসরি status সেট করা যাবে না।
        // PurchaseOrder-এর স্ট্যাটাস-পরিবর্তন শুধুমাত্র নির্দিষ্ট, নিয়ন্ত্রিত মেথডের মাধ্যমে হবে:
        //   - managerIssuedOrderByToken(token)  -> DRAFT to ISSUED
        //   - supplierReceivedOrder(token)      -> ISSUED to RECEIVED
        //   - updateShipmentQuantityCheck(id,q) -> PARTIALLY_RECEIVED / RECEIVED
        // এই জেনারেল updateEntity() মেথড দিয়ে status বদলানো একটা নিরাপত্তা/ওয়ার্কফ্লো-বাইপাস
        // দুর্বলতা তৈরি করত, তাই এই ব্লকটা বাদ দেওয়া হলো।

        if (quotation != null) po.setQuotation(quotation);
        if (supplier != null) po.setSupplier(supplier);
        if (pr != null) po.setPurchaseRequisition(pr);
    }
}