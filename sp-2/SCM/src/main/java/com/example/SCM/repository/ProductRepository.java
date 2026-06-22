package com.example.SCM.repository;

import com.example.SCM.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 💡 নতুন যুক্ত করা হয়েছে: স্টক মুভমেন্টের অটো-ফিলআপের জন্য অপ্টিমাইজড নেম কুয়েরি
    @Query("SELECT p.name FROM Product p WHERE p.id = :id")
    Optional<String> findNameById(@Param("id") Long id);

    // ১. প্রোডাক্ট কোড দিয়ে ইউনিক প্রোডাক্ট খোঁজা
    Optional<Product> findByProductCode(String productCode);

    // ২. নির্দিষ্ট ক্যাটাগরি আইডি (CategoryId) অনুযায়ী সব প্রোডাক্ট ফিল্টার করা
    List<Product> findByCategoryId(Long categoryId);

    // ৩. শুধুমাত্র একটিভ (Active) প্রোডাক্টগুলোর লিস্ট বের করা
    List<Product> findByIsActiveTrue();

    // ৪. কাস্টম JPQL কুয়েরি: ইনভেন্টরি অ্যালার্ট বা রিঅর্ডার পয়েন্ট ক্রসিং প্রোডাক্ট খোঁজা
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.reorderPoint AND p.isActive = true")
    List<Product> findLowStockProducts();

    // ৫. কাস্টম JPQL কুয়েরি: প্রোডাক্টের নাম বা প্রোডাক্ট কোড দিয়ে ওয়াইল্ডকার্ড সার্চ
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    // ৬. কাস্টম Native SQL কুয়েরি: ড্যাশবোর্ডের জন্য টোটাল ইনভেন্টরি স্টক ভ্যালু বের করা
    @Query(value = "SELECT SUM(p.unit_cost * p.quantity) FROM products p WHERE p.is_active = true", nativeQuery = true)
    Double calculateTotalInventoryValue();
}