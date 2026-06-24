package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.InventoryMapper;
import com.example.SCM.dto.request.InventoryRequestDTO;
import com.example.SCM.dto.response.InventoryResponseDTO;
import com.example.SCM.entity.Inventory;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.Warehouse;
import com.example.SCM.repository.InventoryRepository;
import com.example.SCM.repository.ProductRepository;
import com.example.SCM.repository.WarehouseRepository;
import com.example.SCM.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImp implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryMapper inventoryMapper;

    /**
     * 1. Save New Inventory Stock
     */
    @Override
    @Transactional
    public InventoryResponseDTO save(InventoryRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Inventory request data cannot be null");
        }

        // ডাটাবেজে একই ওয়ারহাউজে একই প্রোডাক্টের কম্বিনেশন অলরেডি এক্সিস্ট করে কি না চেক (ডুপ্লিকেট স্টক এড়াতে)
        Optional<Inventory> existingStock = inventoryRepository.findByProductIdAndWarehouseId(dto.getProductId(), dto.getWarehouseId());
        if (existingStock.isPresent()) {
            throw new RuntimeException("Inventory record already exists for this Product in the specified Warehouse! Please use update instead.");
        }

        // রিলেশনাল প্রোডাক্ট এবং ওয়ারহাউজ অবজেক্ট খুঁজে বের করা
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + dto.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with ID: " + dto.getWarehouseId()));

        // Mapper দিয়ে DTO -> Entity রূপান্তর
        Inventory inventory = inventoryMapper.toEntity(dto, product, warehouse);

        // বিজনেস লজিক: ইনভেন্টরি লেভেলে স্টক স্ট্যাটাস ডাইনামিকালি সেট করা
        calculateAndSetStockStatus(inventory, product);

        Inventory savedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toResponseDTO(savedInventory);
    }

    // 2. Update Existing Inventory Stock

    @Override
    @Transactional
    public InventoryResponseDTO update(Long id, InventoryRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Inventory request data cannot be null");
        }

        // এক্সিস্টিং ইনভেন্টরি রো খুঁজে বের করা
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory record not found with ID: " + id));

        // যদি প্রোডাক্ট বা ওয়ারহাউজ চেঞ্জ করার রিকোয়ারমেন্ট আসে
        Product product = inventory.getProduct();
        if (dto.getProductId() != null && !dto.getProductId().equals(product.getId())) {
            product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("New Product not found with ID: " + dto.getProductId()));
        }

        Warehouse warehouse = inventory.getWarehouse();
        if (dto.getWarehouseId() != null && !dto.getWarehouseId().equals(warehouse.getId())) {
            warehouse = warehouseRepository.findById(dto.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("New Warehouse not found with ID: " + dto.getWarehouseId()));
        }

        // ম্যাপারের মাধ্যমে বেসিক ডেটা ও ডেট আপডেট
        inventoryMapper.updateEntity(dto, inventory, product, warehouse);

        // বিজনেস লজিক: আপডেটেড পরিমাণের ওপর ভিত্তি করে স্টক স্ট্যাটাস আবার চেক করা
        calculateAndSetStockStatus(inventory, product);

        Inventory updatedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toResponseDTO(updatedInventory);
    }

    // 3. Find All Inventories

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> findAll() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 4. Find Inventory By ID

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryResponseDTO> getById(Long id) {
        return inventoryRepository.findById(id)
                .map(inventoryMapper::toResponseDTO);
    }

    // 5. Delete Inventory Record

    @Override
    @Transactional
    public void delete(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory record not found with ID: " + id));

        inventoryRepository.delete(inventory);
    }

    /**
     * 💡 রিয়াল-টাইম ইনভেন্টরি বিজনেস লজিক হেল্পার মেথড
     * quantityOnHand এবং quantityReserved এর ওপর ভিত্তি করে অটো-স্ট্যাটাস ক্যালকুলেশন
     */
    private void calculateAndSetStockStatus(Inventory inventory, Product product) {
        int availableQuantity = inventory.getQuantityOnHand() - inventory.getQuantityReserved();

        if (availableQuantity <= 0) {
            inventory.setStockStatus("OUT_OF_STOCK");
        } else if (availableQuantity <= product.getReorderPoint()) {
            inventory.setStockStatus("LOW_STOCK"); // প্রোডাক্টের নিজস্ব রিঅর্ডার পয়েন্ট ক্রস করলে ওয়ার্নিং
        } else {
            inventory.setStockStatus("IN_STOCK");
        }
    }
}