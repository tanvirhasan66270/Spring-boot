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

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * RequestDTO, PurchaseOrder, এবং Product অবজেক্ট থেকে নতুন POLineItem Entity-তে রূপান্তর (Create Operation)
     */
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

        // Delivery Date Mapping (String -> LocalDate)
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

    /**
     * POLineItem Entity থেকে POLineItemResponseDTO-তে রূপান্তর (Read Operations)
     * এটি চাইল্ড টেবিলের ডাটা ফ্ল্যাট করে এক লাইনে নিয়ে আসে যাতে UI গ্রিডে সহজে বাইন্ড করা যায়।
     */
    public POLineItemResponseDTO toResponseDTO(POLineItem item) {
        if (item == null) {
            return null;
        }

        POLineItemResponseDTO dto = new POLineItemResponseDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setLineTotal(item.getLineTotal()); // এনটিটি লেভেল থেকে অটো ক্যালকুলেটেড ভ্যালু আসবে
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

            // 💡 আপনার চাহিদা অনুযায়ী: প্যারেন্ট অর্ডারের কারেন্ট রোল-আপ গ্র্যান্ড টোটালটি
            // এই চাইল্ড লাইনের totalAmount প্রোপার্টিতে পাস করে দেওয়া হলো
            dto.setTotalAmount(po.getGrandTotal());
        }

        // Product Details Flattening
        if (item.getProduct() != null) {
            Product prod = item.getProduct();
            dto.setProductId(prod.getId());
            dto.setProductName(prod.getName());  // ফ্রন্টএন্ড ড্যাশবোর্ডে সরাসরি নাম দেখানোর জন্য
            dto.setProductCode(prod.getProductCode());  // কাস্টম প্রোডাক্ট কোড শো করার জন্য
        }

        return dto;
    }

    /**
     * এক্সিস্টিং POLineItem Entity-কে RequestDTO এবং নতুন অবজেক্ট দিয়ে আপডেট করা (Update Operation)
     */
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

        // যদি প্রোডাক্ট আইটেম পরিবর্তন করার কোনো রিকোয়ারমেন্ট থাকে
        if (product != null) {
            item.setProduct(product);
        }
    }
}