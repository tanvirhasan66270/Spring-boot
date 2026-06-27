package com.example.SCM.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column( unique = true)
    private String productCode;

    @Column(nullable = false)
    private String name;

    private String unit;

    private int reorderPoint;

    private double unitCost;

    private int quantity;

    private double sellingPrice;

    private String hasExpiryDate;

    //  ওজনের নতুন ফিল্ড যা থেকে কাস্টমার অর্ডারের ডেলিভারি চার্জ ক্যালকুলেট হবে ──
    @Column(name = "weight", nullable = false)
    private double weight; // কেজিতে হিসাব হবে (যেমন: 0.2 মানে 200 গ্রাম, 1.5 মানে 1.5 KG)

    @Column( nullable = false)
    private boolean isActive = true;

    private String availability;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image; // Base64

    // Relationship mapping with category
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}