package com.example.SCM.entity;

import com.example.SCM.enumClass.GenderStatus;
import com.example.SCM.enumClass.LanguageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "qc_inspectors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QCInspector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Auth account — Source of truth for name, phone, email, password, role
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "nid_number", nullable = false, unique = true, length = 50)
    private String nidNumber;

    @Column(name = "passport_number", unique = true, length = 50)
    private String passportNumber;

    @Column(nullable = false)
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GenderStatus gender;

    @Column(name = "image_url")
    private String image;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private LanguageStatus language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "police_station_id")
    private PoliceStation policeStation;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}