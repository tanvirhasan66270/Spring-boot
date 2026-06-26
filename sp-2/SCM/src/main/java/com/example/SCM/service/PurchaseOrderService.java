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



    /**
     *  ম্যানেজারের ইমেইল বাটন থেকে PO স্ট্যাটাস ISSUED করার গেটওয়ে
     */
    PurchaseOrderResponseDTO managerIssuedOrder(Long id, Long managerId);

    /**
     * 🤝 সাপ্লায়ারের ড্যাশবোর্ড বা ইমেইল থেকে অর্ডার কনফার্ম/RECEIVED করার গেটওয়ে
     */
    PurchaseOrderResponseDTO supplierReceivedOrder(Long id);

    /**
     * 🚛 শিপমেন্টের সময় কোয়ান্টিটি চেক করে অটোমেটিক PARTIALLY_RECEIVED বা RECEIVED করার গেটওয়ে
     */
    PurchaseOrderResponseDTO updateShipmentQuantityCheck(Long id, int shippedQuantity);
}