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

    // ১. নির্দিষ্ট কোনো ইউজারের (requestedBy) তৈরি করা সমস্ত রিকুইজিশন লিস্ট বের করা
    // (ইউজারের নিজস্ব রিকুইজিশন হিস্ট্রি স্ক্রিনের জন্য)
    List<PurchaseRequisition> findByRequestedBy(Long requestedBy);

    // ২. স্ট্যাটাস অনুযায়ী ফিল্টার করা (যেমন: এপ্রুভালের জন্য সমস্ত PENDING রিকুইজিশন ড্যাশবোর্ডে দেখানোর জন্য)
    List<PurchaseRequisition> findByApprovalStatus(PurchaseRequisitionStatus approvalStatus);

    // ৩. উগ্রতা বা গুরুত্ব লেভেল অনুযায়ী ফিল্টার করা (যেমন: CRITICAL বা HIGH রিকুইজিশনগুলো আগে প্রসেস করার জন্য)
    List<PurchaseRequisition> findByUrgencyLevel(UrgencyLevel urgencyLevel);

    // ৪. কাস্টম JPQL কুয়েরি: একটি নির্দিষ্ট প্রোডাক্ট আইডি (ProductId) কোন কোন রিকুইজিশনের ভেতরে আছে তা খুঁজে বের করা
    // (মেনি-টু-মেনি জয়েন টেবিল `requisition_products` ট্রাভার্স করার জন্য)
    @Query("SELECT pr FROM PurchaseRequisition pr JOIN pr.products p WHERE p.id = :productId")
    List<PurchaseRequisition> findByProductId(@Param("productId") Long productId);

    // ৫. কাস্টম JPQL কুয়েরি: কোনো নির্দিষ্ট সাপ্লায়ার আইডি (SupplierId) কোন কোন রিকুইজিশনে সাজেস্ট করা হয়েছে তা বের করা
    @Query("SELECT pr FROM PurchaseRequisition pr JOIN pr.suppliers s WHERE s.id = :supplierId")
    List<PurchaseRequisition> findBySupplierId(@Param("supplierId") Long supplierId);

    // ৬. ম্যানেজারের এপ্রুভাল হিস্ট্রি ট্র্যাকিং (কোন ম্যানেজার কোন কোন পিআর এপ্রুভ বা রিজেক্ট করেছেন)
    List<PurchaseRequisition> findByApprovedBy(Long approvedBy);
}