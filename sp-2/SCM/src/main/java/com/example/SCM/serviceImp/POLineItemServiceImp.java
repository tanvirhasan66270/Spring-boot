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
    private final TrackingCodeGenerator trackingCodeGenerator;


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

        // ইনভেন্টরি এভেইলেবল স্টক চেক (Optional টাইপ সেফটি সিঙ্ক মেনে)
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Inventory record not found for Product ID: " + product.getId()));

        int availableStock = inventory.getQuantityOnHand() - inventory.getQuantityReserved();
        if (availableStock < dto.getQuantity()) {
            throw new RuntimeException("InsufficientStockException: Low warehouse inventory!");
        }

        // ভার্চুয়ালি স্টক লক করতে Reserved Quantity বাড়ানো
        inventory.setQuantityReserved(inventory.getQuantityReserved() + dto.getQuantity());
        inventoryRepository.save(inventory);

        // DTO -> Entity কনভার্সন
        POLineItem item = poLineItemMapper.toEntity(dto, order, product);

        if (POLineItemStatus.SHIPPED.name().equalsIgnoreCase(dto.getStatus())) {
            item.setTrackingNumber(trackingCodeGenerator.generateTrackingCode());
        }

        POLineItem savedItem = poLineItemRepository.save(item);

        //রোল-আপ লজিক: নতুন আইটেম সেভ হওয়ার পর প্যারেন্ট PurchaseOrder-এর totalAmount ডাটাবেজে আপডেট করা
        double updatedTotal = poLineItemRepository.getActiveTotalAmountByPoId(order.getId());
        order.setTotalAmount(updatedTotal);
        purchaseOrderRepository.save(order);

        return poLineItemMapper.toResponseDTO(savedItem);
    }


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
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Inventory record not found for product."));

        POLineItemStatus oldStatus = item.getStatus();
        int oldQuantity = item.getQuantity();

        // কোয়ান্তিটি পরিবর্তনের উপর ভিত্তি করে ইনভেন্টরি রিজার্ভেশন অ্যাডজাস্টমেন্ট
        if (dto.getQuantity() != oldQuantity) {
            int difference = dto.getQuantity() - oldQuantity;
            if (difference > 0) {
                int availableStock = inventory.getQuantityOnHand() - inventory.getQuantityReserved();
                if (availableStock < difference) {
                    throw new RuntimeException("InsufficientStockException: Low warehouse stock to support update!");
                }
                inventory.setQuantityReserved(inventory.getQuantityReserved() + difference);
            } else {
                inventory.setQuantityReserved(inventory.getQuantityReserved() - Math.abs(difference));
            }
        }

        // ম্যাপার দিয়ে ফিল্ড আপডেট
        poLineItemMapper.updateEntity(dto, item, product);
        POLineItemStatus newStatus = item.getStatus();

        // স্টেট মেশিন লজিক ১ (PENDING -> SHIPPED): ট্র্যাকিং কোড এবং ইনভেন্টরি ফাইনাল ডিডাকশন
        if (oldStatus != POLineItemStatus.SHIPPED && newStatus == POLineItemStatus.SHIPPED) {
            if (item.getTrackingNumber() == null) {
                item.setTrackingNumber(trackingCodeGenerator.generateTrackingCode());
            }
            inventory.setQuantityOnHand(inventory.getQuantityOnHand() - item.getQuantity());
            inventory.setQuantityReserved(inventory.getQuantityReserved() - item.getQuantity());
        }

        //স্টেট মেশিন লজিক ২ (CANCELLED): রিজার্ভড স্টক রিলিজ
        if (oldStatus != POLineItemStatus.CANCELLED && newStatus == POLineItemStatus.CANCELLED) {
            if (oldStatus != POLineItemStatus.SHIPPED && oldStatus != POLineItemStatus.DELIVERED) {
                inventory.setQuantityReserved(inventory.getQuantityReserved() - item.getQuantity());
            }
            item.setLineTotal(0.0);
        }

        inventoryRepository.save(inventory);
        POLineItem updatedItem = poLineItemRepository.save(item);

        // 💡 রোল-আপ লজিক: চাইল্ড আপডেট হওয়ার পর প্যারেন্ট মাস্টার টেবিলের totalAmount সিঙ্ক করা
        double updatedTotal = poLineItemRepository.getActiveTotalAmountByPoId(order.getId());
        order.setTotalAmount(updatedTotal);
        purchaseOrderRepository.save(order);

        return poLineItemMapper.toResponseDTO(updatedItem);
    }


    @Override
    @Transactional(readOnly = true)
    public List<POLineItemResponseDTO> findAll() {
        return poLineItemRepository.findAll().stream()
                .map(poLineItemMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<POLineItemResponseDTO> getById(Long id) {
        return poLineItemRepository.findById(id)
                .map(poLineItemMapper::toResponseDTO);
    }


    @Override
    @Transactional
    public void delete(Long id) {
        POLineItem item = poLineItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PO Line Item not found with ID: " + id));

        PurchaseOrder order = item.getPurchaseOrder();

        // ডিলিট করার আগে লক থাকা রিজার্ভড স্টক রিলিজ করা
        if (item.getStatus() != POLineItemStatus.CANCELLED && item.getStatus() != POLineItemStatus.SHIPPED) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId())
                    .stream()
                    .findFirst()
                    .orElse(null);
            if (inventory != null) {
                inventory.setQuantityReserved(Math.max(0, inventory.getQuantityReserved() - item.getQuantity()));
                inventoryRepository.save(inventory);
            }
        }

        poLineItemRepository.delete(item);

        // রোল-আপ লজিক: আইটেম চিরতরে মুছে যাওয়ার পর প্যারেন্ট অর্ডারের totalAmount পুনরায় হিসাব করে কমানো
        double updatedTotal = poLineItemRepository.getActiveTotalAmountByPoId(order.getId());
        order.setTotalAmount(updatedTotal);
        purchaseOrderRepository.save(order);
    }

    /**
    * লজিস্টিকস ড্যাশবোর্ডে ট্র্যাকিং নাম্বার দিয়ে আইটেমের স্টেট জানার জন্য
     */
    @Override
    @Transactional(readOnly = true)
    public POLineItemResponseDTO tracking(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Tracking number cannot be null or empty");
        }

        POLineItem item = poLineItemRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("No purchase order item found with Tracking Number: " + trackingNumber));

        return poLineItemMapper.toResponseDTO(item);
    }
}