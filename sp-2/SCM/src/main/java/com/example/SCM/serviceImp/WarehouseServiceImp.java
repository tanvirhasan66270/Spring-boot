package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.WarehouseMapper;
import com.example.SCM.dto.request.WarehouseRequestDTO;
import com.example.SCM.dto.response.WarehouseResponseDTO;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.Warehouse;
import com.example.SCM.repository.PoliceStationRepository; // আপনার প্রজেক্টের থানা রিপোজিটরি ইম্পোর্ট করবেন
import com.example.SCM.repository.WarehouseRepository;
import com.example.SCM.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImp implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final PoliceStationRepository policeStationRepository;
    private final WarehouseMapper warehouseMapper;

   // 1. Save New Warehouse

    @Override
    @Transactional
    public WarehouseResponseDTO save(WarehouseRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Warehouse request data cannot be null");
        }

        // ওয়ারহাউজের নাম ডুপ্লিকেট কি না চেক (ইউনিক কন্ট্রেয়েন্ট ভ্যালিডেশন)
        Optional<Warehouse> existingWarehouse = warehouseRepository.findByName(dto.getName());
        if (existingWarehouse.isPresent()) {
            throw new RuntimeException("Warehouse name '" + dto.getName() + "' already exists!");
        }

        // ডাটাবেজ থেকে রিলেশনাল PoliceStation (থানা) খুঁজে বের করা
        PoliceStation policeStation = null;
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not found with ID: " + dto.getPoliceStationId()));
        }

        // Mapper দিয়ে DTO -> Entity রূপান্তর এবং সেভ
        Warehouse warehouse = warehouseMapper.toEntity(dto, policeStation);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        return warehouseMapper.toResponseDTO(savedWarehouse);
    }

    // 2. Update Existing Warehouse

    @Override
    @Transactional
    public WarehouseResponseDTO update(Long id, WarehouseRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Warehouse request data cannot be null");
        }

        // এক্সিস্টিং ওয়ারহাউজ খুঁজে বের করা
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with ID: " + id));

        // নাম পরিবর্তন করা হয়ে থাকলে নতুন নাম অন্য কোনো ওয়ারহাউজের সাথে ডুপ্লিকেট হচ্ছে কি না চেক
        if (dto.getName() != null && !dto.getName().equals(warehouse.getName())) {
            Optional<Warehouse> duplicateCheck = warehouseRepository.findByName(dto.getName());
            if (duplicateCheck.isPresent()) {
                throw new RuntimeException("Warehouse name '" + dto.getName() + "' is already taken!");
            }
        }

        // পুলিশ স্টেশন চেঞ্জ করা হয়ে থাকলে নতুন থানা অবজেক্ট লোড করা
        PoliceStation policeStation = warehouse.getPoliceStation();
        if (dto.getPoliceStationId() != null && (policeStation == null || !dto.getPoliceStationId().equals(policeStation.getId()))) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("New Police Station not found with ID: " + dto.getPoliceStationId()));
        }

        // ম্যাপারের মাধ্যমে অবজেক্ট ডেটা আপডেট করা
        warehouseMapper.updateEntity(dto, warehouse, policeStation);

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponseDTO(updatedWarehouse);
    }

    // 3. Find All Warehouses

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponseDTO> findAll() {
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 4. Find Warehouse By ID

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseResponseDTO> getById(Long id) {
        return warehouseRepository.findById(id)
                .map(warehouseMapper::toResponseDTO);
    }

    // 5. Delete Warehouse
    @Override
    @Transactional
    public void delete(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with ID: " + id));

        warehouseRepository.delete(warehouse);
    }
}