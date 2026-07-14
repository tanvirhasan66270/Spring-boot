package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.DriverRequestDTO;
import com.example.SCM.dto.response.DriverResponseDTO;
import com.example.SCM.entity.*;
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
        Driver driver = new Driver();
        driver.setDriverName(dto.getDriverName());
        driver.setPhone(dto.getPhone());
        driver.setAddress(dto.getAddress());
        driver.setNidNumber(dto.getNidNumber());
        driver.setGender(dto.getGender());
        driver.setEmail(dto.getEmail());
        driver.setVehicleType(dto.getVehicleType());
        driver.setVehicleNumber(dto.getVehicleNumber());
        driver.setDob(dto.getDob());
        driver.setRating(dto.getRating());
        driver.setTotalDeliveries(dto.getTotalDeliveries());
        driver.setTotalEarnings(dto.getTotalEarnings());
        driver.setImage(dto.getImage());
        driver.setUser(user);
        driver.setActive(false);
        driver.setPoliceStation(policeStation);
        driver.setWarehouses(warehouses);
        return driver;

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
        dto.setImage(driver.getImage());
        dto.setCreatedAt(driver.getCreatedAt());
        dto.setUpdatedAt(driver.getUpdatedAt());

        // =========================
        // LOCATION INFORMATION
        // =========================
        if (driver.getPoliceStation() != null) {

            PoliceStation ps = driver.getPoliceStation();

            dto.setPoliceStationId(ps.getId());
            dto.setPoliceStationName(ps.getName());

            District district = ps.getDistrict();

            if (district != null) {

                dto.setDistrictId(district.getId());
                dto.setDistrictName(district.getName());

                Division division = district.getDivision();

                if (division != null) {

                    dto.setDivisionId(division.getId());
                    dto.setDivisionName(division.getName());

                    Country country = division.getCountry();

                    if (country != null) {

                        dto.setCountryId(country.getId());
                        dto.setCountryName(country.getName());

                    }
                }
            }
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