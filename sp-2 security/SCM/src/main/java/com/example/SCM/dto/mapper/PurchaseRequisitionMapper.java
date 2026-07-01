package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.PurchaseRequisitionRequestDTO;
import com.example.SCM.dto.response.PurchaseRequisitionResponseDTO;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.PurchaseRequisition;
import com.example.SCM.entity.Supplier;
import com.example.SCM.enumClass.PurchaseRequisitionStatus;
import com.example.SCM.enumClass.UrgencyLevel;
import com.example.SCM.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseRequisitionMapper {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final UserRepository userRepository;

    // ম্যানুয়াল কনস্ট্রাক্টর ইনজেকশন
    public PurchaseRequisitionMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PurchaseRequisition toEntity(PurchaseRequisitionRequestDTO dto, List<Product> products, List<Supplier> suppliers) {
        if (dto == null) return null;

        PurchaseRequisition pr = new PurchaseRequisition();
        pr.setRequestedBy(dto.getRequestedBy());

        // কারেন্সি নাল আসলে ডিফল্ট "USD" সেট হবে
        pr.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "USD");
        pr.setQuantityRequired(dto.getQuantityRequired());
        pr.setRemarks(dto.getRemarks());

        // String থেকে LocalDate-এ রূপান্তর
        if (dto.getRequiredByDate() != null && !dto.getRequiredByDate().trim().isEmpty()) {
            pr.setRequiredByDate(LocalDate.parse(dto.getRequiredByDate(), dateFormatter));
        }

        // Frontend থেকে আসা String-কে UrgencyLevel Enum-এ রূপান্তর (নাল সেফটিসহ)
        if (dto.getUrgencyLevel() != null) {
            pr.setUrgencyLevel(UrgencyLevel.valueOf(dto.getUrgencyLevel().toUpperCase()));
        }

        // Many-to-Many রিলেশনের অবজেক্ট লিস্ট ইনজেক্ট করা
        pr.setProducts(products);
        pr.setSuppliers(suppliers);

        // নতুন PR তৈরির সময় স্ট্যাটাস ডিফল্ট PENDING থাকবে
        pr.setApprovalStatus(PurchaseRequisitionStatus.PENDING);

        return pr;
    }


    public PurchaseRequisitionResponseDTO convertTOResponseDTO(PurchaseRequisition pr) {
        if (pr == null) return null;

        PurchaseRequisitionResponseDTO dto = new PurchaseRequisitionResponseDTO();
        dto.setId(pr.getId());
        dto.setRequestedBy(pr.getRequestedBy());
        dto.setCurrency(pr.getCurrency());
        dto.setQuantityRequired(pr.getQuantityRequired());
        dto.setUrgencyLevel(pr.getUrgencyLevel());
        dto.setRequiredByDate(pr.getRequiredByDate());
        dto.setApprovalStatus(pr.getApprovalStatus());
        dto.setRemarks(pr.getRemarks());
        dto.setCreatedAt(pr.getCreatedAt());

        if (pr.getApprovedBy() != null) {
            dto.setApprovedBy(pr.getApprovedBy());
            userRepository.findById(pr.getApprovedBy()).ifPresent(user -> {
                dto.setApprovedByName(user.getName()); // রেসপন্সে ম্যানেজারের নাম সেট হবে
            });
        }

        if (pr.getProducts() != null && !pr.getProducts().isEmpty()) {
            dto.setProductIds(pr.getProducts().stream()
                    .map(Product::getId)
                    .collect(Collectors.toList()));

            dto.setProductNames(pr.getProducts().stream()
                    .map(Product::getName)
                    .collect(Collectors.toList()));
        }

        // Supplier List Flattening (আইডি এবং নামের আলাদা তালিকা তৈরি)
        if (pr.getSuppliers() != null && !pr.getSuppliers().isEmpty()) {
            dto.setSupplierIds(pr.getSuppliers().stream()
                    .map(Supplier::getId)
                    .collect(Collectors.toList()));

            dto.setSupplierNames(pr.getSuppliers().stream()
                    .map(Supplier::getName)
                    .collect(Collectors.toList()));
        }

        return dto;
    }


    public void updateEntity(PurchaseRequisitionRequestDTO dto, PurchaseRequisition pr, List<Product> products, List<Supplier> suppliers) {
        if (dto == null || pr == null) {
            return;
        }

        if (dto.getRequestedBy() != null) pr.setRequestedBy(dto.getRequestedBy());
        if (dto.getCurrency() != null) pr.setCurrency(dto.getCurrency());

        pr.setQuantityRequired(dto.getQuantityRequired());

        if (dto.getRemarks() != null) pr.setRemarks(dto.getRemarks());

        if (dto.getRequiredByDate() != null && !dto.getRequiredByDate().trim().isEmpty()) {
            pr.setRequiredByDate(LocalDate.parse(dto.getRequiredByDate(), dateFormatter));
        }

        if (dto.getUrgencyLevel() != null) {
            pr.setUrgencyLevel(UrgencyLevel.valueOf(dto.getUrgencyLevel().toUpperCase()));
        }

        // আপডেটেড Many-to-Many লিস্ট সেট করা
        if (products != null) {
            pr.setProducts(products);
        }
        if (suppliers != null) {
            pr.setSuppliers(suppliers);
        }
    }
}