import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PurchaseOrderRequestModel, PurchaseOrderResponseModel } from '../../shared/model/purchaseOrderModel';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { QuotationService } from '../../../service/quatation.service';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-purchase-order',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase-order.component.html',
  styleUrl: './purchase-order.component.css',
})
export class PurchaseOrderComponent implements OnInit {

  orders: PurchaseOrderResponseModel[] = [];
  quotations: any[] = [];
  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;

  order: PurchaseOrderRequestModel = {
    quotationId: 0,
    issuedBy: 0,
    totalAmount: 0,
    currency: 'USD',
    expectedDeliveryDate: '',
    status: 'DRAFT'
  };

  constructor(
    private service: PurchaseOrderService,
    private quotationService: QuotationService,
    private storage: StorageService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    const currentUser = this.storage.getUser();
    if (currentUser) {
      this.order.issuedBy = currentUser.userId;
    }
    this.loadOrders();
    this.loadQuotations();
  }

  loadOrders() {
    this.service.findAll().subscribe({
      next: (data) => { this.orders = data || []; this.cdr.markForCheck(); }
    });
  }

  loadQuotations() {
    // শুধুমাত্র APPROVED কোটেশনগুলো ফিল্টার করে আনা যাতে সেগুলোর বিপরীতে PO ইস্যু 
    this.quotationService.findAll().subscribe({
      next: (data) => {
        this.quotations = data ? data.filter((q: any) => q.status === 'APPROVED') : [];
        this.cdr.markForCheck();
      }
    });
  }

  onQuotationChange(event: any) {
    const qId = +event.target.value;
    const selectedQ = this.quotations.find(q => q.id === qId);
    if (selectedQ) {
      this.order.totalAmount = selectedQ.totalPrice;
    }
  }

  openDrawer() { this.reset(); this.isEdit = false; this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.reset(); this.cdr.markForCheck(); }

  save() {
    this.errorMessage = null;

    if (this.order.quotationId === 0) {
      this.errorMessage = 'Validation Fault: Mapping an active Approved Quotation node is required.';
      return;
    }

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.order).subscribe({
        next: () => { alert("Purchase Order updated successfully."); this.closeDrawer(); this.loadOrders(); },
        error: (err) => this.errorMessage = err.error?.message || "Modification matrix locked."
      });
    } else {
      this.service.save(this.order).subscribe({
        next: () => { alert("New Purchase Order created as DRAFT. Approval mail dispatched to manager."); this.closeDrawer(); this.loadOrders(); },
        error: (err) => this.errorMessage = err.error?.message || "Deployment pipeline fault."
      });
    }
  }

  edit(o: PurchaseOrderResponseModel) {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;
    this.order = {
      quotationId: o.quotationId,
      issuedBy: o.issuedBy,
      totalAmount: o.totalAmount,
      currency: o.currency || 'USD',
      expectedDeliveryDate: o.expectedDeliveryDate,
      status: o.status
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Definitively purge this Purchase Order record from active directories?")) {
      this.service.delete(id).subscribe({
        next: () => { alert("PO instance wiped successfully."); this.loadOrders(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  reset() {
    const currentUser = this.storage.getUser();
    this.order = {
      quotationId: 0,
      issuedBy: currentUser?.userId || 0,
      totalAmount: 0,
      currency: 'USD',
      expectedDeliveryDate: '',
      status: 'DRAFT'
    };
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}
