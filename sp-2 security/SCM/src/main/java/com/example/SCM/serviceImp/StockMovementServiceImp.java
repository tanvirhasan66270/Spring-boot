package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.StockMovementMapper;
import com.example.SCM.dto.request.StockMovementRequestDTO;
import com.example.SCM.dto.response.StockMovementResponseDTO;
import com.example.SCM.entity.Product;
import com.example.SCM.entity.StockMovement;
import com.example.SCM.entity.User;
import com.example.SCM.entity.Warehouse;
import com.example.SCM.repository.StockMovementRepository;
import com.example.SCM.repository.ProductRepository;
import com.example.SCM.repository.WarehouseRepository;
import com.example.SCM.repository.UserRepository; // 🎯 ইউজার এনটিটি লোড করার জন্য ইমপোর্ট করা হয়েছে
import com.example.SCM.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImp implements StockMovementService {

    private final StockMovementRepository repository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final StockMovementMapper mapper;

    @Override
    @Transactional
    public StockMovementResponseDTO logMovement(StockMovementRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Stock movement request data cannot be null");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + dto.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Target Warehouse not found with ID: " + dto.getWarehouseId()));

        Warehouse sourceWarehouse = null;
        if (dto.getSourceWarehouseId() != null && dto.getSourceWarehouseId() > 0) {
            sourceWarehouse = warehouseRepository.findById(dto.getSourceWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Source Warehouse not found with ID: " + dto.getSourceWarehouseId()));
        }

        User performer = userRepository.findById(dto.getPerformedBy())
                .orElseThrow(() -> new RuntimeException("User personnel not found with ID: " + dto.getPerformedBy()));

        StockMovement entity = mapper.toEntity(dto, product, warehouse, sourceWarehouse, performer);

        StockMovement savedEntity = repository.saveAndFlush(entity);

        // সরাসরি অবজেক্ট গ্রাফ থেকে রিলেশনাল নামসহ ফ্ল্যাটেনড ডিটিও রিটার্ন
        return mapper.convertTOResponseDTO(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> findAll() {
        // নতুন ম্যাপার অবজেক্ট ট্রাভার্সাল করতে পারায় স্ট্রিমের ভেতর আলাদা ম্যানুয়াল কুয়েরি করার দরকার নেই
        return repository.findAll().stream()
                .map(mapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StockMovementResponseDTO> getById(Long id) {
        return repository.findById(id)
                .map(mapper::convertTOResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Stock Ledger record pointer missing at datastore context. ID: " + id);
        }
        repository.deleteById(id);
    }
}