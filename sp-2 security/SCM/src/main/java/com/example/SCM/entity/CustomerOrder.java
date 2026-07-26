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
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
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

        //  ৫. পরিশোধিত টাকা (Paid Amount) হিসেবে codAmount কে সেট করা হলো
        this.paidAmount = String.valueOf(this.codAmount);

        double paid = 0.0;
        try {
            paid = Double.parseDouble(this.paidAmount);
        } catch (Exception e) {
            paid = 0.0;
        }

        //  ৬. সঠিক ডিউ (Due Amount): মোট টাকা থেকে পরিশোধিত টাকা বিয়োগ করা
        double due = this.totalAmount - paid;
        this.dueAmount = String.valueOf(due < 0 ? 0.0 : due);

        // পেমেন্ট কন্ডিশন ও বিজনেস রুলস মেকানিজম
        if (this.paymentMethod == PaymentMethod.CASH) {
            this.paymentStatus = PaymentStatus.UNPAID;
        } else if (paid >= this.totalAmount && this.totalAmount > 0) {
            this.paymentStatus = PaymentStatus.PAID;
        } else if (paid > 0 && paid < this.totalAmount) {
            this.paymentStatus = PaymentStatus.PARTIALLY_PAID;
        } else {
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