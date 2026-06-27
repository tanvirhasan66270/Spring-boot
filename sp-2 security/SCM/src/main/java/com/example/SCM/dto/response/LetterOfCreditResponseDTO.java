package com.example.SCM.dto.response;

import lombok.Data;

@Data
public class LetterOfCreditResponseDTO {
    private Long id;
    private String lcNumber;
    private Long purchaseOrderId;
    private String poNumber;
    private String issuingBank;
    private String shipmentIncoTerms;
    private String latestShipmentDate;
    private String portOfLoading;
    private String portOfDischarge;
    private int amendmentCount;
    private double amount;
    private Long supplierId;
    private String supplierName;
    private String supplierEmail;
    private String currency;
    private String expiryDate;
    private String lcStatus;
    private String documentVaultUrl;
    private String openedAt;
    private String createdAt;
    private String updatedAt;
}