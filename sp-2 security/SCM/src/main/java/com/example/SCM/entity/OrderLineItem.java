package com.example.SCM.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_line_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder customerOrder; // FK → CustomerOrder

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // FK → Product


    private int quantity;


    private double unitPrice;

    private double lineTotal;

    private double itemWeightTotal;


    private String remarks;

    @PrePersist
    @PreUpdate
    protected void preSaveCalculations() {
        // ১. ডাটাবেসে যাওয়ার আগে প্রতি আইটেমের কোয়ান্টিটি ও প্রাইস গুণ করে টোটাল বের করা
        this.lineTotal = this.quantity * this.unitPrice;

        // ২. প্রোডাক্টের নিজস্ব ওজন থেকে এই আইটেম রো-এর মোট ওজন বের করা
        if (this.product != null) {
            this.itemWeightTotal = this.quantity * this.product.getWeight();
        }
    }
}