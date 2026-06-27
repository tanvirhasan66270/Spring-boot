package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.StockMovementMapper;
import com.example.SCM.dto.request.StockMovementRequestDTO;
import com.example.SCM.dto.response.StockMovementResponseDTO;
import com.example.SCM.entity.StockMovement;
import com.example.SCM.repository.StockMovementRepository;
import com.example.SCM.repository.ProductRepository;
import com.example.SCM.repository.WarehouseRepository;
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
    private final StockMovementMapper mapper;

    @Override
    @Transactional
    public StockMovementResponseDTO logMovement(StockMovementRequestDTO dto) {
        //  ট্রানজেকশন পার্সিস্ট করা
        StockMovement entity = repository.save(mapper.toEntity(dto));

        //  রিলেশন টেবিল থেকে নাম দুটি অটো-ফেচ করা
        String productName = productRepository.findNameById(entity.getProductId()).orElse("Unknown Product");
        String warehouseName = warehouseRepository.findNameById(entity.getWarehouseId()).orElse("Unknown Warehouse");

        //  ডিটিওতে ম্যাপ করে পাঠানো
        return mapper.convertTOResponseDTO(entity, productName, warehouseName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> findAll() {
        return repository.findAll().stream().map(entity -> {
            String productName = productRepository.findNameById(entity.getProductId()).orElse("Unknown Product");
            String warehouseName = warehouseRepository.findNameById(entity.getWarehouseId()).orElse("Unknown Warehouse");
            return mapper.convertTOResponseDTO(entity, productName, warehouseName);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StockMovementResponseDTO> getById(Long id) {
        return repository.findById(id).map(entity -> {
            String productName = productRepository.findNameById(entity.getProductId()).orElse("Unknown Product");
            String warehouseName = warehouseRepository.findNameById(entity.getWarehouseId()).orElse("Unknown Warehouse");
            return mapper.convertTOResponseDTO(entity, productName, warehouseName);
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Stock Ledger record pointer missing at datastore context.");
        }
        repository.deleteById(id);
    }
}