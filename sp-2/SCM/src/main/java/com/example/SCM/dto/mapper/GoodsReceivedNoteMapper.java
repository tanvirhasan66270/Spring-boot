package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.GoodsReceivedNoteRequestDTO;
import com.example.SCM.dto.response.GoodsReceivedNoteResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.enumClass.GRNStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class GoodsReceivedNoteMapper {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 1. GoodsReceivedNote Entity থেকে GoodsReceivedNoteResponseDTO-তে রূপান্তর (Flattening Relation)
     * 💡 অবজেক্ট গ্রাফ ভেঙে সম্পূর্ণ ফ্ল্যাট ডেটা তৈরি করে যাতে ফ্রন্টঅ্যান্ড গ্রিডে সরাসরি বাইন্ড করা যায়।
     */
    public GoodsReceivedNoteResponseDTO toResponseDTO(GoodsReceivedNote grn) {
        if (grn == null) {
            return null;
        }

        GoodsReceivedNoteResponseDTO dto = new GoodsReceivedNoteResponseDTO();

        // বেসিক ফিল্ডস ম্যাপিং
        dto.setId(grn.getId());
        dto.setGrnNumber(grn.getGrnNumber());
        dto.setQuantity(grn.getQuantity());
        dto.setReceivedQuantity(grn.getReceivedQuantity());
        dto.setReceivedAt(grn.getReceivedAt());
        dto.setStatus(grn.getStatus());
        dto.setRemarks(grn.getRemarks());
        dto.setInspectionDate(grn.getInspectionDate());
        dto.setCreatedAt(grn.getCreatedAt());
        dto.setUpdatedAt(grn.getUpdatedAt());

        // --- 📦 Purchase Order Details Flattening ---
        if (grn.getPurchaseOrder() != null) {
            dto.setPoId(grn.getPurchaseOrder().getId());
            dto.setPoNumber(grn.getPurchaseOrder().getPoNumber());
        }

        // --- 🧵 Product Details Flattening ---
        if (grn.getProduct() != null) {
            dto.setProductId(grn.getProduct().getId());
            dto.setProductName(grn.getProduct().getName());
        }

        // --- 🏢 Warehouse Details Flattening ---
        if (grn.getWarehouse() != null) {
            dto.setWarehouseId(grn.getWarehouse().getId());
            dto.setWarehouseName(grn.getWarehouse().getName());
        }

        // --- 🔑 Received By User Details Flattening ---
        if (grn.getReceivedBy() != null) {
            dto.setReceivedBy(grn.getReceivedBy().getId());
            dto.setReceivedByName(grn.getReceivedBy().getName());
        }

        // --- 🕵️‍♂️ Inspected By User Details Flattening ---
        if (grn.getInspectedBy() != null) {
            dto.setInspectedBy(grn.getInspectedBy().getId());
            dto.setInspectedByName(grn.getInspectedBy().getName());
        }

        return dto;
    }

    /**
     * 2. Request DTO এবং অ্যাসোসিয়েটেড Entities থেকে নতুন GoodsReceivedNote Entity-তে রূপান্তর (Create Operation)
     * 💡 নোট: grnNumber এবং PO থেকে quantity অটো-লোডিং লজিকটি সার্ভিস ইমপ্লিমেন্টেশনে সেট হবে।
     */
    public GoodsReceivedNote toEntity(GoodsReceivedNoteRequestDTO dto, PurchaseOrder po, Product product, User receivedBy, Warehouse warehouse, User inspectedBy) {
        if (dto == null) {
            return null;
        }

        GoodsReceivedNote grn = new GoodsReceivedNote();

        // কোর ডাটা ফিল্ডস ম্যাপিং
        grn.setReceivedQuantity(dto.getReceivedQuantity());
        grn.setRemarks(dto.getRemarks());

        // String -> LocalDate ডেট কনভার্সন (নাল ও ব্ল্যাঙ্ক প্রোটেকশনসহ)
        if (dto.getReceivedAt() != null && !dto.getReceivedAt().trim().isEmpty()) {
            grn.setReceivedAt(LocalDate.parse(dto.getReceivedAt(), dateFormatter));
        } else {
            grn.setReceivedAt(LocalDate.now()); // ডিফল্ট কারেন্ট ডেট
        }

        if (dto.getInspectionDate() != null && !dto.getInspectionDate().trim().isEmpty()) {
            grn.setInspectionDate(LocalDate.parse(dto.getInspectionDate(), dateFormatter));
        }

        // Enum Status Mapping (String -> Enum)
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            grn.setStatus(GRNStatus.valueOf(dto.getStatus().toUpperCase()));
        } else {
            grn.setStatus(GRNStatus.PENDING); // ডিফল্ট স্ট্যাটাস PENDING
        }

        // ফরেন কি/রিলেশন অবজেক্ট ইনজেক্ট করা
        grn.setPurchaseOrder(po);
        grn.setProduct(product);
        grn.setReceivedBy(receivedBy);
        grn.setWarehouse(warehouse);
        grn.setInspectedBy(inspectedBy);

        return grn;
    }

    /**
     * 3. এক্সিস্টিং GoodsReceivedNote Entity আপডেট করার লজিক (Update Operation)
     */
    public void updateEntity(GoodsReceivedNoteRequestDTO dto, GoodsReceivedNote grn, PurchaseOrder po, Product product, Warehouse warehouse, User inspectedBy) {
        if (dto == null || grn == null) {
            return;
        }

        // কোর প্রোফাইল ফিল্ডস আপডেট
        grn.setReceivedQuantity(dto.getReceivedQuantity());
        grn.setRemarks(dto.getRemarks());

        // ডেট ফিল্ডস আপডেট
        if (dto.getReceivedAt() != null && !dto.getReceivedAt().trim().isEmpty()) {
            grn.setReceivedAt(LocalDate.parse(dto.getReceivedAt(), dateFormatter));
        }
        if (dto.getInspectionDate() != null && !dto.getInspectionDate().trim().isEmpty()) {
            grn.setInspectionDate(LocalDate.parse(dto.getInspectionDate(), dateFormatter));
        }

        // এনাম ফিল্ডস আপডেট
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            grn.setStatus(GRNStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        // রিলেশনাল অবজেক্ট চয়েস অ্যাসাইনমেন্ট (নাল সেফটিসহ)
        if (po != null) grn.setPurchaseOrder(po);
        if (product != null) grn.setProduct(product);
        if (warehouse != null) grn.setWarehouse(warehouse);
        if (inspectedBy != null) grn.setInspectedBy(inspectedBy);
    }
}