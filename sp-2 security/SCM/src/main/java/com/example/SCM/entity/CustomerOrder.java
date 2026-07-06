package com.example.SCM.entity;

import com.example.SCM.Util.ExecuteCalculations;
import com.example.SCM.enumClass.CustomerOrderStatus;
import com.example.SCM.enumClass.PaymentMethod;
import com.example.SCM.enumClass.PaymentStatus;
import com.example.SCM.enumClass.ServiceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(unique = true, nullable = false)
    private String orderNumber;

    private String customerName;
    private String customerEmail;

    private double itemSubtotal;
    private double weight;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ServiceType serviceType = ServiceType.STANDARD;

    @Column(nullable = false)
    private String currency;

    private double codAmount = 0.0;
    private double deliveryCharge;
    private double totalAmount;

    @Column(nullable = false)
    private String paidAmount;

    private String dueAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CustomerOrderStatus status = CustomerOrderStatus.PENDING;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private String deliveryPhone;

    private LocalDate estimatedDelivery;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // ── Zone management / Object Relations ───────────────────────
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderLineItem> lineItems = new ArrayList<>();

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

    private void syncCustomerMetadata() {
        if (this.customer != null) {
            this.customerName = this.customer.getName();
            this.customerEmail = this.customer.getEmail();
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

        // পেমেন্ট কন্ডিশন ও বিজনেস রুলস মেকানিজম
        double paid = 0.0;
        try {
            paid = Double.parseDouble(this.paidAmount);
        } catch (Exception e) {
            paid = 0.0;
        }

        this.dueAmount = String.valueOf(this.totalAmount - paid);

        // রুল ১: যদি পেমেন্ট মেথড CASH (ক্যাশ অন ডেলিভারি) হয়, তবে স্ট্যাটাস সবসময় UNPAID থাকবে
        if (this.paymentMethod == PaymentMethod.CASH) {
            this.paymentStatus = PaymentStatus.UNPAID;
        }
        // রুল ২: যদি paidAmount গ্র্যান্ড টোটালের সমান বা বেশি হয় (paid == totalAmount)
        else if (paid >= this.totalAmount && this.totalAmount > 0) {
            this.paymentStatus = PaymentStatus.PAID;
        }
        // রুল ৩: যদি paidAmount মোট টাকার থেকে কম অথচ ০ এর থেকে বেশি হয় (আংশিক পরিশোধ)
        else if (paid > 0 && paid < this.totalAmount) {
            this.paymentStatus = PaymentStatus.PARTIALLY_PAID;
        }
        // ডিফল্ট ব্যাকআপ রুট
        else {
            this.paymentStatus = PaymentStatus.UNPAID;
        }
    }

    public void addLineItem(OrderLineItem item) {
        if (item != null) {
            lineItems.add(item);
            item.setCustomerOrder(this);
        }
    }

    public Long getCustomerId() {
        return this.customer != null ? this.customer.getId() : null;
    }
}