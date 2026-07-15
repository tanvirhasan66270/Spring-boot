package com.example.SCM.repository;

import com.example.SCM.entity.PurchaseRequisitionToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PurchaseRequisitionTokenRepository extends JpaRepository<PurchaseRequisitionToken, Long> {

    // Token দিয়ে খুঁজবে
    Optional<PurchaseRequisitionToken> findByToken(String token);

    // Purchase Requisition ID দিয়ে খুঁজবে (Update-এর জন্য প্রয়োজন)
    Optional<PurchaseRequisitionToken> findByPurchaseRequisitionId(Long purchaseRequisitionId);

    // Required token খুঁজবে
    List<PurchaseRequisitionToken> findByRequiredByDateLessThanEqual(LocalDate date);

    // Active এবং Required token খুঁজবে (Scheduler-এর জন্য)
    List<PurchaseRequisitionToken> findByActiveTrueAndRequiredByDateLessThanEqual(LocalDate date);
}