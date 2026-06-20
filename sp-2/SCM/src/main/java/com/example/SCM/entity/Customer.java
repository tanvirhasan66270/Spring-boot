package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String gender;
    private String email;

    @Column(name = "nid_number")
    private String nidNumber;

    private String phone;

    @Temporal(TemporalType.DATE)
    private Date dob;

    private String image;

    // Auth account — source of truth for name, phone, email, password, role
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Where the customer lives / prefers pickup
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "police_station_id")
    private PoliceStation policeStation;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { // 💡 মেথডের নাম কনভেনশন অনুযায়ী ফিক্সড করা হলো
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}