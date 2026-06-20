package com.example.SCM.entity;

import com.example.SCM.Util.ExecuteCalculations;
import com.example.SCM.enumClass.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber; // ট্র্যাকিং কোড জেনারেটর

    // ── 👥 রিলেশনশিপ ম্যাপিং (Foreign Keys) ──────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer; // FK → User (Customer)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // FK → Product

    // ── 💰 ফিন্যান্সিয়াল এবং ক্যালকুলেশন ফিল্ডস ──────────────────────────────────

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "line_total", nullable = false)
    private double lineTotal; // LineTotal = quantity * unitPrice

    @Column(nullable = false)
    private double weight; // পার্সেলের ওজন (KG)

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 20)
    private ServiceType serviceType = ServiceType.STANDARD;

    @Column(name = "cod_amount")
    private double codAmount = 0.0; // Cash on Delivery কালেকশন অ্যামাউন্ট

    @Column(name = "delivery_charge", nullable = false)
    private double deliveryCharge; // ইউটিলিটি ক্লাস থেকে অটো ক্যালকুলেট হবে

    @Column(name = "total_amount", nullable = false)
    private double totalAmount; // totalAmount = lineTotal + deliveryCharge

    @Column(nullable = false, length = 10)
    private String currency = "USD"; // Default 'USD'

    // ── 🚛 লজিস্টিকস এবং স্ট্যাটাস ──────────────────────────────────────────

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, CONFIRMED, SHIPPED, DELIVERED

    @Column(name = "delivery_address", columnDefinition = "TEXT", nullable = false)
    private String deliveryAddress;

    @Column(name = "estimated_delivery")
    private LocalDate estimatedDelivery;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // 💡 লাইফসাইকেল হুক: ডাটাবেসে প্রথমবার সেভ হওয়ার আগে অটো-ক্যালকুলেশন মেকানিজম
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.orderNumber == null) {
            this.orderNumber = "ORD-" + System.currentTimeMillis(); // ইউনিক ট্র্যাকিং আইডি জেনারেটর
        }
        executeCalculations();
    }

    // 💡 লাইফসাইকেল হুক: ডাটাবেসে কোনো ডাটা এডিট বা আপডেট হওয়ার আগে অটো-ক্যালকুলেশন মেকানিজম
    @PreUpdate
    protected void onUpdate() {
        executeCalculations();
    }

    // ── ইউটিলিটি ক্লাস ব্যবহার করে গাণিতিক হিসাব সম্পন্ন করার প্রাইভেট মেথড ──
    private void executeCalculations() {
        // ১. পণ্যের মোট দাম হিসাব (Line Total)
        this.lineTotal = ExecuteCalculations.calculateLineTotal(this.quantity, this.unitPrice);

        // ২. ইউটিলিটি ক্লাস থেকে ডেলিভারি চার্জ হিসাব
        this.deliveryCharge = ExecuteCalculations.calculateDeliveryCharge(this.weight, this.serviceType, this.codAmount);

        // ৩. সর্বমোট বিল বের করা: lineTotal + deliveryCharge
        this.totalAmount = this.lineTotal + this.deliveryCharge;
    }
}