package com.example.SCM.entity;

import com.example.SCM.enumClass.StockMovementType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    // 💡 TRANSFER মুভমেন্টের জন্য সোর্স ওয়্যারহাউজ ট্র্যাক করতে। এটি NULL ভ্যালু অ্যাকসেপ্ট করবে।
    @Column(name = "send_warehouse", nullable = true)
    private String sendWarehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private StockMovementType movementType;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "reference_id", nullable = false)
    private String referenceId;

    @Column(name = "performed_by", nullable = false)
    private Long performedBy; // Mapped to User (Logistics_Officer) ID node

    @Column(name = "moved_at", nullable = false)
    private LocalDateTime movedAt;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @PrePersist
    protected void onCreate() {
        if (this.movedAt == null) {
            this.movedAt = LocalDateTime.now();
        }
    }
}