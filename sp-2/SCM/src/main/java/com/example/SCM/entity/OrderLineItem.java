package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_line_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder customerOrder; // FK → CustomerOrder [cite: 1691]

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // FK → Product [cite: 1692]

    @Column(nullable = false)
    private int quantity; // [cite: 1693]

    @Column(name = "unit_price", nullable = false)
    private double unitPrice; // [cite: 1694]

    @Column(name = "line_total", nullable = false)
    private double lineTotal; // LineTotal = quantity * unitPrice [cite: 1695]

    // ── 🆕 নতুন যুক্ত করা ফিল্ডস ──────────────────────────────────
    @Column(name = "item_weight_total", nullable = false)
    private double itemWeightTotal; // ১টি আইটেমের মোট ওজন = (quantity * Product Weight)

    @Column(length = 255)
    private String remarks; // প্রোডাক্টের সাইজ, কালার বা বিশেষ কোনো নোটের জন্য

    @PrePersist
    @PreUpdate
    protected void preSaveCalculations() {
        // ১. ডাটাবেসে যাওয়ার আগে প্রতি আইটেমের কোয়ান্টিটি ও প্রাইস গুণ করে টোটাল বের করা [cite: 1696]
        this.lineTotal = this.quantity * this.unitPrice; // [cite: 1696]

        // ২. প্রোডাক্টের নিজস্ব ওজন থেকে এই আইটেম রো-এর মোট ওজন বের করা
        if (this.product != null) {
            this.itemWeightTotal = this.quantity * this.product.getWeight();
        }
    }
}