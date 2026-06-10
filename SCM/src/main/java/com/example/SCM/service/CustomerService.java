package com.example.SCM.service;

import com.example.SCM.entity.Customer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public interface CustomerService {
    Customer save(Customer c, MultipartFile file);
    List<Customer> findAll();
    Optional<Customer> getById(Long id);
    void delete(Long id);
}
