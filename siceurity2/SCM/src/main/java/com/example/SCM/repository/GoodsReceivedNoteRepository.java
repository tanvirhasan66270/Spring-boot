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


}