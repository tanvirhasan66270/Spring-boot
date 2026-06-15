package com.example.SCM.repository;

import com.example.SCM.entity.PurchaseOrder;
import com.example.SCM.enumClass.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    // ১. ইউনিক পিও নাম্বার (poNumber) দিয়ে পারচেজ অর্ডার খুঁজে বের করা (ইনভয়েস বা ট্র্যাকিংয়ের জন্য)
    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    // ২. নির্দিষ্ট কোনো সাপ্লায়ারের (SupplierId) আন্ডারে তৈরি হওয়া সব পিও লিস্ট বের করা
    List<PurchaseOrder> findBySupplierId(Long supplierId);

    // ৩. নির্দিষ্ট পারচেজ রিকুইজিশন (PurchaseRequisitionId) দিয়ে পিও চেক করা
    Optional<PurchaseOrder> findByPurchaseRequisitionId(Long purchaseRequisitionId);

    // ৪. স্ট্যাটাস অনুযায়ী ফিল্টার করা (যেমন: কোন কোন অর্ডার এখনো ISSUED বা RECEIVED অবস্থায় আছে)
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    // ৫. নির্দিষ্ট কোনো অফিসার (issuedBy) মোট কতগুলো অর্ডার ইস্যু করেছেন তার তালিকা
    List<PurchaseOrder> findByIssuedBy(Long issuedBy);


    @Query("SELECT po FROM PurchaseOrder po WHERE po.totalAmount >= :amount")
    List<PurchaseOrder> findHighValueOrders(@Param("amount") double amount);


}