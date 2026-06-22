package com.example.SCM.repository;

import com.example.SCM.entity.Invoice;
import com.example.SCM.enumClass.InvoiceStatus;
import com.example.SCM.enumClass.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // ১. ইনভয়েস নম্বর (ইউনিক ট্র্যাকিং কোড) দিয়ে ইনভয়েস খোঁজা (ড্যাশবোর্ড সার্চের জন্য)
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    // ২. একটি নির্দিষ্ট অর্ডারের (customerOrderId) অধীনে তৈরি হওয়া ইনভয়েস বের করা
    // 💡 জাভা ফিল্ডের নাম 'customerOrderId' থাকায় এই মেথডটি মেমোরি রিলেশন সঠিকভাবে হ্যান্ডেল করবে
    List<Invoice> findByCustomerOrderId(Long customerOrderId);

    // ৩. নির্দিষ্ট পেমেন্ট স্ট্যাটাস (যেমন: UNPAID, PARTIALLY_PAID) অনুযায়ী ফিল্টার করা
    List<Invoice> findByPaymentStatus(PaymentStatus paymentStatus);

    // ৪. নির্দিষ্ট ইনভয়েস লাইফসাইকেল স্ট্যাটাস (DRAFT, ISSUED, CANCELLED) অনুযায়ী ফিল্টার করা
    List<Invoice> findByInvoiceStatus(InvoiceStatus invoiceStatus);

    // ৫. কাস্টম JPQL কুয়েরি: বকেয়া বা ডিউ ইনভয়েসগুলোর লিস্ট বের করা (ফাইন্যান্সিয়াল অ্যালার্টের জন্য)
    @Query("SELECT i FROM Invoice i WHERE i.dueAmount > 0 AND i.invoiceStatus = com.example.SCM.enumClass.InvoiceStatus.ISSUED")
    List<Invoice> findPendingDueInvoices();

    // ৬. কাস্টম Native SQL কুয়েরি: ড্যাশবোর্ডের জন্য টোটাল রিসিভেবল বা বকেয়া টাকার পরিমাণ হিসাব করা
    // 💡 ডাটাবেজে আপনার কলামের নাম 'due_amount' এবং 'invoice_status' এর সাথে ১০০% সিঙ্ক করা হয়েছে
    @Query(value = "SELECT SUM(i.due_amount) FROM invoices i WHERE i.invoice_status = 'ISSUED'", nativeQuery = true)
    Double calculateTotalOutstandingRevenue();
}