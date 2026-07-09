package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.InventoryMapper;
import com.example.SCM.dto.request.InventoryRequestDTO;
import com.example.SCM.dto.response.InventoryResponseDTO;
import com.example.SCM.entity.Inventory;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.Warehouse;
import com.example.SCM.enumClass.StockStatus; // 🎯 এনাম প্যাকেজ ইমপোর্ট
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

    @Override
    @Transactional
    public InventoryResponseDTO save(InventoryRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Inventory request data cannot be null");
        }

        Optional<Inventory> existingStock = inventoryRepository.findByProductIdAndWarehouseId(dto.getProductId(), dto.getWarehouseId());
        if (existingStock.isPresent()) {
            throw new RuntimeException("Inventory record already exists for this Product in the specified Warehouse! Please use update instead.");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + dto.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with ID: " + dto.getWarehouseId()));

        Inventory inventory = inventoryMapper.toEntity(dto, product, warehouse);

        // 🚀 বিজনেস লজিক: এনাম ট্র্যাকিং ক্যালকুলেটর
        calculateAndSetStockStatus(inventory, product);

        // 🎯 saveAndFlush ব্যবহার করা হয়েছে যাতে @PrePersist (lastUpdated টাইম) ইনস্ট্যান্ট সিঙ্ক হয়
        Inventory savedInventory = inventoryRepository.saveAndFlush(inventory);
        return inventoryMapper.convertTOResponseDTO(savedInventory);
    }

    @Override
    @Transactional
    public InventoryResponseDTO update(Long id, InventoryRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Inventory request data cannot be null");
        }

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory record not found with ID: " + id));

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

        inventoryMapper.updateEntity(dto, inventory, product, warehouse);

        // 🚀 বিজনেস লজিক: পরিমাণের ওপর ভিত্তি করে স্টক এনাম আপডেট
        calculateAndSetStockStatus(inventory, product);

        Inventory updatedInventory = inventoryRepository.saveAndFlush(inventory);
        return inventoryMapper.convertTOResponseDTO(updatedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> findAll() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryResponseDTO> getById(Long id) {
        return inventoryRepository.findById(id)
                .map(inventoryMapper::convertTOResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory record not found with ID: " + id));

        inventoryRepository.delete(inventory);
    }

    private void calculateAndSetStockStatus(Inventory inventory, Product product) {
        int availableQuantity = inventory.getQuantityOnHand() - inventory.getQuantityReserved();

        if (availableQuantity <= 0) {
            inventory.setStockStatus(StockStatus.OUT_OF_STOCK);
        } else if (product != null && availableQuantity <= product.getReorderPoint()) {
            inventory.setStockStatus(StockStatus.LOW_STOCK);
        } else {
            inventory.setStockStatus(StockStatus.IN_STOCK);
        }
    }
}