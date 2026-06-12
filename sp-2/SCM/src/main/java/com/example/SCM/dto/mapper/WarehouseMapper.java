package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.WarehouseRequestDTO;

import com.example.SCM.dto.response.WarehouseResponseDTO;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    // WarehouseRequestDTO এবং অ্যাসোসিয়েটেড PoliceStation থেকে Warehouse Entity-তে রূপান্তর (Create Operation)

    public Warehouse toEntity(WarehouseRequestDTO dto, PoliceStation policeStation) {
        if (dto == null) {
            return null;
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        warehouse.setCapacity(dto.getCapacity());
        warehouse.setManagerId(dto.getManagerId());
        warehouse.setActive(dto.isActive());

        // রিলেশনাল পুলিশ স্টেশন (থানা) অবজেক্ট ইনজেক্ট করা
        warehouse.setPoliceStation(policeStation);

        return warehouse;
    }

    /**
     * Warehouse Entity থেকে WarehouseResponseDTO-তে রূপান্তর (Read/All Operations)
     * এখানে সম্পূর্ণ লোকেশন গ্রাফটিকে ফ্ল্যাট করে স্ট্রিং ডাটায় রূপান্তর করা হয়েছে।
     */
    public WarehouseResponseDTO toResponseDTO(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }

        WarehouseResponseDTO dto = new WarehouseResponseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        dto.setCapacity(warehouse.getCapacity());
        dto.setManagerId(warehouse.getManagerId());
        dto.setActive(warehouse.isActive());
        dto.setCreatedAt(warehouse.getCreatedAt());
        dto.setUpdatedAt(warehouse.getUpdatedAt());

        // লোকেশন চেইন ব্রেকিং ও নাল-সেফ ম্যাপিং (PoliceStation -> District -> Division)
        if (warehouse.getPoliceStation() != null) {
            PoliceStation ps = warehouse.getPoliceStation();
            dto.setPoliceStationId(ps.getId());
            dto.setPoliceStationName(ps.getName());

            // যদি PoliceStation এনটিটির সাথে District-এর সম্পর্ক থাকে
            if (ps.getDistrict() != null) {
                dto.setDistrictName(ps.getDistrict().getName());

                // যদি District এনটিটির সাথে Division-এর সম্পর্ক থাকে
                if (ps.getDistrict().getDivision() != null) {
                    dto.setDivisionName(ps.getDistrict().getDivision().getName());
                }
            }
        }

        return dto;
    }

    // এক্সিস্টিং Warehouse Entity-কে Request DTO এবং নতুন PoliceStation দিয়ে আপডেট করা (Update Operation)

    public void updateEntity(WarehouseRequestDTO dto, Warehouse warehouse, PoliceStation policeStation) {
        if (dto == null || warehouse == null) {
            return;
        }

        if (dto.getName() != null) warehouse.setName(dto.getName());
        if (dto.getLocation() != null) warehouse.setLocation(dto.getLocation());

        warehouse.setCapacity(dto.getCapacity());
        warehouse.setManagerId(dto.getManagerId());
        warehouse.setActive(dto.isActive());

        // পুলিশ স্টেশন পরিবর্তন করা হলে নতুন অবজেক্ট সেট হবে
        if (policeStation != null) {
            warehouse.setPoliceStation(policeStation);
        }
    }
}