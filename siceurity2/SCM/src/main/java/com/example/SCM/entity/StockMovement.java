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

     @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long warehouseId;


    private String sendWarehouse;

    @Enumerated(EnumType.STRING)
    private StockMovementType movementType;

    private int quantity;

    @Column(nullable = false)
    private String referenceId;

    @Column(nullable = false)
    private Long performedBy;

    @Column(nullable = false)
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