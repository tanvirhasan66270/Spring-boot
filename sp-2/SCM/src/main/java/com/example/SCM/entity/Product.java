package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", unique = true)
    private String productCode;

    @Column(nullable = false)
    private String name;

    private String unit;

    @Column(name = "reorder_point")
    private int reorderPoint;

    @Column(name = "unit_cost")
    private double unitCost;

    private int quantity;

    @Column(name = "selling_price")
    private double sellingPrice;

    @Column(name = "has_expiry_date")
    private String hasExpiryDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    private String availability;


    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image; // Base64 লার্জ স্ট্রিং ডাটা সেভ করার জন্য LONGTEXT ব্যবহার করা হয়েছে

    // ক্যাটাগরির সাথে রিলেশনশিপ ম্যাপিং
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}