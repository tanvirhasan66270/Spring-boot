package com.example.SCM.repository;

import com.example.SCM.entity.GRNLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GRNLineItemRepository extends JpaRepository<GRNLineItem, Long> {

    /**
     * 1. অপ্টিমাইজড অল-লাইন আইটেম লিস্ট কুয়েরি (Fetch Join)
     * 💡 এটি সিঙ্গেল ডাটাবেজ হিটে সম্পূর্ণ রিলেশনাল অবজেক্ট চেইন (GoodsReceivedNote এবং Product) লোড করে পারফরম্যান্স বহুগুণ বাড়িয়ে দেবে।
     */
    @Query("""
        SELECT DISTINCT i FROM GRNLineItem i
        LEFT JOIN FETCH i.goodsReceivedNote g
        LEFT JOIN FETCH i.product p
    """)
    List<GRNLineItem> findAllItemsWithDetails();

    /**
     * 2. আইডি দিয়ে সিঙ্গেল লাইন আইটেম খোঁজা (Fetch Join মেকানিজম)
     */
    @Query("""
        SELECT i FROM GRNLineItem i
        LEFT JOIN FETCH i.goodsReceivedNote g
        LEFT JOIN FETCH i.product p
        WHERE i.id = :id
    """)
    Optional<GRNLineItem> findByIdWithDetails(@Param("id") Long id);

    /**
     * 3. 🎯 সুনির্দিষ্ট GRN ID দিয়ে তার অধীনে থাকা সমস্ত লাইন আইটেম বা প্রোডাক্ট ব্রেকডাউন খুঁজে বের করা
     * 💡 এটি আপনার ফ্রন্টএন্ডে একটি নির্দিষ্ট জিআরএন-এর আন্ডারে থাকা সমস্ত প্রোডাক্ট গ্রিড ভিউতে পপুলেট করতে সবচেয়ে বেশি সাহায্য করবে।
     */
    @Query("""
        SELECT i FROM GRNLineItem i
        LEFT JOIN FETCH i.product p
        WHERE i.goodsReceivedNote.id = :grnId
    """)
    List<GRNLineItem> findByGoodsReceivedNoteId(@Param("grnId") Long grnId);

    /**
     * 4. নির্দিষ্ট প্রোডাক্ট আইডি (Product ID) দিয়ে সমস্ত জিআরএন ট্র্যাকিং ফিল্টার
     */
    List<GRNLineItem> findByProductId(Long productId);
}