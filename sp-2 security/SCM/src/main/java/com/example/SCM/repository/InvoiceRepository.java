package com.example.SCM.repository;

import com.example.SCM.entity.Invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


    List<Invoice> findByCustomerEmail(String customerEmail);

    // 5. Custom JPQL Query: Fetch pending due invoices
    @Query("SELECT i FROM Invoice i WHERE i.dueAmount > 0 AND i.invoiceStatus = com.example.SCM.enumClass.InvoiceStatus.ISSUED")
    List<Invoice> findPendingDueInvoices();

    // ৬. কাস্টম Native SQL কুয়েরি: ড্যাশবোর্ডের জন্য টোটাল রিসিভেবল বা বকেয়া টাকার পরিমাণ হিসাব করা
    // 💡 ডাটাবেজে আপনার কলামের নাম 'due_amount' এবং 'invoice_status' এর সাথে ১০০% সিঙ্ক করা হয়েছে
    @Query(value = "SELECT SUM(i.due_amount) FROM invoices i WHERE i.invoice_status = 'ISSUED'", nativeQuery = true)
    Double calculateTotalOutstandingRevenue();
}