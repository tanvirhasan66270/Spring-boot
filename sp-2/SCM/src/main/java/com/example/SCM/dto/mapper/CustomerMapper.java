package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.CustomerRequestDTO;
import com.example.SCM.dto.response.CustomerResponseDTO;
import com.example.SCM.entity.Customer;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;

import com.example.SCM.role.Role;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    /**
     * Maps CustomerRequestDTO to a new User entity.
     * Note: Password encoding should be handled in the service layer before saving.
     *
     * @param dto the incoming request data
     * @return populated User entity, or null if dto is null
     */
    public User toUserEntity(CustomerRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setRole(Role.CUSTOMER); // Sets the default role for this register flow

        return user;
    }

    /**
     * Maps CustomerRequestDTO along with already resolved/saved dependencies into a Customer entity.
     *
     * @param dto           the incoming request data
     * @param user          the linked User authentication account
     * @param policeStation the resolved PoliceStation entity from the DB (can be null)
     * @return populated Customer entity, or null if dto is null
     */
    public Customer toCustomerEntity(CustomerRequestDTO dto, User user, PoliceStation policeStation) {
        if (dto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());
        
        if (dto.getDob() != null && !dto.getDob().trim().isEmpty()) {
            try {
                customer.setDob(java.sql.Date.valueOf(dto.getDob()));
            } catch (IllegalArgumentException e) {
                try {
                    customer.setDob(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dto.getDob()));
                } catch (Exception ex) {
                    // Ignore parsing failure
                }
            }
        }
        
        customer.setPoliceStation(policeStation);

        return customer;
    }

    /**
     * Converts a complete Customer entity hierarchy into a flattened CustomerResponseDTO.
     * Built with null-safe checks to prevent NullPointerExceptions across relational tables.
     *
     * @param c the Customer entity fetched from the database
     * @return populated CustomerResponseDTO, or null if customer is null
     */
    public CustomerResponseDTO toResponseDTO(Customer c) {
        if (c == null) {
            return null;
        }

        CustomerResponseDTO dto = new CustomerResponseDTO();

        // 1. Map Core Customer Profile Data
        dto.setId(c.getId());
        dto.setAddress(c.getAddress());
        dto.setGender(c.getGender());
        dto.setDob(c.getDob() != null
                ? c.getDob().toString() : null);
        dto.setImage(c.getImage());

        // 2. Map Linked User (Auth) Data safely
        User user = c.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhoneNumber());
            dto.setRole(c.getUser().getRole() != null
                    ? c.getUser().getRole().name() : null);
        }

        // 3. Unwind and flatten Location Hierarchy safely (PoliceStation -> District -> Division)
        PoliceStation policeStation = c.getPoliceStation();
        if (policeStation != null) {
            dto.setPoliceStationId(policeStation.getId());
            dto.setPoliceStationName(policeStation.getName());

            if (policeStation.getDistrict() != null) {
                dto.setDistrictName(policeStation.getDistrict().getName());

                if (policeStation.getDistrict().getDivision() != null) {
                    dto.setDivisionName(policeStation.getDistrict().getDivision().getName());
                }
            }
        }

        return dto;
    }


}
