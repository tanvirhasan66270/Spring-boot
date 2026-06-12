package com.example.SCM.repository;

import com.example.SCM.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    // ১. ওয়ারহাউজের নাম দিয়ে ইউনিকলি সার্চ করা (ডুপ্লিকেট নাম এড়াতে)

    Optional<Warehouse> findByName(String name);

    // ২. শুধুমাত্র একটিভ (Active) ওয়ারহাউজগুলোর লিস্ট বের করা (চলতি লজিস্টিকস অপারেশনের জন্য)

    List<Warehouse> findByIsActiveTrue();

    // ৩. নির্দিষ্ট একজন ম্যানেজার (ManagerId) কোন কোন ওয়ারহাউজ হ্যান্ডেল করছেন তা বের করা

    List<Warehouse> findByManagerId(Long managerId);

    // ৪. নির্দিষ্ট পুলিশ স্টেশন (থানা) আইডি অনুযায়ী সব ওয়ারহাউজ ফিল্টার করা

    List<Warehouse> findByPoliceStationId(Long policeStationId);

    // ৫. কাস্টম JPQL কুয়েরি: কোনো নির্দিষ্ট জেলা (District Name) অনুযায়ী সমস্ত ওয়ারহাউজ খুঁজে বের করা
    // (আঞ্চলিক লজিস্টিকস এবং ডিস্ট্রিবিউশন প্ল্যানিংয়ের জন্য অত্যন্ত দরকারী)

    @Query("SELECT w FROM Warehouse w WHERE w.policeStation.district.name = :districtName")
    List<Warehouse> findByDistrictName(@Param("districtName") String districtName);

    // ৬. কাস্টম JPQL কুয়েরি: নির্দিষ্ট ধারণক্ষমতার (Capacity) চেয়ে বড় বা সমান ওয়ারহাউজগুলো ফিল্টার করা
    // (লার্জ শিপমেন্ট রিসিভ করার কোয়ারি হিসেবে ব্যবহৃত হয়)\

    List<Warehouse> findByCapacityGreaterThanEqual(double capacity);
}