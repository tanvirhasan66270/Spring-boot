import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { purchaseRequisitionRequestModel, purchaseRequisitionResponseModel } from '../../shared/model/purchase-requisionModel';
import { PurchaseRequisitionService } from '../../../service/purchase-requisition.service';
import { AddProductService } from '../../../service/add-product.service';
import { SupplierService } from '../../../service/supplier.service';
import { StorageService, KEYS } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-purchase-requisition',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase-requisition.component.html',
  styleUrl: './purchase-requisition.component.css'
})
export class PurchaseRequisitionComponent implements OnInit {

  requisitions: purchaseRequisitionResponseModel[] = [];
  filteredRequisitions: purchaseRequisitionResponseModel[] = []; // 🎯 ডাইনামিক রিয়েল-টাইম সার্চ বাফার
  products: any[] = [];
  suppliers: any[] = [];
  userRole: string = '';
  currentSupplierId: number | null = null;

  // 🔍 ডুয়াল সার্চ ইনপুট মডেলস
  searchId: string = '';
  searchUrgency: string = '';

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;

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
    this.userRole = this.storage.getActiveRole()?.toUpperCase() || '';
    const currentUser = this.storage.getUser();
    if (currentUser) {
      this.requisition.requestedBy = currentUser.userId;
      if (!this.userRole && currentUser.role) {
        this.userRole = currentUser.role.toUpperCase();
      }
    }

    // 🔒 সেশন ক্যাশ থেকে সাপ্লায়ার মেটা অবজেক্ট ও আইডি রিড
    const cachedSupplier = this.storage.getData(KEYS.SUPPLIER) as any;
    if (cachedSupplier) {
      this.currentSupplierId = cachedSupplier.id;
    }

    this.loadRequisitions();
    this.loadProducts();
    this.loadSuppliers();
  }

  loadRequisitions() { 
    this.service.findAll().subscribe({
      next: (data) => {
        const allRequisitions = data || [];

        // 🔒 STRICT DATA ISOLATION LOCK: ইউজার SUPPLIER হলে শুধুমাত্র তার সাথে ম্যাচিং APPROVED ডাটা ম্যাপ হবে
        if (this.userRole === 'SUPPLIER' && this.currentSupplierId) {
          this.requisitions = allRequisitions.filter((pr: any) => {
            const hasSupplier = pr.supplierIds?.includes(this.currentSupplierId) || 
                                (pr.suppliers && pr.suppliers.some((s: any) => s.id === this.currentSupplierId));
            return hasSupplier && pr.approvalStatus === 'APPROVED';
          });
        } else {
          // ADMIN/MANAGER/PROCUREMENT হলে গ্লোবাল ডাটা লোড হবে
          this.requisitions = allRequisitions;
        }

        this.filteredRequisitions = [...this.requisitions];
        this.applyDoubleSearch(); // যদি সার্চ ফিল্ডে অলরেডি কোনো ইনপুট থাকে
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || "Failed to sync cluster directories.";
        this.cdr.markForCheck();
      }
    }); 
  }

  // 🎯 ডুয়াল সার্চ বার ফিল্টারিং লজিক (PR Reference ID এবং Urgency Level)
  applyDoubleSearch() {
    const idTerm = this.searchId.toLowerCase().trim();
    const urgencyTerm = this.searchUrgency.toLowerCase().trim();

    this.filteredRequisitions = this.requisitions.filter(pr => {
      const prIdStr = pr.id ? `#PRQ-${pr.id}` : '';
      const urgency = pr.urgencyLevel || 'LOW';

      const matchesId = prIdStr.toLowerCase().includes(idTerm) || pr.id?.toString().includes(idTerm);
      const matchesUrgency = urgency.toLowerCase().includes(urgencyTerm);

      return matchesId && matchesUrgency;
    });

    this.cdr.markForCheck();
  }

  loadProducts() { this.productService.findAll().subscribe(data => { this.products = data || []; this.cdr.markForCheck(); }); }
  loadSuppliers() { 
    if (this.userRole === 'SUPPLIER') return; // সাপ্লায়ার হলে গ্লোবাল ভেন্ডার লিস্ট লোড করার প্রয়োজন নেই
    this.supplierService.findAll().subscribe(data => { this.suppliers = data || []; this.cdr.markForCheck(); }); 
  }

  onProductSelect(event: any) {
    const id = +event.target.value;
    if (id && !this.requisition.productIds.includes(id)) {
      this.requisition.productIds.push(id);
    }
    this.selectedProductInput = null; 
    this.cdr.markForCheck();
  }

  removeProduct(id: number) {
    this.requisition.productIds = this.requisition.productIds.filter(pId => pId !== id);
    this.cdr.markForCheck();
  }

  getProductById(id: number) {
    return this.products.find(p => p.id === id);
  }

  onSupplierSelect(event: any) {
    const id = +event.target.value;
    if (id && !this.requisition.supplierIds.includes(id)) {
      this.requisition.supplierIds.push(id);
    }
    this.selectedSupplierInput = null; 
    this.cdr.markForCheck();
  }

  removeSupplier(id: number) {
    this.requisition.supplierIds = this.requisition.supplierIds.filter(sId => sId !== id);
    this.cdr.markForCheck();
  }

  getSupplierById(id: number) {
    return this.suppliers.find(s => s.id === id);
  }

  autoGrowTextarea(event: any) {
    const element = event.target;
    element.style.height = 'auto';
    element.style.height = (element.scrollHeight + 2) + 'px';
  }

  openDrawer() { this.reset(); this.isEdit = false; this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.reset(); this.cdr.markForCheck(); }

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
      currency: 'USD', 
      quantityRequired: o.quantityRequired,
      urgencyLevel: o.urgencyLevel,
      requiredByDate: o.requiredByDate,
      remarks: o.remarks || ''
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Are you sure you want to permanently delete this procurement requisition record?")) {
      this.service.delete(id).subscribe({
        next: () => { alert("Purged successfully."); this.loadRequisitions(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
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