package com.example.SCM.repository;

import com.example.SCM.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    // ১. সম্পূর্ণ অবজেক্ট গ্রাফ ওয়ান-শটে ফেচ করার জন্য ইগার কুয়েরি
    @Query("SELECT DISTINCT o FROM CustomerOrder o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.lineItems i LEFT JOIN FETCH i.product")
    List<CustomerOrder> findAllOrdersWithDetails();

    // ২. নির্দিষ্ট ID দিয়ে অর্ডারের সম্পূর্ণ ডেটা ফেচ জয়েন করা
    @Query("SELECT o FROM CustomerOrder o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.lineItems i LEFT JOIN FETCH i.product WHERE o.id = :id")
    Optional<CustomerOrder> findByIdWithDetails(@Param("id") Long id);

    // ৩. ইউনিক অর্ডার নম্বর দিয়ে ট্র্যাকিং ডেটা লোড করা
    @Query("SELECT o FROM CustomerOrder o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.lineItems i LEFT JOIN FETCH i.product WHERE o.orderNumber = :orderNumber")
    Optional<CustomerOrder> findByOrderNumberWithDetails(@Param("orderNumber") String orderNumber);
}