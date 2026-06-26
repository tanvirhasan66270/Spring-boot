package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String contactPerson; // অটোমেটিক contact_person কলাম হবে

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    private String address;
    private String nidNumber;
    private String passportNumber;
    private String gender; // MALE / FEMALE / OTHER

    @Temporal(TemporalType.DATE)
    private String dob;

    private String image;

    // Builder.Default ব্যবহার করলে অবজেক্ট তৈরির সময় এগুলো ডিফল্ট মান পেয়ে যাবে, columnDefinition বাদ
    @Builder.Default
    private double rating = 0.0;

    private int averageLeadTimeDays;

    private boolean isActive = true; // Primitive boolean হওয়ায় @Column লাগবে না

    @Column(updatable = false) // তৈরির সময় লক থাকবে
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt; // updatable = false তুলে দেওয়া হলো যেন আপডেট কাজ করে

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now(); // প্রথমবার সেভ হওয়ার সময়ও যেন কারেন্ট টাইম পায়
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Auth account — source of truth for name, phone, email, password, role
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Where the supplier lives / prefers pickup
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "police_station_id")
    private PoliceStation policeStation;
}