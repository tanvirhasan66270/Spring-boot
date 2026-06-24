package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.InvoiceRequestDTO;
import com.example.SCM.dto.response.InvoiceResponseDTO;
import com.example.SCM.entity.Invoice;
import com.example.SCM.enumClass.InvoiceStatus;
import com.example.SCM.enumClass.PaymentMethod;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class InvoiceMapper {

    public Invoice toEntity(InvoiceRequestDTO dto) {
        if (dto == null) return null;

        Invoice invoice = new Invoice();
        invoice.setCustomerOrderId(dto.getCustomerOrderId());
        invoice.setSalesOfficerId(dto.getSalesOfficerId());
        invoice.setSubtotal(dto.getSubtotal());
        invoice.setTaxRate(dto.getTaxRate());
        invoice.setDiscountAmount(dto.getDiscountAmount());
        invoice.setDiscountPercentage(dto.getDiscountPercentage());
        invoice.setShippingFees(dto.getShippingFees());
        invoice.setPaidAmount(dto.getPaidAmount());
        invoice.setTransactionReference(dto.getTransactionReference());
        invoice.setDeliveryAddress(dto.getDeliveryAddress());
        invoice.setNotes(dto.getNotes());
        invoice.setCancelledReason(dto.getCancelledReason());

        if (dto.getDeliveryDate() != null && !dto.getDeliveryDate().isBlank()) {
            invoice.setDeliveryDate(LocalDate.parse(dto.getDeliveryDate()));
        }
        if (dto.getPaymentMethod() != null && !dto.getPaymentMethod().isBlank()) {
            invoice.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase()));
        }
        if (dto.getInvoiceStatus() != null && !dto.getInvoiceStatus().isBlank()) {
            invoice.setInvoiceStatus(InvoiceStatus.valueOf(dto.getInvoiceStatus().toUpperCase()));
        }

        return invoice;
    }

    public InvoiceResponseDTO toResponseDTO(Invoice entity) {
        if (entity == null) return null;

        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(entity.getId());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setCustomerOrderId(entity.getCustomerOrderId());
        dto.setCustomerEmail(entity.getCustomerEmail()); // 💡 ফ্রন্টএন্ডের অটো ফিলাপ ট্র্যাক রাখার জন্য সিঙ্কড
        dto.setSalesOfficerId(entity.getSalesOfficerId());
        dto.setIssuedToName(entity.getIssuedToName());
        dto.setCurrency(entity.getCurrency());
        dto.setSubtotal(entity.getSubtotal());
        dto.setTaxRate(entity.getTaxRate());
        dto.setTaxAmount(entity.getTaxAmount());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setDiscountPercentage(entity.getDiscountPercentage());
        dto.setShippingFees(entity.getShippingFees());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setDueAmount(entity.getDueAmount());
        dto.setPaymentStatus(entity.getPaymentStatus().name());
        dto.setInvoiceStatus(entity.getInvoiceStatus().name());
        dto.setDeliveryDate(entity.getDeliveryDate());
        dto.setDeliveryAddress(entity.getDeliveryAddress());
        dto.setNotes(entity.getNotes());
        dto.setCancelledReason(entity.getCancelledReason());
        dto.setIssuedAt(entity.getIssuedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCancelledAt(entity.getCancelledAt());

        if (entity.getPaymentMethod() != null) {
            dto.setPaymentMethod(entity.getPaymentMethod().name());
        }
        return dto;
    }

    //  এক্সিস্টিং ইনভয়েস আপডেট করার জন্য ইউটিলিটি ম্যাপার মেথড
    public void updateEntityFromDTO(InvoiceRequestDTO dto, Invoice invoice) {
        if (dto == null || invoice == null) return;

        invoice.setCustomerOrderId(dto.getCustomerOrderId());
        invoice.setSalesOfficerId(dto.getSalesOfficerId());
        invoice.setSubtotal(dto.getSubtotal());
        invoice.setTaxRate(dto.getTaxRate());
        invoice.setDiscountAmount(dto.getDiscountAmount());
        invoice.setDiscountPercentage(dto.getDiscountPercentage());
        invoice.setShippingFees(dto.getShippingFees());
        invoice.setPaidAmount(dto.getPaidAmount());
        invoice.setTransactionReference(dto.getTransactionReference());
        invoice.setDeliveryAddress(dto.getDeliveryAddress());
        invoice.setNotes(dto.getNotes());
        invoice.setCancelledReason(dto.getCancelledReason());

        if (dto.getDeliveryDate() != null && !dto.getDeliveryDate().isBlank()) {
            invoice.setDeliveryDate(LocalDate.parse(dto.getDeliveryDate()));
        }
        if (dto.getPaymentMethod() != null && !dto.getPaymentMethod().isBlank()) {
            invoice.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase()));
        }
        if (dto.getInvoiceStatus() != null && !dto.getInvoiceStatus().isBlank()) {
            invoice.setInvoiceStatus(InvoiceStatus.valueOf(dto.getInvoiceStatus().toUpperCase()));
        }
    }
}