package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.PurchaseRequisitionMapper;
import com.example.SCM.dto.request.PurchaseRequisitionRequestDTO;
import com.example.SCM.dto.response.PurchaseRequisitionResponseDTO;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.PurchaseRequisition;
import com.example.SCM.entity.Supplier;
import com.example.SCM.repository.ProductRepository;
import com.example.SCM.repository.PurchaseRequisitionRepository; // আপনার প্রজেক্টের PR রিপোজিটরি ইম্পোর্ট করবেন
import com.example.SCM.repository.SupplierRepository; // আপনার প্রজেক্টের সাপ্লায়ার রিপোজিটরি ইম্পোর্ট করবেন
import com.example.SCM.service.PurchaseRequisitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseRequisitionServiceImp implements PurchaseRequisitionService {

    private final PurchaseRequisitionRepository requisitionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseRequisitionMapper requisitionMapper;


     //1. Save New Purchase Requisition

    @Override
    @Transactional
    public PurchaseRequisitionResponseDTO save(PurchaseRequisitionRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Requisition data cannot be null");
        }

        // ফ্রন্টএন্ড থেকে পাঠানো প্রোডাক্ট আইডি কালেকশন দিয়ে ডাটাবেজ থেকে আসল অবজেক্ট লিস্ট লোড করা
        List<Product> products = null;
        if (dto.getProductIds() != null && !dto.getProductIds().isEmpty()) {
            products = productRepository.findAllById(dto.getProductIds());
            if (products.size() != dto.getProductIds().size()) {
                throw new RuntimeException("One or more Product IDs are invalid!");
            }
        }

        // ফ্রন্টএন্ড থেকে পাঠানো সাপ্লায়ার আইডি কালেকশন দিয়ে ডাটাবেজ থেকে আসল অবজেক্ট লিস্ট লোড করা
        List<Supplier> suppliers = null;
        if (dto.getSupplierIds() != null && !dto.getSupplierIds().isEmpty()) {
            suppliers = supplierRepository.findAllById(dto.getSupplierIds());
            if (suppliers.size() != dto.getSupplierIds().size()) {
                throw new RuntimeException("One or more Supplier IDs are invalid!");
            }
        }

        // Mapper দিয়ে Entity তৈরি এবং রিলেশনাল অবজেক্ট ইনজেকশন
        PurchaseRequisition pr = requisitionMapper.toEntity(dto, products, suppliers);

        PurchaseRequisition savedPr = requisitionRepository.save(pr);
        return requisitionMapper.toResponseDTO(savedPr);
    }

    // 2. Update Existing Purchase Requisition

    @Override
    @Transactional
    public PurchaseRequisitionResponseDTO update(Long id, PurchaseRequisitionRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Requisition data cannot be null");
        }

        // এক্সিস্টিং রিকুইজিশন রেকর্ড খুঁজে বের করা
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Requisition not found with ID: " + id));

        // এপ্রুভড বা রিজেক্টেড হয়ে যাওয়া রিকুইজিশন এডিট করা লক করার বিজনেস লজিক (Optional but Professional)
        if (!"PENDING".equals(pr.getApprovalStatus().name())) {
            throw new RuntimeException("Only PENDING requisitions can be updated or modified!");
        }

        // নতুন প্রোডাক্ট লিস্ট চেঞ্জ করা হয়ে থাকলে তা আপডেট করা
        List<Product> updatedProducts = pr.getProducts();
        if (dto.getProductIds() != null) {
            updatedProducts = productRepository.findAllById(dto.getProductIds());
        }

        // নতুন সাপ্লায়ার লিস্ট চেঞ্জ করা হয়ে থাকলে তা আপডেট করা
        List<Supplier> updatedSuppliers = pr.getSuppliers();
        if (dto.getSupplierIds() != null) {
            updatedSuppliers = supplierRepository.findAllById(dto.getSupplierIds());
        }

        // ম্যাপার দিয়ে এক্সিস্টিং এনটিটির ফিল্ডগুলো এবং Many-to-Many অবজেক্ট ট্র্যাকিং রিফ্রেশ করা
        requisitionMapper.updateEntity(dto, pr, updatedProducts, updatedSuppliers);

        PurchaseRequisition updatedPr = requisitionRepository.save(pr);
        return requisitionMapper.toResponseDTO(pr);
    }

   // 3. Find All Purchase Requisitions

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseRequisitionResponseDTO> findAll() {
        return requisitionRepository.findAll().stream()
                .map(requisitionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 4. Find Purchase Requisition By ID

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseRequisitionResponseDTO> getById(Long id) {
        return requisitionRepository.findById(id)
                .map(requisitionMapper::toResponseDTO);
    }

    // 5. Delete Purchase Requisition

    @Override
    @Transactional
    public void delete(Long id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Requisition not found with ID: " + id));

        // অলরেডি এপ্রুভ হয়ে যাওয়া পিআর (PR) ডিলিট করা সিকিউরিটি পলিসি ব্রেক করে, তাই লজিক চেক:
        if (!"PENDING".equals(pr.getApprovalStatus().name())) {
            throw new RuntimeException("Approved or Processing Requisitions cannot be deleted from history!");
        }

        requisitionRepository.delete(pr);
    }
}