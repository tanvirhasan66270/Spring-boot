package com.example.SCM.serviceImp;

import com.example.SCM.Util.TrakingCode.TrackingCodeGenerator;
import com.example.SCM.dto.mapper.POLineItemMapper;
import com.example.SCM.dto.request.POLineItemRequestDTO;
import com.example.SCM.dto.response.POLineItemResponseDTO;
import com.example.SCM.entity.Inventory;
import com.example.SCM.entity.POLineItem;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.PurchaseOrder;
import com.example.SCM.enumClass.POLineItemStatus;
import com.example.SCM.repository.InventoryRepository;
import com.example.SCM.repository.POLineItemRepository;
import com.example.SCM.repository.ProductRepository;
import com.example.SCM.repository.PurchaseOrderRepository;
import com.example.SCM.service.POLineItemService;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class POLineItemServiceImp implements POLineItemService {

    private final POLineItemRepository poLineItemRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final POLineItemMapper poLineItemMapper;
    private final TrackingCodeGenerator trackingCodeGenerator; //
    @Lazy// 💡 আপনার কাস্টম ইউটিল ইনজেকশন
    private final PurchaseOrderServiceImp purchaseOrderServiceImp; // রোল-আপ ট্রিগারের জন্য

    /**
     * 1. Save New PO Line Item
     */
    @Override
    @Transactional
    public POLineItemResponseDTO save(POLineItemRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Line item data cannot be null");
        }

        PurchaseOrder order = purchaseOrderRepository.findById(dto.getPoId())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + dto.getPoId()));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + dto.getProductId()));

        // ইনভেন্টরি চেক পাস (Usable Stock = QuantityOnHand - QuantityReserved)
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .stream()
                .findFirst() // লিস্টের প্রথম ইনভেন্টরি রেকর্ডটি নেবে
                .orElseThrow(() -> new RuntimeException("Inventory record not found for Product ID: " + product.getId()));
        int availableStock = inventory.getQuantityOnHand() - inventory.getQuantityReserved();

        if (availableStock < dto.getQuantity()) {
            throw new RuntimeException("InsufficientStockException: Requested quantity (" + dto.getQuantity()
                    + ") exceeds available usable stock (" + availableStock + ")!");
        }

        // ইনভেন্টরিতে স্টক রিজার্ভেশন লগ (QuantityReserved বাড়িয়ে দেওয়া)
        inventory.setQuantityReserved(inventory.getQuantityReserved() + dto.getQuantity());
        inventoryRepository.save(inventory);

        // Mapping DTO -> Entity
        POLineItem item = poLineItemMapper.toEntity(dto, order, product);

        // যদি প্রথমবারই SHIPPED স্ট্যাটাসে আসে তবে কাস্টম ট্র্যাকিং কোড সেটআপ
        if (POLineItemStatus.SHIPPED.name().equalsIgnoreCase(dto.getStatus())) {
            item.setTrackingNumber(trackingCodeGenerator.generateTrackingCode());
        }

        POLineItem savedItem = poLineItemRepository.save(item);

        // প্যারেন্ট অর্ডারের গ্র্যান্ড টোটাল রোল-আপ আপডেট ট্রিগার
        order.getLineItems().add(savedItem);
        purchaseOrderServiceImp.triggerGrandTotalRollUp(order);

        return poLineItemMapper.toResponseDTO(savedItem);
    }

    /**
     * 2. Update Existing PO Line Item (State Machine Engine)
     */
    @Override
    @Transactional
    public POLineItemResponseDTO update(Long id, POLineItemRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Line item data cannot be null");
        }

        POLineItem item = poLineItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PO Line Item not found with ID: " + id));

        Product product = item.getProduct();
        PurchaseOrder order = item.getPurchaseOrder();
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .stream()
                .findFirst() // লিস্টের প্রথম ইনভেন্টরি রেকর্ডটি নেবে
                .orElseThrow(() -> new RuntimeException("Inventory record not found for Product ID: " + product.getId()));

        POLineItemStatus oldStatus = item.getStatus();
        int oldQuantity = item.getQuantity();

        // কোয়ান্টিটি পরিবর্তনের সাপেক্ষে ইনভেন্টরি ব্যালেন্স করা
        if (dto.getQuantity() != oldQuantity) {
            int difference = dto.getQuantity() - oldQuantity;
            if (difference > 0) {
                int availableStock = inventory.getQuantityOnHand() - inventory.getQuantityReserved();
                if (availableStock < difference) {
                    throw new RuntimeException("InsufficientStockException: Low warehouse stock to support change!");
                }
                inventory.setQuantityReserved(inventory.getQuantityReserved() + difference);
            } else {
                inventory.setQuantityReserved(inventory.getQuantityReserved() - Math.abs(difference));
            }
        }

        // ম্যাপার দিয়ে ফিল্ড আপডেট
        poLineItemMapper.updateEntity(dto, item, product);
        POLineItemStatus newStatus = item.getStatus();

        // 💡 স্টেট মেশিন লজিক ১ (PENDING -> SHIPPED): ট্র্যাকিং কোড বসানো এবং স্টক ফাইনালি মাইনাস করা
        if (oldStatus != POLineItemStatus.SHIPPED && newStatus == POLineItemStatus.SHIPPED) {
            if (item.getTrackingNumber() == null) {
                item.setTrackingNumber(trackingCodeGenerator.generateTrackingCode());
            }
            // শিপমেন্ট হয়ে যাওয়ার কারণে ওয়ান-হ্যান্ড এবং রিজার্ভড দুই জায়গা থেকেই স্টক পার্মানেন্টলি কেটে নেওয়া হবে
            inventory.setQuantityOnHand(inventory.getQuantityOnHand() - item.getQuantity());
            inventory.setQuantityReserved(inventory.getQuantityReserved() - item.getQuantity());
        }




        // 💡 স্টেট মেশিন লজিক ২ (স্ট্যাটাস যখন CANCELLED): স্টক রিলিজ ও লাইন টোটাল $0 করা
        if (oldStatus != POLineItemStatus.CANCELLED && newStatus == POLineItemStatus.CANCELLED) {
            // যদি শিপড হওয়ার আগে ক্যানসেল হয়, তবে আটকে থাকা রিজার্ভড স্টক রিলিজ করা হবে
            if (oldStatus != POLineItemStatus.SHIPPED && oldStatus != POLineItemStatus.DELIVERED) {
                inventory.setQuantityReserved(inventory.getQuantityReserved() - item.getQuantity());
            }
            item.setLineTotal(0.0); // ক্যানসেলড হওয়ার কারণে লাইন টোটাল জিরো
        }

        inventoryRepository.save(inventory);
        POLineItem updatedItem = poLineItemRepository.save(item);

        // প্যারেন্ট টেবিলের রোল-আপ গ্র্যান্ড টোটাল রি-ট্রিগার
        purchaseOrderServiceImp.triggerGrandTotalRollUp(order);

        return poLineItemMapper.toResponseDTO(updatedItem);
    }

    /**
     * 3. Find All Line Items
     */
    @Override
    @Transactional(readOnly = true)
    public List<POLineItemResponseDTO> findAll() {
        return poLineItemRepository.findAll().stream()
                .map(poLineItemMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 4. Find Line Item By ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<POLineItemResponseDTO> getById(Long id) {
        return poLineItemRepository.findById(id)
                .map(poLineItemMapper::toResponseDTO);
    }

    /**
     * 5. Delete Line Item
     */
    @Override
    @Transactional
    public void delete(Long id) {
        POLineItem item = poLineItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PO Line Item not found with ID: " + id));

        PurchaseOrder order = item.getPurchaseOrder();

        // ডিলিট করার আগে স্টক রিজার্ভেশন অবমুক্ত করা (যদি আইটেমটি একটিভ থাকে)
        if (item.getStatus() != POLineItemStatus.CANCELLED && item.getStatus() != POLineItemStatus.SHIPPED) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId())
                    .stream()
                    .findFirst() // লিস্টের প্রথম রেকর্ডটি অপশনাল হিসেবে নেবে
                    .orElse(null); // রেকর্ড না পাওয়া গেলে নাল সেট করবে
            if (inventory != null) {
                inventory.setQuantityReserved(Math.max(0, inventory.getQuantityReserved() - item.getQuantity()));
                inventoryRepository.save(inventory);
            }
        }

        poLineItemRepository.delete(item);

        // রোল-আপ গ্র্যান্ড টোটাল রি-ক্যালকুলেশন
        order.getLineItems().remove(item);
        purchaseOrderServiceImp.triggerGrandTotalRollUp(order);
    }

    @Override
    public POLineItemResponseDTO tracking(String trackingNumber) {


        return null;
    }
}