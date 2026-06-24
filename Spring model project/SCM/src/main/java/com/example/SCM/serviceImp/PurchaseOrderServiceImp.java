package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.PurchaseOrderMapper;
import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.repository.PurchaseOrderRepository;
import com.example.SCM.repository.QuotationRepository;
import com.example.SCM.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImp implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final QuotationRepository quotationRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    /**
     * 1. Save New Purchase Order (Create Operation)
     * 💡 জেনুইন রিলেশন চেইন লজিক: QuotationId দিয়ে কোটেশন টেবিল থেকে অটোমেটিক
     * Supplier, PurchaseRequisition, এবং quantity ব্যাকঅ্যান্ডে লোড হয়ে সেভ হবে।
     */
    @Override
    @Transactional
    public PurchaseOrderResponseDTO save(PurchaseOrderRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Purchase Order data cannot be null");
        }

        // ১. ফ্রন্টঅ্যান্ড থেকে আসা মূল Quotation আইডি কুয়েরি করা
        Quotation quotation = quotationRepository.findById(dto.getQuotationId())
                .orElseThrow(() -> new RuntimeException("Quotation not found with ID: " + dto.getQuotationId()));

        // ২. 🔗 ম্যাজিক চেইন লজিক: কোটেশনের ভেতর থেকে অটোমেটিক Supplier এবং Requisition অবজেক্ট বের করা
        // (আপনার Quotation এনটিটির ফিল্ড নেম অনুযায়ী getSupplier() এবং getPurchaseRequisition() মিলিয়ে নেবেন)
        Supplier supplier = quotation.getSupplier();
        if (supplier == null) {
            throw new RuntimeException("No Supplier is linked with the selected Quotation!");
        }

        PurchaseRequisition purchaseRequisition = quotation.getPurchaseRequisition();
        if (purchaseRequisition == null) {
            throw new RuntimeException("No Purchase Requisition is linked with the selected Quotation!");
        }

        // ৩. ম্যাপার কল করে এনটিটি জেনারেট করা (কোটেশন থেকে কোয়ান্টিটি অটো-লোডিং মেকানিজমসহ)
        PurchaseOrder po = purchaseOrderMapper.toEntity(dto, quotation, supplier, purchaseRequisition);

        // ৪. ডাটাবেজে পারচেজ অর্ডার লক/সেভ করা
        PurchaseOrder savedPo = purchaseOrderRepository.save(po);

        return purchaseOrderMapper.toResponseDTO(savedPo);
    }

    /**
     * 2. Update Existing Purchase Order (Update Operation)
     */
    @Override
    @Transactional
    public PurchaseOrderResponseDTO update(Long id, PurchaseOrderRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        // ১. এক্সিস্টিং পারচেজ অর্ডার কাস্টম Fetch Join মেথড দিয়ে লোড করা (N+1 প্রোটেকশন)
        PurchaseOrder po = purchaseOrderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + id));

        // ২. যদি আপডেটের সময় কোটেশন আইডি পরিবর্তন হয়, তবে তার ওপর ভিত্তি করে নতুন রিলেশন চেইন লোড করা
        Quotation quotation = po.getQuotation();
        Supplier supplier = po.getSupplier();
        PurchaseRequisition pr = po.getPurchaseRequisition();

        if (dto.getQuotationId() != null && !dto.getQuotationId().equals(quotation.getId())) {
            quotation = quotationRepository.findById(dto.getQuotationId())
                    .orElseThrow(() -> new RuntimeException("New selected Quotation not found with ID: " + dto.getQuotationId()));
            supplier = quotation.getSupplier();
            pr = quotation.getPurchaseRequisition();
        }

        // ৩. ম্যাপার দিয়ে এক্সিস্টিং এনটিটির স্টেট ও ডাটা চেইন রিফ্রেশ করা
        purchaseOrderMapper.updateEntity(dto, po, quotation, supplier, pr);

        PurchaseOrder updatedPo = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toResponseDTO(updatedPo);
    }

    /**
     * 3. Find All Purchase Orders (Using optimized Repository Fetch Join)
     */
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponseDTO> findAll() {
        return purchaseOrderRepository.findAllPurchaseOrders().stream()
                .map(purchaseOrderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 4. Find Purchase Order By ID with full detailed graph
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseOrderResponseDTO> getById(Long id) {
        return purchaseOrderRepository.findByIdWithDetails(id)
                .map(purchaseOrderMapper::toResponseDTO);
    }

    /**
     * 5. Hard Delete Purchase Order
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!purchaseOrderRepository.existsById(id)) {
            throw new RuntimeException("Purchase Order not found with ID: " + id);
        }
        purchaseOrderRepository.deleteById(id);
    }

}