package com.example.SCM.service;

import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface CustomerOrderService {
    CustomerOrderResponseDTO save(CustomerOrderRequestDTO dto, MultipartFile image);
    CustomerOrderResponseDTO update(Long id, CustomerOrderRequestDTO dto, MultipartFile image);

    // 🌟 স্ট্যাটাস আপডেট মেথড সিগনেচার
    CustomerOrderResponseDTO updateOrderStatus(Long id, String status);

    List<CustomerOrderResponseDTO> findAll();


    // 🌟 কাস্টমার ভেদে তার নিজের অর্ডারগুলো পাওয়ার সিগনেচার
    List<CustomerOrderResponseDTO> findByCustomerUsername(String username);
//    List<CustomerOrderResponseDTO> findByCustomerId(Long id);

    Optional<CustomerOrderResponseDTO> getById(Long id);
    void delete(Long id);
    Optional<CustomerOrderResponseDTO> trackOrder(String orderNumber);

    // দুই-ধাপের পেমেন্ট ভেরিফিকেশন ইঞ্জিন মডিউল
    void processFinalPaymentConfirmation(Long orderId, double amountPaid, String method);
}