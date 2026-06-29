package com.example.SCM.repository;

import com.example.SCM.entity.POLineItem;
import com.example.SCM.enumClass.POLineItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface POLineItemRepository extends JpaRepository<POLineItem, Long> {



    // ৩. ইউনিক ট্র্যাকিং নাম্বার (trackingNumber) দিয়ে নির্দিষ্ট আইটেম খুঁজে বের করা (Optional টাইপ-সেফ সেটিং)
    Optional<POLineItem> findByTrackingNumber(String trackingNumber);

    // ৪. কাস্টম JPQL কুয়েরি: একটি PO এর আন্ডারে থাকা শুধুমাত্র সক্রিয় (যা CANCELLED নয়) আইটেমগুলোর মোট যোগফল বের করা
    // 💡 যেহেতু PurchaseOrder টেবিলে grandTotal কলাম নেই, এই কুয়েরিটি সরাসরি ডাটাবেজ থেকে রিয়েল-টাইম totalAmount হিসাব করে আনবে
    @Query("SELECT COALESCE(SUM(p.lineTotal), 0.0) FROM POLineItem p WHERE p.purchaseOrder.id = :poId AND p.status <> com.example.SCM.enumClass.POLineItemStatus.CANCELLED")
    double getActiveTotalAmountByPoId(@Param("poId") Long poId);
}