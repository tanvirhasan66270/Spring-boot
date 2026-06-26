package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "warehouse_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // ফিউচারে সার্ভিস লেয়ারে সহজে অবজেক্ট ক্রিয়েট করার জন্য যুক্ত করা হলো
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // প্রোডাক্টের সাথে মেনি-টু-ওয়ান রিলেশন
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // ওয়ারহাউজের সাথে মেনি-টু-ওয়ান রিলেশন
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    private int quantityOnHand;

    private int quantityReserved;

    private String locationStatus; // যেমন: "Rack-A, Row-3"

    private LocalDate expiryDate;

    @Column(nullable = false)
    private String stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK

    @Column(nullable = false)
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