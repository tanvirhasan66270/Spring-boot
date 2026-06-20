package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.DeliveryTripRequestDTO;
import com.example.SCM.dto.response.DeliveryTripResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.enumClass.DeliveryTripStatus;
import org.springframework.stereotype.Component;

@Component
public class DeliveryTripMapper {

    public DeliveryTripResponseDTO toResponseDTO(DeliveryTrip entity) {
        if (entity == null) return null;

        DeliveryTripResponseDTO dto = new DeliveryTripResponseDTO();
        dto.setId(entity.getId());
        dto.setSendBy(entity.getSendBy());
        if (entity.getStatus() != null) dto.setStatus(entity.getStatus().name());
        if (entity.getStartedAt() != null) dto.setStartedAt(entity.getStartedAt().toString());
        if (entity.getCompletedAt() != null) dto.setCompletedAt(entity.getCompletedAt().toString());
        dto.setRecipientSignature(entity.getRecipientSignature());
        dto.setDeliveryPhotoUrl(entity.getDeliveryPhotoUrl());
        dto.setCustomerAddress(entity.getCustomerAddress());
        dto.setVehicleInfo(entity.getVehicleInfo());
        dto.setDestinationInfo(entity.getDestinationInfo());
        dto.setScheduleInfo(entity.getScheduleInfo());
        dto.setTripInfo(entity.getTripInfo());
        if (entity.getCreatedAt() != null) dto.setCreatedAt(entity.getCreatedAt().toString());
        if (entity.getUpdatedAt() != null) dto.setUpdatedAt(entity.getUpdatedAt().toString());

        // 🔗 DeliveryTripMapper.java ক্লাসের ভেতরের সংশোধিত অংশ:
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
            dto.setRecipientName(entity.getCustomer().getName());

            // 💡 যদি কাস্টমার অবজেক্টের ভেতরে কোনো ইউজার রিলেশন থাকে, তবে সেখান থেকে নাম তুলে অটো-ফিক্স করবে
            if (entity.getCustomer().getUser() != null) {
                dto.setRecipientName(entity.getCustomer().getUser().getName());
            } else {
                // সেফটি ব্যাকআপ হিসেবে কাস্টমারের নামকেও অ্যাসাইন করে রাখতে পারেন
                dto.setRecipientName(entity.getCustomer().getName());
            }
        }
        // 💡 Auto Fixed Driver details from User Node
        if (entity.getDriver() != null) {
            dto.setDriverId(entity.getDriver().getId());
            dto.setDriverName(entity.getDriver().getDriverName());
            dto.setDriverEmail(entity.getDriver().getEmail());
        }
        if (entity.getVehicle() != null) {
            dto.setVehicleId(entity.getVehicle().getId());
            dto.setVehiclePlateNumber(entity.getVehicle().getPlateNumber());
        }
        return dto;
    }

    public DeliveryTrip toEntity(DeliveryTripRequestDTO dto, Customer customer, Driver driver, Vehicle vehicle) {
        if (dto == null) return null;

        DeliveryTrip entity = new DeliveryTrip();
        entity.setSendBy(dto.getSendBy());
        entity.setStatus(dto.getStatus() != null ? DeliveryTripStatus.valueOf(dto.getStatus().toUpperCase()) : DeliveryTripStatus.PENDING);
        entity.setRecipientSignature(dto.getRecipientSignature());
        entity.setDeliveryPhotoUrl(dto.getDeliveryPhotoUrl());
        entity.setCustomerAddress(dto.getCustomerAddress());
        entity.setVehicleInfo(dto.getVehicleInfo());
        entity.setDestinationInfo(dto.getDestinationInfo());
        entity.setScheduleInfo(dto.getScheduleInfo());
        entity.setTripInfo(dto.getTripInfo());
        entity.setCustomer(customer);
        entity.setDriver(driver);
        entity.setVehicle(vehicle);

        return entity;
    }

    public void updateEntity(DeliveryTripRequestDTO dto, DeliveryTrip entity, Customer customer, Driver driver, Vehicle vehicle) {
        if (dto == null || entity == null) return;

        if (dto.getSendBy() != null) entity.setSendBy(dto.getSendBy());
        if (dto.getStatus() != null) entity.setStatus(DeliveryTripStatus.valueOf(dto.getStatus().toUpperCase()));
        if (dto.getCustomerAddress() != null) entity.setCustomerAddress(dto.getCustomerAddress());
        if (dto.getVehicleInfo() != null) entity.setVehicleInfo(dto.getVehicleInfo());
        if (dto.getDestinationInfo() != null) entity.setDestinationInfo(dto.getDestinationInfo());
        if (dto.getScheduleInfo() != null) entity.setScheduleInfo(dto.getScheduleInfo());
        if (dto.getTripInfo() != null) entity.setTripInfo(dto.getTripInfo());

        if (customer != null) entity.setCustomer(customer);
        if (driver != null) entity.setDriver(driver);
        if (vehicle != null) entity.setVehicle(vehicle);
    }
}