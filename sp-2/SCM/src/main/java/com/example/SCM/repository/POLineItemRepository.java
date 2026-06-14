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


//    Optional<POLineItem> findByTrackingCode(String trackingCode);
//
//    boolean existsByTrackingCode(String trackingCode);


    // ১. একটি নির্দিষ্ট Purchase Order (poId) এর আন্ডারে থাকা সমস্ত লাইন আইটেম খুঁজে বের করা
    // 💡 এটি আপনার রোল-আপ গ্র্যান্ড টোটাল লজিক (Stream Summation) এর জন্য সবচেয়ে বেশি ব্যবহৃত হবে
    List<POLineItem> findByPurchaseOrderId(Long poId);

    // ২. নির্দিষ্ট স্ট্যাটাস অনুযায়ী লাইন আইটেম ফিল্টার করা (যেমন: কতগুলো আইটেম SHIPPED বা PENDING অবস্থায় আছে)
    List<POLineItem> findByStatus(POLineItemStatus status);

    // ৩. কোনো নির্দিষ্ট প্রোডাক্ট আইডি (productId) দিয়ে লাইন আইটেম ট্র্যাক করা
    List<POLineItem> findByProductId(Long productId);

    // ৪. ইউনিক ট্র্যাকিং নাম্বার (trackingNumber) দিয়ে নির্দিষ্ট আইটেম খুঁজে বের করা
    // (লজিস্টিকস বা শিপমেন্ট ট্র্যাকিং স্ক্রিনের জন্য)


    POLineItem findByTrackingNumber(String trackingNumber);

    // ৫. কাস্টম JPQL কোয়েরি: নির্দিষ্ট PO এর আন্ডারে শুধুমাত্র সক্রিয় (যা CANCELLED নয়) আইটেমগুলোর লিস্ট আনা
    // (যদি আপনি শুধুমাত্র একটিভ আইটেমগুলোর যোগফল দিয়ে grandTotal আপডেট করতে চান)
    @Query("SELECT p FROM POLineItem p WHERE p.purchaseOrder.id = :poId AND p.status <> com.example.SCM.enumClass.POLineItemStatus.CANCELLED")
    List<POLineItem> findActiveLineItemsByPoId(@Param("poId") Long poId);

    // ৬. কাস্টম JPQL কোয়েরি: ডাটাবেজ লেভেলেই সরাসরি একটি PO এর সমস্ত লাইনের মোট যোগফল (Roll-up sum) বের করা
    // (সার্ভিস লেয়ারে স্ট্রিমিং না করে সরাসরি ডাটাবেজ থেকে গ্র্যান্ড টোটাল সাম বের করার অল্টারনেটিভ অপ্টিমাইজড মেথড)
    @Query("SELECT COALESCE(SUM(p.lineTotal), 0.0) FROM POLineItem p WHERE p.purchaseOrder.id = :poId")
    double getSumOfLineTotalsByPoId(@Param("poId") Long poId);
}