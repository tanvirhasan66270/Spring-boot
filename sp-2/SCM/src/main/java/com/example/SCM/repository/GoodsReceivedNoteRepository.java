package com.example.SCM.repository;

import com.example.SCM.entity.GoodsReceivedNote;
import com.example.SCM.enumClass.GRNStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsReceivedNoteRepository extends JpaRepository<GoodsReceivedNote, Long> {

    /**
     * 1. অপ্টিমাইজড অল-জিআরএন লিস্ট কুয়েরি (Fetch Join)
     * 💡 এটি সিঙ্গেল ডাটাবেজ হিটে সম্পূর্ণ রিলেশনাল অবজেক্ট চেইন (PO, Product, Warehouse, Users) লোড করে পারফরম্যান্স বহুগুণ বাড়িয়ে দেবে।
     */
    @Query("""
        SELECT DISTINCT g FROM GoodsReceivedNote g
        LEFT JOIN FETCH g.purchaseOrder po
        LEFT JOIN FETCH g.product p
        LEFT JOIN FETCH g.warehouse w
        LEFT JOIN FETCH g.receivedBy rb
        LEFT JOIN FETCH g.inspectedBy ib
    """)
    List<GoodsReceivedNote> findAllGRNs();

    /**
     * 2. আইডি দিয়ে সিঙ্গেল জিআরএন খোঁজা (Fetch Join মেকানিজম)
     * আপনার 'RiderRepository'-এর 'findByIdWithZones' প্যাটার্ন অনুযায়ী হুবহু সিঙ্কড।
     */
    @Query("""
        SELECT g FROM GoodsReceivedNote g
        LEFT JOIN FETCH g.purchaseOrder po
        LEFT JOIN FETCH g.product p
        LEFT JOIN FETCH g.warehouse w
        LEFT JOIN FETCH g.receivedBy rb
        LEFT JOIN FETCH g.inspectedBy ib
        WHERE g.id = :id
    """)
    Optional<GoodsReceivedNote> findByIdWithDetails(@Param("id") Long id);

    /**
     * 3. অটো-জেনারেটেড ইউনিক GRN নাম্বার দিয়ে কুয়েরি করা (ট্র্যাকিং ড্যাশবোর্ডের জন্য)
     */
    Optional<GoodsReceivedNote> findByGrnNumber(String grnNumber);

    /**
     * 4. নির্দিষ্ট পারচেজ অর্ডার (PO) আইডি দিয়ে সমস্ত জিআরএন ট্র্যাকিং (যেমন: একটি PO-র মাল পারশিয়ালি কয়েকবারে ঢুকলে)
     */
    List<GoodsReceivedNote> findByPurchaseOrderId(Long purchaseOrderId);

    /**
     * 5. নির্দিষ্ট ওয়ারহাউজ আইডি অনুযায়ী ইনভেন্টরি জিআরএন ফিল্টার করা
     */
    List<GoodsReceivedNote> findByWarehouseId(Long warehouseId);

    /**
     * 6. জিআরএন স্ট্যাটাস অনুযায়ী ফিল্টার করা (যেমন: কতগুলো PENDING বা APPROVED অবস্থায় আছে)
     */
    List<GoodsReceivedNote> findByStatus(GRNStatus status);

    /**
     * 7. মাল রিসিভকারী ইউজার (Login User) এর আইডি দিয়ে কুয়েরি করা
     */
    List<GoodsReceivedNote> findByReceivedById(Long receivedById);

    /**
     * 8. ইন্সপেকশন সম্পন্ন করা কোয়ালিটি কন্ট্রোল (QC) ইনস্পেক্টরের ইউজার আইডি দিয়ে ফিল্টার করা
     */
    List<GoodsReceivedNote> findByInspectedById(Long inspectedById);
}