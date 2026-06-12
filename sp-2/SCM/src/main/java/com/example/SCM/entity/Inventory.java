package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "warehouse_id"})})
// একই ওয়ারহাউজে একই প্রোডাক্টের দুটি আলাদা রো যেন না তৈরি হয় (ডুপ্লিকেট স্টক এড়াতে)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // প্রোডাক্টের সাথে মেনি-টু-ওয়ান রিলেশন
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

       // ওয়ারহাউজের সাথে মেনি-টু-ওয়ান রিলেশন
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "quantity_on_hand", nullable = false)
    private int quantityOnHand;

    @Column(name = "quantity_reserved", nullable = false)
    private int quantityReserved;

    @Column(name = "location_status")
    private String locationStatus; // যেমন: "Rack-A, Row-3"

    @Column(name = "expiry_date")
    private LocalDate expiryDate; // মেয়াদের ডেট হ্যান্ডেল করার জন্য LocalDate ব্যবহার করা বেস্ট

    @Column(name = "stock_status", nullable = false)
    private String stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        this.lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}