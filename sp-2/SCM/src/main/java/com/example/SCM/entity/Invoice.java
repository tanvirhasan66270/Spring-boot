package com.example.SCM.entity;

import com.example.SCM.enumClass.InvoiceStatus;
import com.example.SCM.enumClass.PaymentMethod;
import com.example.SCM.enumClass.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    // 💡 ডাটাবেজ কলাম নেম পরিবর্তন করে 'order_id' এর সাথে ম্যাচ করানো হলো
    @Column(name = "order_id", nullable = false)
    private Long customerOrderId;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "sales_officer_id")
    private Long salesOfficerId;

    @Column(name = "issued_to_name", nullable = false)
    private String issuedToName;

    @Column(nullable = false, length = 10)
    private String currency = "BDT";

    // ── Financial Calculation Fields ──
    @Column(nullable = false)
    private double subtotal;

    @Column(name = "tax_rate", nullable = false)
    private double taxRate = 0.0;

    @Column(name = "tax_amount", nullable = false)
    private double taxAmount;

    @Column(name = "discount_amount")
    private double discountAmount = 0.0;

    @Column(name = "discount_percentage")
    private double discountPercentage = 0.0;

    @Column(name = "shipping_fees")
    private double shippingFees = 0.0;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "paid_amount", nullable = false)
    private double paidAmount = 0.0;

    @Column(name = "due_amount", nullable = false)
    private double dueAmount;

    // ── Status & Payment Details ──
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_status", nullable = false, length = 20)
    private InvoiceStatus invoiceStatus = InvoiceStatus.DRAFT;

    // ── Logistics Fields ──
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "delivery_address", columnDefinition = "TEXT", nullable = false)
    private String deliveryAddress;

    // ── Audit Logs & Notes ──
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "cancelled_reason", columnDefinition = "TEXT")
    private String cancelledReason;

    @Column(name = "issued_at")
    private LocalDate issuedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        syncStatusTimestamps();
        calculateFinancials();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        syncStatusTimestamps();
        calculateFinancials();
    }

    private void syncStatusTimestamps() {
        if (this.invoiceStatus == InvoiceStatus.ISSUED && this.issuedAt == null) {
            this.issuedAt = LocalDate.now();
        }
        if (this.invoiceStatus == InvoiceStatus.CANCELLED && this.cancelledAt == null) {
            this.cancelledAt = LocalDateTime.now();
        }
    }

    private void calculateFinancials() {
        if (this.discountPercentage > 0 && this.discountAmount == 0) {
            this.discountAmount = this.subtotal * (this.discountPercentage / 100);
        }
        this.taxAmount = this.subtotal * this.taxRate;
        this.totalAmount = this.subtotal + this.taxAmount + this.shippingFees - this.discountAmount;
        this.dueAmount = this.totalAmount - this.paidAmount;

        if (this.paidAmount <= 0) {
            this.paymentStatus = PaymentStatus.UNPAID;
        } else if (this.paidAmount < this.totalAmount) {
            this.paymentStatus = PaymentStatus.PARTIALLY_PAID;
        } else {
            this.paymentStatus = PaymentStatus.PAID;
        }
    }
}