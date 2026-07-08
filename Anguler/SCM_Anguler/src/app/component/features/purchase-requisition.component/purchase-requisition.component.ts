import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { purchaseRequisitionRequestModel, purchaseRequisitionResponseModel } from '../../shared/model/purchase-requisionModel';
import { PurchaseRequisitionService } from '../../../service/purchase-requisition.service';
import { AddProductService } from '../../../service/add-product.service';
import { SupplierService } from '../../../service/supplier.service';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-purchase-requisition',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase-requisition.component.html',
  styleUrl: './purchase-requisition.component.css'
})
export class PurchaseRequisitionComponent implements OnInit {

  requisitions: purchaseRequisitionResponseModel[] = [];
  products: any[] = [];
  suppliers: any[] = [];
  userRole: string = '';

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;

  // ড্রপডাউন মডেল বাইন্ডিং ট্র্যাকার
  selectedProductInput: any = null;
  selectedSupplierInput: any = null;

  requisition: purchaseRequisitionRequestModel = {
    requestedBy: 0,
    productIds: [],
    supplierIds: [],
    currency: 'USD',
    quantityRequired: 1,
    urgencyLevel: 'LOW',
    requiredByDate: '',
    remarks: ''
  };

  constructor(
    private service: PurchaseRequisitionService,
    private productService: AddProductService,
    private supplierService: SupplierService,
    private storage: StorageService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    const currentUser = this.storage.getUser();
    if (currentUser) {
      this.requisition.requestedBy = currentUser.userId;
      this.userRole = currentUser.role ? currentUser.role.toUpperCase() : '';
    }

    this.loadRequisitions();
    this.loadProducts();
    this.loadSuppliers();
  }

  // ডাইনামিক প্রোডাক্ট সিলেকশন ইঞ্জিন ──
  onProductSelect(event: any) {
    const id = +event.target.value;
    if (id && !this.requisition.productIds.includes(id)) {
      this.requisition.productIds.push(id);
    }
    this.selectedProductInput = null; // ড্রপডাউন রিসেট
    this.cdr.markForCheck();
  }

  removeProduct(id: number) {
    this.requisition.productIds = this.requisition.productIds.filter(pId => pId !== id);
    this.cdr.markForCheck();
  }

  getProductById(id: number) {
    return this.products.find(p => p.id === id);
  }

  //  ডাইনামিক সাপ্লায়ার সিলেকশন ইঞ্জিন ──
  onSupplierSelect(event: any) {
    const id = +event.target.value;
    if (id && !this.requisition.supplierIds.includes(id)) {
      this.requisition.supplierIds.push(id);
    }
    this.selectedSupplierInput = null; // ড্রপডাউন রিসেট
    this.cdr.markForCheck();
  }

  removeSupplier(id: number) {
    this.requisition.supplierIds = this.requisition.supplierIds.filter(sId => sId !== id);
    this.cdr.markForCheck();
  }

  getSupplierById(id: number) {
    return this.suppliers.find(s => s.id === id);
  }

  // টেক্সট-এরিয়া অটো-গ্রো লজিক (Auto-growing Textarea) ──
  autoGrowTextarea(event: any) {
    const element = event.target;
    element.style.height = 'auto';
    element.style.height = element.scrollHeight + '2px' + 'px';
  }

  // ── বাকি স্ট্যান্ডার্ড CRUD মেথডসমূহ ──
  openDrawer() { this.reset(); this.isEdit = false; this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.reset(); this.cdr.markForCheck(); }
  
  loadRequisitions() { this.service.findAll().subscribe(data => { this.requisitions = data || []; this.cdr.markForCheck(); }); }
  loadProducts() { this.productService.findAll().subscribe(data => { this.products = data || []; this.cdr.markForCheck(); }); }
  loadSuppliers() { this.supplierService.findAll().subscribe(data => { this.suppliers = data || []; this.cdr.markForCheck(); }); }

  authorizeApproval(id: number) {
    if (confirm("Verify and AUTHORIZE this procurement requisition vector?")) {
      this.service.approve(id).subscribe({
        next: () => { alert("Requisition APPROVED successfully."); this.loadRequisitions(); },
        error: (err) => alert(err.error?.message || "Authorization exception.")
      });
    }
  }

  authorizeRejection(id: number, action: 'REJECT' | 'CANCEL') {
    if (confirm(`Definitively register ${action} state on this requisition sheet?`)) {
      this.service.rejectOrCancel(id, action).subscribe({
        next: () => { alert(`Requisition state set to ${action} successfully.`); this.loadRequisitions(); },
        error: (err) => alert(err.error?.message || "Status mutation failure.")
      });
    }
  }

  save() {
    this.errorMessage = null;
    if (this.requisition.productIds.length === 0 || this.requisition.supplierIds.length === 0) {
      this.errorMessage = "Validation Fault: Please select at least one product and supplier node.";
      return;
    }

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.requisition).subscribe({
        next: () => { alert("Requisition updated successfully!"); this.closeDrawer(); this.loadRequisitions(); },
        error: (err) => this.errorMessage = err.error?.message || "Update failed."
      });
    } else {
      this.service.save(this.requisition).subscribe({
        next: () => { alert("Requisition logged successfully!"); this.closeDrawer(); this.loadRequisitions(); },
        error: (err) => this.errorMessage = err.error?.message || "Creation error."
      });
    }
  }

  edit(o: purchaseRequisitionResponseModel) {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;
    this.requisition = {
      requestedBy: o.requestedBy,
      productIds: [...o.productIds],
      supplierIds: [...o.supplierIds],
      currency: 'USD', // ফিক্সড ইউএসডি
      quantityRequired: o.quantityRequired,
      urgencyLevel: o.urgencyLevel,
      requiredByDate: o.requiredByDate,
      remarks: o.remarks || ''
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    this.service.delete(id).subscribe({
      next: () => { alert("Purged successfully."); this.loadRequisitions(); },
      error: (err) => alert(err.error?.message || err.message)
    });
  }

  reset() {
    const currentUser = this.storage.getUser();
    this.requisition = {
      requestedBy: currentUser?.userId || 0,
      productIds: [],
      supplierIds: [],
      currency: 'USD',
      quantityRequired: 1,
      urgencyLevel: 'LOW',
      requiredByDate: '',
      remarks: ''
    };
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }

  isProductSelected(productId: number): boolean {
  return this.requisition.productIds.includes(productId);
}

isSupplierSelected(supplierId: number): boolean {
  return this.requisition.supplierIds.includes(supplierId);
}
}