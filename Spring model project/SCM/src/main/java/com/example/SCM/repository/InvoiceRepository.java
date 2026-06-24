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



    // ৫. কাস্টম JPQL কুয়েরি: বকেয়া বা ডিউ ইনভয়েসগুলোর লিস্ট বের করা (ফাইন্যান্সিয়াল অ্যালার্টের জন্য)
    @Query("SELECT i FROM Invoice i WHERE i.dueAmount > 0 AND i.invoiceStatus = com.example.SCM.enumClass.InvoiceStatus.ISSUED")
    List<Invoice> findPendingDueInvoices();

    // ৬. কাস্টম Native SQL কুয়েরি: ড্যাশবোর্ডের জন্য টোটাল রিসিভেবল বা বকেয়া টাকার পরিমাণ হিসাব করা
    // 💡 ডাটাবেজে আপনার কলামের নাম 'due_amount' এবং 'invoice_status' এর সাথে ১০০% সিঙ্ক করা হয়েছে
    @Query(value = "SELECT SUM(i.due_amount) FROM invoices i WHERE i.invoice_status = 'ISSUED'", nativeQuery = true)
    Double calculateTotalOutstandingRevenue();
}