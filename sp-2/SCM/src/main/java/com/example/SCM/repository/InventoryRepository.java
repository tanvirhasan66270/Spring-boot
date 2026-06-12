package com.example.SCM.repository;

import com.example.SCM.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // ১. নির্দিষ্ট প্রোডাক্ট এবং ওয়ারহাউজ আইডি দিয়ে ইউনিক স্টক রো খুঁজে বের করা
      Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    // ২. একটি নির্দিষ্ট ওয়ারহাউজে (WarehouseId) কী কী প্রোডাক্ট আছে তার লিস্ট বের করা
    List<Inventory> findByWarehouseId(Long warehouseId);

    // ৩. একটি নির্দিষ্ট প্রোডাক্ট (ProductId) কোন কোন গুদামে বা ওয়ারহাউজে ছড়িয়ে আছে তা বের করা
    List<Inventory> findByProductId(Long productId);

    // ৪. স্টকের অবস্থা অনুযায়ী ফিল্টার করা (যেমন: কোন কোন প্রোডাক্ট OUT_OF_STOCK বা LOW_STOCK অবস্থায় আছে)
    List<Inventory> findByStockStatus(String stockStatus);

    // ৫. কাস্টম JPQL কুয়েরি: নির্দিষ্ট কোনো জেলা (District Name) বা অঞ্চলের সমস্ত ওয়ারহাউজের টোটাল স্টক ফিল্টার করা
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.policeStation.district.name = :districtName")
    List<Inventory> findByWarehouseDistrictName(@Param("districtName") String districtName);

    // ৬. কাস্টম JPQL কুয়েরি: যেসব প্রোডাক্টের মেয়াদ (Expiry Date) শেষ বা অতিক্রান্ত হতে চলেছে (Expired Stock Tracking)
    // এটি কোয়ালিটি কন্ট্রোল এবং ব্যাচ খালাসের জন্য অত্যন্ত দরকারি
    @Query("SELECT i FROM Inventory i WHERE i.expiryDate IS NOT NULL AND i.expiryDate <= CURRENT_DATE")
    List<Inventory> findExpiredInventories();
}