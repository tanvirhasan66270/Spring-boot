package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.POLineItemRequestDTO;
import com.example.SCM.dto.response.POLineItemResponseDTO;
import com.example.SCM.entity.POLineItem;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.PurchaseOrder;
import com.example.SCM.enumClass.POLineItemStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class POLineItemMapper {

    //"YYYY-MM-DD" স্ট্রিং
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public POLineItem toEntity(POLineItemRequestDTO dto, PurchaseOrder purchaseOrder, Product product) {
        if (dto == null) {
            return null;
        }

        POLineItem item = new POLineItem();
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        item.setQuotationRef(dto.getQuotationRef());
        item.setPoNumber(dto.getPoNumber());
        item.setShipmentMethod(dto.getShipmentMethod());
        item.setNotes(dto.getNotes());

        // Delivery Date Conversion (String -> LocalDate)
        if (dto.getDeliveryDate() != null && !dto.getDeliveryDate().trim().isEmpty()) {
            item.setDeliveryDate(LocalDate.parse(dto.getDeliveryDate(), dateFormatter));
        }

        // Status Mapping (String -> Enum)
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            item.setStatus(POLineItemStatus.valueOf(dto.getStatus().toUpperCase()));
        } else {
            item.setStatus(POLineItemStatus.PENDING); // ডিফল্ট স্ট্যাটাস PENDING সেট হবে
        }

        // রিলেশনাল ফরেন অবজেক্টগুলো ইনজেক্ট করা
        item.setPurchaseOrder(purchaseOrder);
        item.setProduct(product);

        // নোট: lineTotal এর ক্যালকুলেশনটি এনটিটির @PrePersist বা @PreUpdate মেথডের মাধ্যমে অটোমেটিক সম্পন্ন হবে।
        return item;
    }


    public POLineItemResponseDTO toResponseDTO(POLineItem item) {
        if (item == null) {
            return null;
        }

        POLineItemResponseDTO dto = new POLineItemResponseDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setLineTotal(item.getLineTotal()); // এনটিটি লেভেলের অটো ক্যালকুলেটেড ভ্যালু (@PrePersist/PreUpdate থেকে)
        dto.setQuotationRef(item.getQuotationRef());
        dto.setPoNumber(item.getPoNumber());
        dto.setDeliveryDate(item.getDeliveryDate());
        dto.setShipmentMethod(item.getShipmentMethod());
        dto.setTrackingNumber(item.getTrackingNumber());
        dto.setNotes(item.getNotes());
        dto.setStatus(item.getStatus());
        dto.setCreatedAt(item.getCreatedAt());

        // Parent PurchaseOrder Details Flattening
        if (item.getPurchaseOrder() != null) {
            PurchaseOrder po = item.getPurchaseOrder();
            dto.setPoId(po.getId());

            // যেহেতু এনটিটি থেকে সরাসরি লিস্ট বা গ্র্যান্ড টোটাল বাদ গেছে, তাই সার্ভিস লেয়ার থেকে
            // এই ভ্যালুটি কাস্টম কুয়েরি বা সামেশনের মাধ্যমে রিয়েল-টাইম ক্যালকুলেট হয়ে ডাটাবেজ থেকে রিফ্লেক্ট করবে।
            dto.setTotalAmount(po.getTotalAmount());
        }

        // Product Details Flattening (নাল সেফটিসহ)
        if (item.getProduct() != null) {
            Product prod = item.getProduct();
            dto.setProductId(prod.getId());
            dto.setProductName(prod.getName());      // ফ্রন্টএন্ড ড্যাশবোর্ডে সরাসরি নাম দেখানোর জন্য
            dto.setProductCode(prod.getProductCode());  // কাস্টম প্রোডাক্ট কোড শো করার জন্য
        }

        return dto;
    }


    public void updateEntity(POLineItemRequestDTO dto, POLineItem item, Product product) {
        if (dto == null || item == null) {
            return;
        }

        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());

        if (dto.getQuotationRef() != null) item.setQuotationRef(dto.getQuotationRef());
        if (dto.getPoNumber() != null) item.setPoNumber(dto.getPoNumber());
        if (dto.getShipmentMethod() != null) item.setShipmentMethod(dto.getShipmentMethod());
        if (dto.getNotes() != null) item.setNotes(dto.getNotes());

        if (dto.getDeliveryDate() != null && !dto.getDeliveryDate().trim().isEmpty()) {
            item.setDeliveryDate(LocalDate.parse(dto.getDeliveryDate(), dateFormatter));
        }

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            item.setStatus(POLineItemStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        // যদি প্রোডাক্ট পরিবর্তন করার রিকোয়ারমেন্ট থাকে
        if (product != null) {
            item.setProduct(product);
        }
    }
}