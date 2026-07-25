import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PurchaseOrderRequestModel, PurchaseOrderResponseModel } from '../../shared/model/purchaseOrderModel';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { QuotationService } from '../../../service/quatation.service';
import { StorageService, KEYS } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-purchase-order',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase-order.component.html',
  styleUrl: './purchase-order.component.css',
})
export class PurchaseOrderComponent implements OnInit {

  orders: PurchaseOrderResponseModel[] = [];
  filteredOrders: PurchaseOrderResponseModel[] = [];
  quotations: any[] = [];
  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;
  
  activeRole: string = 'CUSTOMER';
  currentSupplierId: number | null = null;

  searchQuery: string = '';       // ১ম সার্চ বার: PO Number বা Status
  searchSupplier: string = '';    // ২য় সার্চ বার: Supplier Name বা ID
  searchDate: string = '';        // ৩য় সার্চ বার: Expected Delivery/Creation Date

  order: PurchaseOrderRequestModel = {
    quotationId: 0,
    issuedBy: 0,
    issuedByName: '', 
    totalAmount: 0,
    quantity: 1,      
    currency: 'USD',
    expectedDeliveryDate: '',
    status: 'DRAFT',
    createdAt: ''     
  };

  constructor(
    private service: PurchaseOrderService,
    private quotationService: QuotationService,
    private storage: StorageService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.activeRole = this.storage.getActiveRole()?.toUpperCase() || 'CUSTOMER';
    const currentUser = this.storage.getUser();
    if (currentUser) {
      this.order.issuedBy = currentUser.userId;
    }

    const cachedSupplier = this.storage.getData(KEYS.SUPPLIER) as { id: number; [key: string]: any };
    if (cachedSupplier) {
      this.currentSupplierId = cachedSupplier.id;
    }

    this.loadOrders();
    this.loadQuotations();
  }

  // 🌟 LOGISTICS_OFFICER এবং SUPPLIER এই বাটনটি দেখতে পাবে না
  canAddPurchaseOrder(): boolean {
    return this.activeRole === 'ADMIN' || 
           this.activeRole === 'MANAGER' || 
           this.activeRole === 'PROCUREMENT';
  }

  // রোল এবং প্রিভিলেজ ম্যাচিং হেল্পার (সার্চ কার্ড কন্ট্রোল)
  isManagementUser(): boolean {
    return this.activeRole === 'ADMIN' || 
           this.activeRole === 'MANAGER' || 
           this.activeRole === 'PROCUREMENT' || 
           this.activeRole === 'LOGISTICS_OFFICER';
  }

  // 🌟 LOGISTICS_OFFICER, PROCUREMENT এবং MANAGER ভেতরের সাধারণ সার্চ বার দেখতে পারবে না
  shouldShowCardSearchBar(): boolean {
    if (this.activeRole === 'PROCUREMENT' || this.activeRole === 'MANAGER' || this.activeRole === 'LOGISTICS_OFFICER') {
      return false;
    }
    return true; 
  }

  loadOrders() {
    this.service.findAll().subscribe({
      next: (data) => { 
        this.orders = data || [];
        this.applyDataIsolationAndFilters();
      },
      error: (err) => console.error('PO Load Error:', err)
    });
  }

  applyDataIsolationAndFilters(): void {
    let ordersPipe = [...this.orders];

    if (this.activeRole === 'SUPPLIER' && this.currentSupplierId) {
      ordersPipe = ordersPipe.filter((po: any) => {
        const sId = po.supplierId || (po.supplier ? po.supplier.id : null);
        return sId === this.currentSupplierId;
      });
    } 
    else if (!this.isManagementUser() && this.activeRole !== 'SUPPLIER') {
      ordersPipe = []; 
    }

    if (this.isManagementUser()) {
      if (this.searchQuery.trim() !== '') {
        const query = this.searchQuery.toLowerCase();
        ordersPipe = ordersPipe.filter((o: any) => 
          (o.poNumber && o.poNumber.toLowerCase().includes(query)) || 
          (o.status && o.status.toLowerCase().includes(query))
        );
      }

      if (this.searchSupplier.trim() !== '') {
        const supplierQuery = this.searchSupplier.toLowerCase();
        ordersPipe = ordersPipe.filter((o: any) => 
          (o.supplierName && o.supplierName.toLowerCase().includes(supplierQuery)) || 
          (o.supplierId && o.supplierId.toString() === supplierQuery) ||
          (o.supplier?.name && o.supplier.name.toLowerCase().includes(supplierQuery))
        );
      }

      if (this.searchDate !== '') {
        ordersPipe = ordersPipe.filter((o: any) => 
          (o.createdAt && o.createdAt.includes(this.searchDate)) ||
          (o.expectedDeliveryDate && o.expectedDeliveryDate.includes(this.searchDate))
        );
      }
    }

    this.filteredOrders = ordersPipe;
    this.cdr.markForCheck();
  }
  
  loadQuotations() {
    if (this.activeRole === 'SUPPLIER') return;

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
      this.order.supplierName = selectedQ.supplierName;
      this.order.supplierEmail = selectedQ.supplierEmail || 
                                 selectedQ.email || 
                                 (selectedQ.supplier ? selectedQ.supplier.email : 'N/A');
      this.order.purchaseRequisitionId = selectedQ.purchaseRequisitionId;
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
      issuedByName: (this.storage.getUser()?.name) || 'Unknown', 
      totalAmount: o.totalAmount,
      quantity: o.quantity,
      currency: o.currency,
      expectedDeliveryDate: o.expectedDeliveryDate,
      status: o.status,
      poNumber: o.poNumber,
      supplierName: o.supplierName,
      supplierEmail: o.supplierEmail, 
      purchaseRequisitionId: o.purchaseRequisitionId,
      createdAt: o.createdAt
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
    const systemDate = new Date().toISOString();

    this.order = {
      quotationId: 0,
      issuedBy: currentUser?.userId || 0,
      issuedByName: currentUser?.name || 'Unknown User',
      totalAmount: 0,
      quantity: 1,
      currency: 'USD',
      expectedDeliveryDate: '',
      status: 'DRAFT',
      createdAt: systemDate,
      supplierName: '',
      supplierEmail: '',
      purchaseRequisitionId: 0
    };
    
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}