package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.GRNLineItemMapper;
import com.example.SCM.dto.mapper.GoodsReceivedNoteMapper;
import com.example.SCM.dto.request.GRNLineItemRequestDTO;
import com.example.SCM.dto.request.GoodsReceivedNoteRequestDTO;
import com.example.SCM.dto.response.GoodsReceivedNoteResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.repository.*;
import com.example.SCM.service.GoodsReceivedNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsReceivedNoteServiceImp implements GoodsReceivedNoteService {

    private final GoodsReceivedNoteRepository goodsReceivedNoteRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final GoodsReceivedNoteMapper goodsReceivedNoteMapper;
    private final GRNLineItemMapper gRNLineItemMapper;

    /**
     * 1. Save New Goods Received Note (Create Operation)
     * 💡 লজিক: PO থেকে অরিজিনাল quantity অটো-লোড হবে এবং একটি ইউনিক grnNumber জেনারেট হবে।
     */
    @Override
    @Transactional
    public GoodsReceivedNoteResponseDTO save(GoodsReceivedNoteRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("GRN data cannot be null");
        }

        // ১. রিলেশনাল অবজেক্টগুলো ডাটাবেজ থেকে কুয়েরি করা (থার্ড-পার্টি রিলেশন সেফটি)
        PurchaseOrder po = purchaseOrderRepository.findById(dto.getPoId())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + dto.getPoId()));

        Product product = null;
        if (dto.getProductId() != null) {
            product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + dto.getProductId()));
        }

        User receivedBy = userRepository.findById(dto.getReceivedBy())
                .orElseThrow(() -> new RuntimeException("Receiver User not found with ID: " + dto.getReceivedBy()));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with ID: " + dto.getWarehouseId()));

        User inspectedBy = null;
        if (dto.getInspectedBy() != null) {
            inspectedBy = userRepository.findById(dto.getInspectedBy())
                    .orElseThrow(() -> new RuntimeException("Inspector User not found with ID: " + dto.getInspectedBy()));
        }

        // ২. ম্যাপার দিয়ে প্রাথমিক এনটিটি তৈরি
        GoodsReceivedNote grn = goodsReceivedNoteMapper.toEntity(dto, po, product, receivedBy, warehouse, inspectedBy);

        // 💡 ৩. অটো-লোডিং লজিক: PurchaseOrder থেকে অরিজিনাল কোয়ান্টিটি সেট করা
        // (আপনার PurchaseOrder এনটিটির একচুয়াল কোয়ান্টিটি ফিল্ডের নাম অনুযায়ী get মেথডটি মিলিয়ে নেবেন)
        if (po.getQuantity() != null) {
            grn.setQuantity(po.getQuantity());
        }

        // GoodsReceivedNoteServiceImp.java এর save মেথডের ভেতর লজিক:



// 💡 ২. ম্যাজিক নাল-সেফ লজিক: ফ্রন্টএন্ড লাইন আইটেম পাঠিয়েছে কিনা তা চেক করা
        if (dto.getLineItems() != null && !dto.getLineItems().isEmpty()) {
            for (GRNLineItemRequestDTO itemDto : dto.getLineItems()) {

                // প্রতিটি চাইল্ড আইটেমের জন্য প্রোডাক্ট কুয়েরি (প্রয়োজন অনুযায়ী)
                Product itemProduct = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found for line item"));

                // ম্যাপার দিয়ে লাইন আইটেম এনটিটি তৈরি
                GRNLineItem lineItem = gRNLineItemMapper.toEntity(itemDto, grn, itemProduct);

                // প্যারেন্ট জিআরএন-এর লিস্টে চাইল্ড আইটেমটি যুক্ত করা
                grn.getLineItems().add(lineItem);
            }
        }

        // 💡 ৪. অটো-জেনারেশন লজিক: ইউনিক ট্র্যাকিং GRN নাম্বার তৈরি
        String generatedGrnNumber = "GRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        grn.setGrnNumber(generatedGrnNumber);

        GoodsReceivedNote savedGrn = goodsReceivedNoteRepository.save(grn);
        return goodsReceivedNoteMapper.toResponseDTO(savedGrn);
    }

    /**
     * 2. Update Existing Goods Received Note (Update Operation)
     */
    @Override
    @Transactional
    public GoodsReceivedNoteResponseDTO update(Long id, GoodsReceivedNoteRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        // এক্সিস্টিং জিআরএন রেকর্ড লোড করা
        GoodsReceivedNote grn = goodsReceivedNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goods Received Note not found with ID: " + id));

        // আপডেটেড রিলেশন অবজেক্ট কুয়েরি হ্যান্ডেলিং
        PurchaseOrder po = purchaseOrderRepository.findById(dto.getPoId())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + dto.getPoId()));

        Product product = dto.getProductId() != null ? productRepository.findById(dto.getProductId()).orElse(null) : null;
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId()).orElse(null);
        User inspectedBy = dto.getInspectedBy() != null ? userRepository.findById(dto.getInspectedBy()).orElse(null) : null;

        // ম্যাপার দিয়ে এক্সিস্টিং এনটিটি আপডেট
        goodsReceivedNoteMapper.updateEntity(dto, grn, po, product, warehouse, inspectedBy);

        // যদি পারচেজ অর্ডার চেঞ্জ হয়ে থাকে তবে কোয়ান্টিটি পুনরায় লোড করা
        if (po.getQuantity() != null) {
            grn.setQuantity(po.getQuantity());
        }

        GoodsReceivedNote updatedGrn = goodsReceivedNoteRepository.save(grn);
        return goodsReceivedNoteMapper.toResponseDTO(updatedGrn);
    }

    /**
     * 3. Find All Goods Received Notes (Read Operation)
     */
    @Override
    @Transactional(readOnly = true)
    public List<GoodsReceivedNoteResponseDTO> findAll() {
        return goodsReceivedNoteRepository.findAll().stream()
                .map(goodsReceivedNoteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 4. Get Single Goods Received Note By ID (Read Operation)
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<GoodsReceivedNoteResponseDTO> getById(Long id) {
        return goodsReceivedNoteRepository.findById(id)
                .map(goodsReceivedNoteMapper::toResponseDTO);
    }

    /**
     * 5. Hard Delete Goods Received Note (Delete Operation)
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!goodsReceivedNoteRepository.existsById(id)) {
            throw new RuntimeException("Goods Received Note not found with ID: " + id);
        }
        goodsReceivedNoteRepository.deleteById(id);
    }
}