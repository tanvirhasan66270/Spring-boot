package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.PurchaseOrderMapper;
import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import com.example.SCM.entity.POLineItem;
import com.example.SCM.entity.PurchaseOrder;
import com.example.SCM.entity.PurchaseRequisition;
import com.example.SCM.entity.Supplier;
import com.example.SCM.repository.PurchaseOrderRepository;
import com.example.SCM.repository.PurchaseRequisitionRepository;
import com.example.SCM.repository.SupplierRepository;
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
    private final SupplierRepository supplierRepository;
    private final PurchaseRequisitionRepository requisitionRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    /**
     * 1. Save New Purchase Order
     */
    @Override
    @Transactional
    public PurchaseOrderResponseDTO save(PurchaseOrderRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Purchase Order data cannot be null");
        }

        // বিজনেস লজিক: ডুপ্লিকেট পিও এড়াতে চেক
        Optional<PurchaseOrder> existingPo = purchaseOrderRepository.findByPurchaseRequisitionId(dto.getPurchaseRequisitionId());
        if (existingPo.isPresent()) {
            throw new RuntimeException("A Purchase Order already exists for this Purchase Requisition ID: " + dto.getPurchaseRequisitionId());
        }

        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + dto.getSupplierId()));

        PurchaseRequisition requisition = requisitionRepository.findById(dto.getPurchaseRequisitionId())
                .orElseThrow(() -> new RuntimeException("Purchase Requisition not found with ID: " + dto.getPurchaseRequisitionId()));

        // Mapper দিয়ে DTO -> Entity রূপান্তর
        PurchaseOrder po = purchaseOrderMapper.toEntity(dto, supplier, requisition);

        // দ্রষ্টব্য: প্রথমবার সেভ হওয়ার সময় চাইল্ড লাইন আইটেম না থাকলে টোটাল $0 থাকবে।
        // লাইন আইটেম অ্যাড হওয়ার সাথে সাথে রোল-আপ মেথড গ্র্যান্ড টোটাল আপডেট করবে।
        PurchaseOrder savedPo = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toResponseDTO(savedPo);
    }

    /**
     * 2. Update Existing Purchase Order
     */
    @Override
    @Transactional
    public PurchaseOrderResponseDTO update(Long id, PurchaseOrderRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Purchase Order data cannot be null");
        }

        // 💡 অপ্টিমাইজেশন: চাইল্ড লাইন আইটেমসহ রেকর্ড খুঁজে বের করা যাতে ডাটা রিফ্রেশ না হয়
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + id));

        // বিজনেস লজিক: RECEIVED বা CANCELLED হলে ডেটা লক থাকবে
        if ("RECEIVED".equals(po.getStatus().name()) || "CANCELLED".equals(po.getStatus().name())) {
            throw new RuntimeException("Closed or Cancelled Purchase Orders cannot be modified!");
        }

        Supplier supplier = po.getSupplier();
        if (dto.getSupplierId() != null && !dto.getSupplierId().equals(supplier.getId())) {
            supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("New Supplier not found with ID: " + dto.getSupplierId()));
        }

        PurchaseRequisition requisition = po.getPurchaseRequisition();
        if (dto.getPurchaseRequisitionId() != null && !dto.getPurchaseRequisitionId().equals(requisition.getId())) {
            requisition = requisitionRepository.findById(dto.getPurchaseRequisitionId())
                    .orElseThrow(() -> new RuntimeException("New Purchase Requisition not found with ID: " + dto.getPurchaseRequisitionId()));
        }

        // ম্যাপারের মাধ্যমে বেসিক ডেটা, ডেট ও এনাম স্ট্যাটাস আপডেট করা
        purchaseOrderMapper.updateEntity(dto, po, supplier, requisition);

        // 💡 রোল-আপ লজিক ট্রিগার: স্ট্যাটাস পরিবর্তনের কারণে গ্র্যান্ড টোটাল রি-ক্যালকুলেট করা
//        triggerGrandTotalRollUp(po);

        PurchaseOrder updatedPo = purchaseOrderRepository.save(po);
        return purchaseOrderMapper.toResponseDTO(updatedPo);
    }

    /**
     * 3. Find All Purchase Orders
     */
    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponseDTO> findAll() {
        return purchaseOrderRepository.findAll().stream()
                .map(purchaseOrderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 4. Find Purchase Order By ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseOrderResponseDTO> getById(Long id) {
        // 💡 অপ্টিমাইজেশন: ড্যাশবোর্ডে চাইল্ড আইটেমের গ্রিড ডেটাসহ একবারে রেন্ডার করার জন্য কাস্টম মেথড ব্যবহার করা হয়েছে
        return purchaseOrderRepository.findById(id)
                .map(purchaseOrderMapper::toResponseDTO);
    }

    /**
     * 5. Delete Purchase Order
     */
    @Override
    @Transactional
    public void delete(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + id));

        // লিগ্যাল অডিট ট্রেইলের জন্য শুধুমাত্র DRAFT অবস্থায় থাকা অর্ডারই ডিলিট করা যাবে।
        if (!"DRAFT".equals(po.getStatus().name())) {
            throw new RuntimeException("Only DRAFT Purchase Orders can be deleted. Released orders must be CANCELLED instead of deleted!");
        }

        purchaseOrderRepository.delete(po);
    }


}