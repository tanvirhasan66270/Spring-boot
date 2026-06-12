package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.CustomerRequestDTO;
import com.example.SCM.dto.Response.CustomerResponseDTO;
import com.example.SCM.entity.Customer;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;

import com.example.SCM.role.Role;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class CustomerMapper {


    public static CustomerResponseDTO toResponseDTO(Customer c) {
//        if (c == null) {
//            return null;
//        }

        CustomerResponseDTO dto = new CustomerResponseDTO();

        dto.setId(c.getId());
        dto.setAddress(c.getAddress());
        dto.setGender(c.getGender());
        dto.setDob(c.getDob() != null
                ? c.getDob().toString() : null);
        dto.setImage(c.getImage());

        User user = c.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhoneNumber());
            dto.setRole(c.getUser().getRole() != null
                    ? c.getUser().getRole().name() : null);
        }

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


    public User toUserEntity(CustomerRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setRole(Role.CUSTOMER);

        return user;
    }


    public Customer toCustomerEntity(CustomerRequestDTO dto, User user, PoliceStation policeStation) {
        if (dto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());

        if (dto.getDob() != null && !dto.getDob().isBlank()) {
            try {
                customer.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDob()));
            } catch (ParseException e) {
                throw new RuntimeException("Invalid date format. Use yyyy-MM-dd");
            }
        }
        customer.setPoliceStation(policeStation);

        return customer;
    }


}
