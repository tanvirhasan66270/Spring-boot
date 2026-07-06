package com.example.SCM.service;

import com.example.SCM.dto.request.CustomerOrderRequestDTO;
import com.example.SCM.dto.response.CustomerOrderResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CustomerOrderService {
    CustomerOrderResponseDTO save(CustomerOrderRequestDTO dto);
    CustomerOrderResponseDTO update(Long id, CustomerOrderRequestDTO dto);
    List<CustomerOrderResponseDTO> findAll();
    Optional<CustomerOrderResponseDTO> getById(Long id);
    void delete(Long id);
    Optional<CustomerOrderResponseDTO> trackOrder(String orderNumber);

    // ➕ দুই-ধাপের পেমেন্ট ভেরিফিকেশন ইঞ্জিন মডিউল
    void processFinalPaymentConfirmation(Long orderId, double amountPaid, String method);
}