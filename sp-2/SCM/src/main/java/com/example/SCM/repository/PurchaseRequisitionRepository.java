package com.example.SCM.repository;

import com.example.SCM.entity.PurchaseRequisition;
import com.example.SCM.enumClass.PurchaseRequisitionStatus;
import com.example.SCM.enumClass.UrgencyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRequisitionRepository extends JpaRepository<PurchaseRequisition, Long> {

    List<PurchaseRequisition> findByRequestedBy(Long requestedBy);
    List<PurchaseRequisition> findByApprovalStatus(PurchaseRequisitionStatus approvalStatus);
    List<PurchaseRequisition> findByUrgencyLevel(UrgencyLevel urgencyLevel);

    @Query("SELECT pr FROM PurchaseRequisition pr JOIN pr.products p WHERE p.id = :productId")
    List<PurchaseRequisition> findByProductId(@Param("productId") Long productId);

    /**
     * 🎯 সাপ্লায়ার সিকিউরিটি ফিল্টার গেটওয়ে
     * (সাপ্লায়ার যখন তার প্যানেল থেকে ডেটা রিড করার রিকোয়েস্ট পাঠাবে, সে শুধু APPROVED ডেটা রিড করতে পারবে)
     */
    @Query("SELECT pr FROM PurchaseRequisition pr JOIN pr.suppliers s WHERE s.id = :supplierId AND pr.approvalStatus = com.example.SCM.enumClass.PurchaseRequisitionStatus.APPROVED")
    List<PurchaseRequisition> findApprovedRequisitionsForSupplier(@Param("supplierId") Long supplierId);

    List<PurchaseRequisition> findByApprovedBy(Long approvedBy);
}