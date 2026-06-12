package com.example.SCM.repository;

import com.example.SCM.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // ১. ইমেইল দিয়ে সাপ্লায়ার খোঁজা (ইউনিক চেক বা লগইনের সোর্স ট্র্যাকিংয়ের জন্য)
    Optional<Supplier> findByEmail(String email);

    // ২. একটিভ (Active) সাপ্লায়ারদের লিস্ট বের করা (ডপডাউন মেনুর জন্য দরকারী)
    List<Supplier> findByIsActiveTrue();

    // ৩. কাস্টম JPQL কুয়েরি: নির্দিষ্ট রেটিং-এর চেয়ে সমান বা বেশি রেটিং ওয়ালা টপ সাপ্লায়ার খোঁজা
    @Query("SELECT s FROM Supplier s WHERE s.rating >= :minRating AND s.isActive = true ORDER BY s.rating DESC")
    List<Supplier> findTopRatedSuppliers(@Param("minRating") double minRating);

    // ৪. কাস্টম JPQL কুয়েরি: সবচেয়ে কম এভারেজ লিড টাইম (ডেলিভারি দিতে কম দিন সময় নেয়) সম্পন্ন সাপ্লায়ার খোঁজা
    @Query("SELECT s FROM Supplier s WHERE s.isActive = true ORDER BY s.averageLeadTimeDays Asc")
    List<Supplier> findFastestSuppliers();

    // ৫. কাস্টম জয়েন কুয়েরি: নির্দিষ্ট পুলিশ স্টেশন বা জেলা (District) অনুযায়ী সাপ্লায়ার ফিল্টার করা
    @Query("SELECT s FROM Supplier s WHERE s.policeStation.id = :psId")
    List<Supplier> findSuppliersByPoliceStation(@Param("psId") Long policeStationId);

    // ৬. কাস্টম Native SQL কুয়েরি: ড্যাশবোর্ডের জন্য টোটাল একটিভ সাপ্লায়ারের সংখ্যা গোনা
    @Query(value = "SELECT COUNT(*) FROM suppliers WHERE is_active = true", nativeQuery = true)
    long countAllActiveSuppliers();
}