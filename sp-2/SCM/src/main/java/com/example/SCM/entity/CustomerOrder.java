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

    // 💡 Lazy Fetch এবং সঠিক ফরেন কি ম্যাপিং যা মেইন ইউজার টেবিলকে পয়েন্ট করে
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // ── 🎯 ডাইনামিক ক্যাশ ফিল্ডস (ইমেইল লিক ও পুরনো ক্যাশ জ্যাম প্রতিরোধ গেটওয়ে) ──
    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Column(name = "customer_email", length = 100)
    private String customerEmail;

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

    // 🎯 কনসিস্টেন্সির জন্য ডাটা টাইপ String এবং লেন্থ ৩০ রাখা হলো
    @Column(name = "paid_amount", nullable = false, length = 30)
    private String paidAmount;

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

    // 🔗 আপনার টপ-লেভেল OrderLineItem ক্লাসের সাথে Bidirectional One-to-Many রিলেশন
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
        syncCustomerMetadata();
        executeCalculations();
    }

    @PreUpdate
    protected void onUpdate() {
        syncCustomerMetadata();
        executeCalculations();
    }

    // ── 🛠️ ইন্টারনাল ইউটিলিটি মেথডস ──

    private void syncCustomerMetadata() {
        if (this.customer != null) {
            this.customerName = this.customer.getName();
            this.customerEmail = this.customer.getEmail(); // 🔥 রিয়েল-টাইম মেইল সিঙ্ক
        }
    }

    public void executeCalculations() {
        // ১. সব লাইন আইটেমের মোট সাবটোটাল বের করা
        this.itemSubtotal = ExecuteCalculations.calculateItemSubtotal(this.lineItems);

        // ২. সব লাইন আইটেমের মোট ওজন বের করা
        this.weight = ExecuteCalculations.calculateTotalOrderWeight(this.lineItems);

        // ৩. মোট ওজনের ওপর ভিত্তি করে ডেলিভারি চার্জ হিসাব করা
        this.deliveryCharge = ExecuteCalculations.calculateDeliveryCharge(this.weight, this.serviceType, this.codAmount);

        // ৪. গ্র্যান্ড টোটাল: সাবটোটাল + ডেলিভারি চার্জ
        this.totalAmount = this.itemSubtotal + this.deliveryCharge;

        // ৫. ইউটিলিটি মেথড ব্যবহার করে paidAmount স্ট্রিং সেট করা
        this.paidAmount = ExecuteCalculations.calculatePaidAmount(this.totalAmount, this.codAmount);
    }

    // অবজেক্ট রিলেশন বাইন্ডিংয়ের জন্য হেল্পার মেথড
    public void addLineItem(OrderLineItem item) {
        if (item != null) {
            lineItems.add(item);
            item.setCustomerOrder(this);
        }
    }

    // 💡 ম্যাপার ও কন্ট্রোলারের টাইপ ট্র্যাপ সেফটি গেটওয়ে
    public Long getCustomerId() {
        return this.customer != null ? this.customer.getId() : null;
    }
}