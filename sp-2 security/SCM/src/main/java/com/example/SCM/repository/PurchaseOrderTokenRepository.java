package com.example.SCM.repository;

import com.example.SCM.entity.PurchaseOrderToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderTokenRepository
        extends JpaRepository<PurchaseOrderToken,Long> {

    Optional<PurchaseOrderToken> findByPurchaseOrderId(Long purchaseOrderId);

    List<PurchaseOrderToken> findByActiveTrueAndExpiryDateLessThanEqual(LocalDate date);}