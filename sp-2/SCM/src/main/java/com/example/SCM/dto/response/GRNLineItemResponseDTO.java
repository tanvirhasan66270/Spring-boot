package com.example.SCM.dto.response;

import lombok.Data;

@Data
public class GRNLineItemResponseDTO {
    private Long id;
    private int quantityOrdered;
    private int quantityReceived;


    private Long grnId;
    private String grnNumber;       // ট্র্যাকিং ডিসপ্লের জন্য

    private Long productId;
    private String productName;
}