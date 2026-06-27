package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.InventoryRequestDTO;
import com.example.SCM.dto.response.InventoryResponseDTO;
import com.example.SCM.entity.Inventory;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class InventoryMapper {

    // "YYYY-MM-DD"
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // InventoryRequestDTO, Product, এবং Warehouse অবজেক্ট থেকে নতুন Inventory Entity-তে রূপান্তর (Create Operation)

    public Inventory toEntity(InventoryRequestDTO dto, Product product, Warehouse warehouse) {


        Inventory inventory = new Inventory();
        inventory.setQuantityOnHand(dto.getQuantityOnHand());
        inventory.setQuantityReserved(dto.getQuantityReserved());
        inventory.setLocationStatus(dto.getLocationStatus());
        inventory.setStockStatus(dto.getStockStatus());

        // String থেকে LocalDate-এ কনভার্ট করা (নাল সেফটিসহ)
        if (dto.getExpiryDate() != null && !dto.getExpiryDate().trim().isEmpty()) {
            inventory.setExpiryDate(LocalDate.parse(dto.getExpiryDate(), dateFormatter));
        }

        // রিলেশনাল ফরেন অবজেক্টগুলো ইনজেক্ট করা
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);

        return inventory;
    }

    /**
     * Inventory Entity থেকে InventoryResponseDTO-তে রূপান্তর (Read/All Operations)
     * এই মেথডটি রিলেশনাল অবজেক্ট গ্রাফ ভেঙে ডাটা ফ্ল্যাটেনিং সম্পন্ন করে।
     */
    public InventoryResponseDTO convertTOResponseDTO(Inventory inventory) {


        InventoryResponseDTO dto = new InventoryResponseDTO();
        dto.setId(inventory.getId());
        dto.setQuantityOnHand(inventory.getQuantityOnHand());
        dto.setQuantityReserved(inventory.getQuantityReserved());
        dto.setLocationStatus(inventory.getLocationStatus());
        dto.setExpiryDate(inventory.getExpiryDate());
        dto.setStockStatus(inventory.getStockStatus());
        dto.setLastUpdated(inventory.getLastUpdated());

        // ক্যালকুলেটেড ফিল্ড: বিক্রয়যোগ্য প্রকৃত স্টক (OnHand - Reserved) অটো সেট করা
        int available = inventory.getQuantityOnHand() - inventory.getQuantityReserved();
        dto.setAvailableQuantity(Math.max(available, 0)); // মাইনাস ভ্যালু এড়াতে

        // Product Details Flattening
        if (inventory.getProduct() != null) {
            Product prod = inventory.getProduct();
            dto.setProductId(prod.getId());
            dto.setProductCode(prod.getProductCode());
            dto.setProductName(prod.getName()); // ফ্রন্টএন্ড টেবিল সরাসরি নাম পেয়ে যাবে
        }

        // Warehouse Details Flattening
        if (inventory.getWarehouse() != null) {
            Warehouse wh = inventory.getWarehouse();
            dto.setWarehouseId(wh.getId());
            dto.setWarehouseName(wh.getName()); // ফ্রন্টএন্ডে গুদামের নাম সরাসরি দেখানোর জন্য
        }

        return dto;
    }

    // এক্সিস্টিং Inventory Entity-কে Request DTO, নতুন Product এবং Warehouse দিয়ে আপডেট করা (Update Operation)

    public void updateEntity(InventoryRequestDTO dto, Inventory inventory, Product product, Warehouse warehouse) {


        inventory.setQuantityOnHand(dto.getQuantityOnHand());
        inventory.setQuantityReserved(dto.getQuantityReserved());

        if (dto.getLocationStatus() != null) inventory.setLocationStatus(dto.getLocationStatus());
        if (dto.getStockStatus() != null) inventory.setStockStatus(dto.getStockStatus());

        // আপডেট অপারেশনের সময় স্ট্রিং ডেট পার্সিং
        if (dto.getExpiryDate() != null && !dto.getExpiryDate().trim().isEmpty()) {
            inventory.setExpiryDate(LocalDate.parse(dto.getExpiryDate(), dateFormatter));
        }

        // যদি প্রোডাক্ট বা ওয়ারহাউজ পরিবর্তনের রিকোয়ারমেন্ট থাকে
        if (product != null) {
            inventory.setProduct(product);
        }
        if (warehouse != null) {
            inventory.setWarehouse(warehouse);
        }
    }
}