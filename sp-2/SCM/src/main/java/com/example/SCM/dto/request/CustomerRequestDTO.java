package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class CustomerRequestDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String address;
    private String gender;
    private String dob;       // String format "yyyy-MM-dd"
    private String nidNumber;
    private String image;
    private Long policeStationId;
}