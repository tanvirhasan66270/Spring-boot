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

    /**
     * 1. অপ্টিমাইজড অল-পারচেজ অর্ডার লিস্ট কুয়েরি (Fetch Join)
     * 💡 এটি সিঙ্গেল ডাটাবেজ হিটে সম্পূর্ণ রিলেশনাল অবজেক্ট চেইন (Quotation, Supplier, PurchaseRequisition) লোড করে পারফরম্যান্স বহুগুণ বাড়িয়ে দেবে।
     */
    @Query("""
        SELECT DISTINCT p FROM PurchaseOrder p
        LEFT JOIN FETCH p.supplier s
        LEFT JOIN FETCH p.purchaseRequisition pr
        LEFT JOIN FETCH p.quotation q
    """)
    List<PurchaseOrder> findAllPurchaseOrders();

    /**
     * 2. আইডি দিয়ে সিঙ্গেল পারচেজ অর্ডার খোঁজা (Fetch Join মেকানিজম)
     * আপনার 'RiderRepository'-এর 'findByIdWithZones' প্যাটার্ন অনুযায়ী হুবহু সিঙ্কড।
     */
    @Query("""
        SELECT p FROM PurchaseOrder p
        LEFT JOIN FETCH p.supplier s
        LEFT JOIN FETCH p.purchaseRequisition pr
        LEFT JOIN FETCH p.quotation q
        WHERE p.id = :id
    """)
    Optional<PurchaseOrder> findByIdWithDetails(@Param("id") Long id);


}