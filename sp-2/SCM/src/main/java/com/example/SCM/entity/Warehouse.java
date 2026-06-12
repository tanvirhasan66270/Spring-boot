package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "warehouses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // TypeScript-এর string id-কে রিলেশনাল ডাটাবেজের স্ট্যান্ডার্ড অনুযায়ী Long (IDENTITY) করা হয়েছে

    @Column(nullable = false, unique = true, length = 150)
    private String name; // ওয়ারহাউজের নাম অবশ্যই ইউনিক এবং নট-নাল হওয়া উচিত

    @Column(nullable = false, columnDefinition = "TEXT")
    private String location; // বিস্তারিত ঠিকানা বা কোঅর্ডিনেটস সেভ করার জন্য TEXT বা বড় সাইজ ডিফাইন করা ভালো

    @Column(nullable = false)
    private double capacity; // ইনভেন্টরি ভলিউম বা স্কয়ার ফিট ট্র্যাকিংয়ের জন্য double বা int ব্যবহার করা যায়

    @Column(name = "manager_id")
    private Long managerId; // যদি আলাদা User বা Employee এনটিটি থাকে, তবে পরবর্তীতে এটিকে @ManyToOne-এ রূপান্তর করা যাবে

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // নতুন ওয়ারহাউজ বাই-ডিফল্ট একটিভ ট্র্যাকিং মোডে থাকবে

    // এন্টারপ্রাইজ সিস্টেমের অডিটিংয়ের জন্য অতিরিক্ত দুটি গুরুত্বপূর্ণ টাইমস্ট্যাম্প কলাম (Optional but Recommended)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * ডাটাবেজে রো ইনসার্ট বা আপডেট হওয়ার আগে টাইমস্ট্যাম্প অটো সেট করার জন্য লিসেনার মেথড
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    // Where the customer lives / prefers pickup
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "police_station_id")
    private PoliceStation policeStation;
}