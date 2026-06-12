package com.example.SCM.repository;

import com.example.SCM.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category , Long> {

    // ১. ক্যাটাগরির নাম দিয়ে হুবহু সার্চ করা (ইউনিক চেক বা ভ্যালিডেশনের জন্য)
    Optional<Category> findByCategoryName(String categoryName);

    // ২. ক্যাটাগরির নামের ভেতর কোনো নির্দিষ্ট শব্দ বা অক্ষর থাকলে তা দিয়ে সার্চ করা (Like Query)
    // উদাহরণ: "Garments" দিয়ে সার্চ করলে "Garments Accessories", "Garments Tools" সব চলে আসবে
    List<Category> findByCategoryNameContainingIgnoreCase(String keyword);

    // ৩. কাস্টম JPQL কুয়েরি: যেসব ক্যাটাগরির অধীনে অন্তত একটি বা তার বেশি প্রোডাক্ট আছে সেগুলো খুঁজে বের করা
    // (ড্যাশবোর্ড ফিল্টারিং বা মেনু রেন্ডারিংয়ের জন্য অত্যন্ত দরকারী)
    @Query("SELECT c FROM Category c WHERE size(c.products) > 0")
    List<Category> findCategoriesWithProducts();

    // 8. কাস্টম Native SQL কুয়েরি: কোনো নির্দিষ্ট ক্যাটাগরির আন্ডারে টোটাল কত পিস প্রোডাক্টের স্টক (Quantity) আছে তা গণনা করা
    @Query(value = "select * from (SELECT SUM(p.quantity) FROM products p WHERE p.category_id = :categoryId) pS", nativeQuery = true)
    Long getTotalProductQuantityByCategoryId(@Param("categoryId") Long categoryId);
}
