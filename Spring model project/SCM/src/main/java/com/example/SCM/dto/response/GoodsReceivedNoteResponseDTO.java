package com.example.SCM.dto.response;

import com.example.SCM.enumClass.GRNStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GoodsReceivedNoteResponseDTO {
    private Long id;
    private String grnNumber;
    private Integer quantity;
    private int receivedQuantity;
    private LocalDate receivedAt;
    private GRNStatus status;
    private String remarks;
    private LocalDate inspectionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private Long poId;
    private String poNumber;         // UI স্ক্রিনে দেখানোর জন্য পারচেজ অর্ডার নাম্বার

    private Long productId;
    private String productName;      // প্রোডাক্টের নাম

    private Long warehouseId;
    private String warehouseName;    // ওয়ারহাউজের নাম

    private Long receivedBy;
    private String receivedByName;   // রিসিভকারী ইউজারের নাম

    private Long inspectedBy;
    private String inspectedByName;  // ইন্সপেকশনকারী ইনস্পেক্টরের নাম
}