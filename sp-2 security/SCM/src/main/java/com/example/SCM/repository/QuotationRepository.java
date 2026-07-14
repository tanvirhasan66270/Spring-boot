package com.example.SCM.repository;

import com.example.SCM.entity.Quotation;
import com.example.SCM.enumClass.QuotationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    List<Quotation> findByValidUntilLessThanEqual(LocalDate date);


    // ১. ইউনিক কোটেশন নাম্বার দিয়ে কোটেশন খুঁজে বের করার জন্য
    Optional<Quotation> findByQuotationNumber(String quotationNumber);

    // ২. একটি নির্দিষ্ট Purchase Requisition-এর অধীনে সব কোটেশন দেখার জন্য
    List<Quotation> findByPurchaseRequisitionId(Long purchaseRequisitionId);

    // ৩. একটি নির্দিষ্ট সাপ্লায়ারের পাঠানো সব কোটেশন দেখার জন্য
    List<Quotation> findBySupplierId(Long supplierId);

    // ৪. স্ট্যাটাস অনুযায়ী ফিল্টার করার জন্য (যেমন: PENDING, APPROVED)
    List<Quotation> findByStatus(QuotationStatus status);

    // ৫. নির্দিষ্ট রিকুইজিশনের মধ্যে যে কোটেশনটি সিলেক্ট বা উইন (isSelected = true) হয়েছে তা দেখার জন্য
    Optional<Quotation> findByPurchaseRequisitionIdAndIsSelectedTrue(Long purchaseRequisitionId);

    // 💡 এডভান্সড কোয়েরি: নির্দিষ্ট রিকুইজিশনের জন্য সবচেয়ে কম প্রাইসের কোটেশনটি খুঁজে বের করা
    @Query("SELECT q FROM Quotation q WHERE q.purchaseRequisition.id = :requisitionId ORDER BY q.totalPrice ASC")
    List<Quotation> findLowestPriceQuotationsByRequisition(@Param("requisitionId") Long requisitionId);
}