package com.example.SCM.entity;

import com.example.SCM.Util.ExecuteCalculations;
import com.example.SCM.enumClass.CustomerOrderStatus;
import com.example.SCM.enumClass.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "item_subtotal", nullable = false)
    private double itemSubtotal;

    @Column(nullable = false)
    private double weight; // সব আইটেমের মোট ওজন (KG)

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 20)
    @Builder.Default
    private ServiceType serviceType = ServiceType.STANDARD;

    @Column(name = "cod_amount")
    @Builder.Default
    private double codAmount = 0.0;

    @Column(name = "delivery_charge", nullable = false)
    private double deliveryCharge;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "BDT";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CustomerOrderStatus status = CustomerOrderStatus.PENDING;

    @Column(name = "delivery_address", columnDefinition = "TEXT", nullable = false)
    private String deliveryAddress;

    @Column(name = "estimated_delivery")
    private LocalDate estimatedDelivery;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderLineItem> lineItems = new ArrayList<>();

    // ── 🎯 হাইবারনেট লাইফসাইকেল হুকস ──────────────────

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.orderNumber == null) {
            this.orderNumber = "ORD-" + System.currentTimeMillis();
        }
        executeCalculations();
    }

    @PreUpdate
    protected void onUpdate() {
        executeCalculations();
    }

    // ── 🛠️ মাস্টার ক্যালকুলেশন মেথড ──
    public void executeCalculations() {
        // ১. সব লাইন আইটেমের মোট সাবটোটাল বের করা
        this.itemSubtotal = ExecuteCalculations.calculateItemSubtotal(this.lineItems);

        // ২. সব লাইন আইটেমের মোট ওজন বের করা
        this.weight = ExecuteCalculations.calculateTotalOrderWeight(this.lineItems);

        // ৩. মোট ওজনের ওপর ভিত্তি করে ডেলিভারি চার্জ হিসাব করা
        this.deliveryCharge = ExecuteCalculations.calculateDeliveryCharge(this.weight, this.serviceType, this.codAmount);

        // ৪. গ্র্যান্ড টোটাল: সাবটোটাল + ডেলিভারি চার্জ
        this.totalAmount = this.itemSubtotal + this.deliveryCharge;
    }

    // অবজেক্ট রিলেশন বাইন্ডিংয়ের জন্য হেল্পার মেথড
    public void addLineItem(OrderLineItem item) {
        if (item != null) {
            lineItems.add(item);
            item.setCustomerOrder(this);
        }
    }
}