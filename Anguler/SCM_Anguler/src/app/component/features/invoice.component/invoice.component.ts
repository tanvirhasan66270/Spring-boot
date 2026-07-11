import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InvoiceRequestModel, InvoiceResponseModel } from '../../shared/model/invoiceModel';
import { InvoiceService } from '../../../service/invoice.service';
import { CustomerOrderService } from '../../../service/customer-order.service';


@Component({
  selector: 'app-invoice',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './invoice.component.html',
  styleUrl: './invoice.component.css'
})
export class InvoiceComponent implements OnInit {

  invoices: InvoiceResponseModel[] = [];
  orders: any[] = []; // অর্ডার ট্র্যাকিং লিংকের জন্য লিস্ট
  
  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;
  selectedInvoiceForView: InvoiceResponseModel | null = null;

  // ইনিশিয়াল ব্ল্যাঙ্ক রিকোয়েস্ট ডিটিও অবজেক্ট
  formModel: InvoiceRequestModel = {
    customerOrderId: null,
    salesOfficerId: null,
    subtotal: 0,
    taxRate: 0,
    discountAmount: 0,
    discountPercentage: 0,
    shippingFees: 0,
    paidAmount: 0,
    paymentMethod: 'CASH',
    transactionReference: '',
    invoiceStatus: 'DRAFT',
    deliveryDate: '',
    deliveryAddress: '',
    notes: '',
    cancelledReason: ''
  };

  constructor(
    private service: InvoiceService,
    private orderService: CustomerOrderService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadInvoices();
    this.loadCustomerOrders();
  }

  loadInvoices() {
    this.service.findAll().subscribe({
      next: (data) => { this.invoices = data || []; this.cdr.markForCheck(); },
      error: (err) => this.handleErrorLog(err)
    });
  }

  loadCustomerOrders() {
    // অর্ডার ভেরিফিকেশনের জন্য লিস্ট লোড করা
    this.orderService.findAll().subscribe({ next: (data) => this.orders = data || [] });
  }

  openDrawer() { this.resetForm(); this.isEdit = false; this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.resetForm(); this.cdr.markForCheck(); }

  submitInvoice() {
    this.errorMessage = null;

    if (!this.formModel.customerOrderId || +this.formModel.customerOrderId === 0) {
      this.errorMessage = "Validation Fault: Customer Order Linkage identifier is required.";
      this.cdr.markForCheck();
      return;
    }

    if (!this.formModel.deliveryAddress || this.formModel.deliveryAddress.trim() === '') {
      this.errorMessage = "Validation Fault: Structural Delivery Address map is required.";
      this.cdr.markForCheck();
      return;
    }

    const payload: InvoiceRequestModel = {
      customerOrderId: +this.formModel.customerOrderId,
      salesOfficerId: this.formModel.salesOfficerId ? +this.formModel.salesOfficerId : null,
      subtotal: +this.formModel.subtotal,
      taxRate: +this.formModel.taxRate,
      discountAmount: +this.formModel.discountAmount,
      discountPercentage: +this.formModel.discountPercentage,
      shippingFees: +this.formModel.shippingFees,
      paidAmount: +this.formModel.paidAmount,
      paymentMethod: this.formModel.paymentMethod || null,
      transactionReference: this.formModel.transactionReference?.trim() || null,
      invoiceStatus: this.formModel.invoiceStatus,
      deliveryDate: this.formModel.deliveryDate || null,
      deliveryAddress: this.formModel.deliveryAddress.trim(),
      notes: this.formModel.notes?.trim() || null,
      cancelledReason: this.formModel.invoiceStatus === 'CANCELLED' ? this.formModel.cancelledReason?.trim() : null
    };

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, payload).subscribe({
        next: () => { alert("Invoice record matrix updated successfully."); this.closeDrawer(); this.loadInvoices(); },
        error: (err) => this.handleErrorLog(err)
      });
    } else {
      this.service.create(payload).subscribe({
        next: () => { alert("New commercial invoice ledger localized inside the datastore."); this.closeDrawer(); this.loadInvoices(); },
        error: (err) => this.handleErrorLog(err)
      });
    }
  }

  edit(i: InvoiceResponseModel) {
    this.errorMessage = null;
    this.currentEditId = i.id;
    this.isEdit = true;
    this.formModel = {
      customerOrderId: i.customerOrderId,
      salesOfficerId: i.salesOfficerId,
      subtotal: i.subtotal,
      taxRate: i.taxRate,
      discountAmount: i.discountAmount,
      discountPercentage: i.discountPercentage,
      shippingFees: i.shippingFees,
      paidAmount: i.paidAmount,
      paymentMethod: i.paymentMethod,
      transactionReference: i.transactionReference,
      invoiceStatus: i.invoiceStatus,
      deliveryDate: i.deliveryDate,
      deliveryAddress: i.deliveryAddress,
      notes: i.notes,
      cancelledReason: i.cancelledReason
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  viewInvoiceDetail(inv: InvoiceResponseModel) {
    this.selectedInvoiceForView = inv;
    this.cdr.markForCheck();
  }

  closeViewModal() {
    this.selectedInvoiceForView = null;
    this.cdr.markForCheck();
  }

  deleteInvoice(id: number) {
    if (confirm("Are you sure you want to drop this invoice dataset node? This action is irreversible.")) {
      this.service.delete(id).subscribe({
        next: () => { alert("Invoice lifecycle terminated successfully."); this.loadInvoices(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  private handleErrorLog(err: any) {
    console.error("SCM Invoice Exception Stack:", err);
    this.errorMessage = err.error?.message || err.message || "400 Bad Request: Financial calculation exception.";
    this.cdr.markForCheck();
  }

  resetForm() {
    this.formModel = {
      customerOrderId: null,
      salesOfficerId: null,
      subtotal: 0,
      taxRate: 0,
      discountAmount: 0,
      discountPercentage: 0,
      shippingFees: 0,
      paidAmount: 0,
      paymentMethod: 'CASH',
      transactionReference: '',
      invoiceStatus: 'DRAFT',
      deliveryDate: '',
      deliveryAddress: '',
      notes: '',
      cancelledReason: ''
    };
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}
