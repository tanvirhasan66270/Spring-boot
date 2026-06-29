package com.example.SCM.service;

import com.example.SCM.dto.request.PurchaseOrderRequestDTO;
import com.example.SCM.dto.response.PurchaseOrderResponseDTO;
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderService {

    // (CRUD)

    PurchaseOrderResponseDTO save(PurchaseOrderRequestDTO dto);

    PurchaseOrderResponseDTO update(Long id, PurchaseOrderRequestDTO dto);

    List<PurchaseOrderResponseDTO> findAll();

    Optional<PurchaseOrderResponseDTO> getById(Long id);

    void delete(Long id);





    PurchaseOrderResponseDTO managerIssuedOrder(Long id, Long managerId);


    PurchaseOrderResponseDTO supplierReceivedOrder(Long id);


    PurchaseOrderResponseDTO updateShipmentQuantityCheck(Long id, int shippedQuantity);
}