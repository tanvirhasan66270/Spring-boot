package com.example.SCM.entity;


import com.example.SCM.enumClass.VehicleStatus;
import com.example.SCM.enumClass.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plate_number", nullable = false, unique = true, length = 50)
    private String plateNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType type;

    @Column(nullable = false)
    private Double capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Column(name = "last_service_date")
    private LocalDate lastServiceDate;

    @Column(name = "fuel_level", nullable = false)
    private Integer fuelLevel; // 0 - 100

    // One-to-One or Many-to-One with Driver (FK -> driver_id)
    // ড্রাইভার অ্যাসাইন না থাকলে এটি নাল (null) হতে পারে
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver;
}