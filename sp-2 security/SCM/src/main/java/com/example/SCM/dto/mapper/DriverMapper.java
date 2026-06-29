package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.DriverRequestDTO;
import com.example.SCM.dto.response.DriverResponseDTO;
import com.example.SCM.entity.Driver;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;
import com.example.SCM.entity.Warehouse;
import com.example.SCM.role.Role;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * DriverMapper
 *
 * Responsible for converting:
 * 1. DriverRequestDTO -> User/Driver Entity
 * 2. Driver Entity -> DriverResponseDTO
 *
 * This class helps separate API models (DTOs)
 * from database entities.
 */
@Component
public class DriverMapper {

       /**
     * Convert DriverRequestDTO to Driver Entity.
     *
     * @param dto Incoming request data from client
     * @param user Associated User account reference
     * @param warehouses Bound monitoring branch storage locations
     * @return Driver entity instance
     */
    public Driver toDriverEntity(DriverRequestDTO dto, User user, Set<Warehouse> warehouses , PoliceStation policeStation) {
        return Driver.builder()
                .driverName(dto.getDriverName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .nidNumber(dto.getNidNumber())
                .gender(dto.getGender())
                .email(dto.getEmail())
                .vehicleType(dto.getVehicleType())
                .vehicleNumber(dto.getVehicleNumber())
                .dob(dto.getDob())
                .rating(dto.getRating())
                .totalDeliveries(dto.getTotalDeliveries())
                .totalEarnings(dto.getTotalEarnings())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .image(dto.getImage())
                .user(user)
                .policeStation(policeStation)
                .warehouses(warehouses)
                .build()
               ;

    }

    /**
     * Convert Driver Entity to DriverResponseDTO.
     *
     * @param driver Driver entity from database
     * @return DriverResponseDTO
     */
    public DriverResponseDTO convertTOResponseDTO(Driver driver) {

        DriverResponseDTO dto = new DriverResponseDTO();
        dto.setId(driver.getId());
        dto.setDriverName(driver.getDriverName());
        dto.setPhone(driver.getPhone());
        dto.setAddress(driver.getAddress());
        dto.setNidNumber(driver.getNidNumber());
        dto.setGender(driver.getGender());
        dto.setEmail(driver.getEmail());
        dto.setVehicleType(driver.getVehicleType());
        dto.setVehicleNumber(driver.getVehicleNumber());
        dto.setDob(driver.getDob());
        dto.setRating(driver.getRating());
        dto.setTotalDeliveries(driver.getTotalDeliveries());
        dto.setTotalEarnings(driver.getTotalEarnings());
        dto.setActive(driver.getActive());
        dto.setImage(driver.getImage());
        dto.setCreatedAt(driver.getCreatedAt());
        dto.setUpdatedAt(driver.getUpdatedAt());

        if (driver.getPoliceStation() != null) {
            dto.setPoliceStationId(driver.getPoliceStation().getId());
            dto.setPoliceStationName(driver.getPoliceStation().getName());
        }

        if (driver.getUser() != null) {
            dto.setUserId(driver.getUser().getId());
            dto.setDriverName(driver.getUser().getName());
            dto.setEmail(driver.getUser().getEmail());
            dto.setPhone(driver.getUser().getPhoneNumber());
            if (driver.getUser().getRole() != null) {
                dto.setRole(driver.getUser().getRole().name());
            }
        }

        if (driver.getWarehouses() != null) {
            dto.setWarehouseNames(driver.getWarehouses().stream()
                    .map(Warehouse::getName)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

}