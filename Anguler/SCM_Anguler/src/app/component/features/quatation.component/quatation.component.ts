import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { QuotationRequestModel, QuotationResponseModel } from '../../shared/model/quatationModel';
import { QuotationService } from '../../../service/quatation.service';
import { AddProductService } from '../../../service/add-product.service';
import { SupplierService } from '../../../service/supplier.service';
import { PurchaseRequisitionService } from '../../../service/purchase-requisition.service';
import { StorageService, KEYS } from '../../../auth/auth_service/storage.service';
import { environment } from '../../../../environment/environment';

@Component({
  selector: 'app-quatation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quatation.component.html',
  styleUrl: './quatation.component.css',
})
export class QuatationComponent implements OnInit {

  readonly imageBaseUrl = environment.imgUrl + "quotation/";

  quotations: QuotationResponseModel[] = [];
  filteredQuotations: QuotationResponseModel[] = []; 
  
  searchQtn: string = '';
  searchSupplierName: string = '';
  searchState: string = '';

  products: any[] = [];
  suppliers: any[] = [];
  requisitions: any[] = [];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;
  selectedFile: File | null = null;
  
  activeRole: string = 'CUSTOMER';
  currentSupplierId: number | null = null;
  currentSupplierName: string = ''; 

  quotation: QuotationRequestModel = {
    supplierId: 0,
    purchaseRequisitionId: 0,
    leadTimeDays: 1,
    receivedAt: '',
    status: 'PENDING',
    productDescription: '',
    unitPrice: 0,
    quantity: 1,
    deliveryTime: '',
    warranty: '',
    notes: '',
    attachmentUrl: ''
  };

  constructor(
    private service: QuotationService,
    private productService: AddProductService,
    private supplierService: SupplierService,
    private requisitionService: PurchaseRequisitionService,
    private storage: StorageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.activeRole = this.storage.getActiveRole()?.toUpperCase() || 'CUSTOMER';
    const user = this.storage.getUser();
    
    const cachedSupplier = this.storage.getData(KEYS.SUPPLIER) as any;
    if (cachedSupplier) {
      this.currentSupplierId = cachedSupplier.id;
      this.currentSupplierName = cachedSupplier.name || user?.name || 'Your Supplier Account';
    } else {
      this.currentSupplierName = user?.name || 'Your Supplier Account';
    }

    this.loadQuotations();
    this.loadProducts();
    this.loadSuppliers();
    this.loadRequisitions();
  }

  getSupplierNameById(supplierId: number): string {
    if (!supplierId || !this.suppliers || this.suppliers.length === 0) {
      return 'Loading...';
    }
    const supplier = this.suppliers.find(s => s.id === supplierId);
    return supplier ? supplier.name : 'Unknown Supplier';
  }

  getLinkedRequisitionQty(): number {
    if (!this.quotation.purchaseRequisitionId || !this.requisitions) {
      return 0;
    }
    const pr = this.requisitions.find(r => r.id === Number(this.quotation.purchaseRequisitionId));
    return pr ? pr.quantityRequired : 0;
  }

  isQuantityValid(): boolean {
    if (!this.quotation.purchaseRequisitionId) {
      return true;
    }
    const prQty = this.getLinkedRequisitionQty();
    if (prQty === 0) {
      return true;
    }
    if (this.quotation.quantity === null || this.quotation.quantity === undefined) {
      return true;
    }
    return this.quotation.quantity <= prQty;
  }

  getImageUrl(fileName: string | null | undefined): string {
    if (!fileName) {
      return 'assets/no-image.png';
    }
    return this.imageBaseUrl + fileName;
  }

  loadQuotations(): void {
    this.service.findAll().subscribe({
      next: (data) => {
        const allRfqs = data || [];
        
        if (this.activeRole === 'SUPPLIER') {
          if (!this.currentSupplierId) {
            const user = this.storage.getUser();
            const match = allRfqs.find((q: any) => {
              const sName = q.supplierName || '';
              return sName.toLowerCase() === (user?.name || '').toLowerCase();
            });
            if (match) {
              this.currentSupplierId = match.supplierId || (match.supplierId ? match.supplierId : null);
              this.currentSupplierName = match.supplierName || this.currentSupplierName;
              this.storage.saveData(KEYS.SUPPLIER, { id: this.currentSupplierId, name: this.currentSupplierName });
            }
          }

          if (this.currentSupplierId) {
            this.quotations = allRfqs.filter((q: any) => {
              const sId = q.supplierId || (q.supplier ? q.supplier.id : null);
              return Number(sId) === Number(this.currentSupplierId);
            });
          } else {
            this.quotations = allRfqs;
          }
        } else {
          this.quotations = allRfqs;
        }

        this.filteredQuotations = [...this.quotations];
        this.applyFilters(); 
        this.cdr.markForCheck();
      },
      error: (err) => this.handleError(err)
    });
  }

  applyFilters(): void {
    const qtnTerm = this.searchQtn.toLowerCase().trim();
    const supTerm = this.searchSupplierName.toLowerCase().trim();
    const stateTerm = this.searchState.toLowerCase().trim();

    this.filteredQuotations = this.quotations.filter(q => {
      const qtnNo = (q.quotationNumber || `QTN-${q.id}`).toLowerCase();
      const supName = (q.supplierName || this.getSupplierNameById(q.supplierId)).toLowerCase();
      const status = (q.status || 'PENDING').toLowerCase();

      return qtnNo.includes(qtnTerm) && 
             supName.includes(supTerm) && 
             status.includes(stateTerm);
    });
    this.cdr.markForCheck();
  }

  loadProducts(): void {
    this.productService.findAll().subscribe({ next: (data) => { this.products = data || []; this.cdr.markForCheck(); } });
  }

  loadSuppliers(): void {
    if (this.activeRole === 'SUPPLIER') return;
    this.supplierService.findAll().subscribe({ next: (data) => { this.suppliers = data || []; this.cdr.markForCheck(); } });
  }

  loadRequisitions(): void {
    this.requisitionService.findAll().subscribe({ next: (data) => { this.requisitions = data || []; this.cdr.markForCheck(); } });
  }

  onFileChange(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  openDrawer(): void {
    if (this.activeRole !== 'ADMIN' && (this.activeRole === 'PROCUREMENT' || this.activeRole === 'MANAGER')) {
      console.warn("Access Denied: Action restricted for this role.");
      return; 
    }

    this.reset();
    this.isEdit = false;
    
    // if (this.activeRole === 'SUPPLIER' && this.currentSupplierId) {
    //   this.quotation.supplierId = this.currentSupplierId;
    //   console.log(this.currentSupplierId)
    // }
    
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  closeDrawer(): void { 
    this.isDrawerOpen = false; 
    this.reset(); 
    this.cdr.markForCheck(); 
  }

  isProcurement(): boolean {
    const role = this.storage.getActiveRole()?.toUpperCase();
    return role === 'PROCUREMENT' || role === 'MANAGER';
  }

  canUpdateStatus(): boolean {
    const role = this.storage.getActiveRole()?.toUpperCase();
    return role === 'ADMIN' || role === 'PROCUREMENT';
  }

  updateStatus(q: QuotationResponseModel): void {
    if (!this.canUpdateStatus()) {
      alert("Access Denied: Only Admin and Procurement can update status.");
      this.loadQuotations();
      return;
    }

    this.service.updateStatus(q.id, q.status).subscribe({
      next: () => {
        alert("Status updated successfully.");
        this.loadQuotations();
      },
      error: (err) => {
        console.error("Update failed", err);
        alert("Failed to update status. Please try again.");
        this.loadQuotations();
      }
    });
  }

  get userRole(): string {
    return this.activeRole;
  }

  save(): void {
    this.errorMessage = null;

    if (this.quotation.supplierId === 0 && this.activeRole !== 'ADMIN') {
      this.errorMessage = "Validation Fault: Target Supplier mapping is mandatory.";
      return;
    }
    
    if (this.quotation.purchaseRequisitionId === 0) {
      this.errorMessage = "Validation Fault: Purchase Requisition node mapping is mandatory.";
      return;
    }

    if (!this.isQuantityValid()) {
      this.errorMessage = "Validation Fault: Quantity limit exceeded.";
      return;
    }

    if (this.activeRole === 'SUPPLIER' && this.currentSupplierId != null) {
      this.quotation.supplierId = this.currentSupplierId;
    }

    this.service.save(this.quotation, this.selectedFile).subscribe({
      next: () => {
        alert("Quotation document node synchronized successfully.");
        this.closeDrawer();
        this.loadQuotations();
      },
      error: (err) => this.handleError(err)
    });
  }

  edit(o: QuotationResponseModel): void {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;

    this.quotation = {
      supplierId: o.supplierId,
      purchaseRequisitionId: o.purchaseRequisitionId,
      leadTimeDays: o.leadTimeDays,
      receivedAt: o.receivedAt,
      status: o.status,
      productDescription: o.productDescription || '',
      unitPrice: o.unitPrice,
      quantity: o.quantity,
      deliveryTime: o.deliveryTime,
      warranty: o.warranty || '',
      notes: o.notes || '',
      attachmentUrl: o.attachmentUrl || ''
    };

    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number): void {
    if (!confirm("Are you sure you want to permanently delete this quotation bid profile?")) return;

    this.service.delete(id).subscribe({
      next: () => {
        alert("Quotation envelope successfully purged.");
        this.loadQuotations();
      },
      error: (err) => alert(err.error?.message || "Deletion failure.")
    });
  }

  canUploadQuotation(): boolean {
    return !this.isProcurement();
  }

  reset(): void {
    this.quotation = {
      supplierId: this.activeRole === 'SUPPLIER' && this.currentSupplierId ? this.currentSupplierId : 0,
      purchaseRequisitionId: 0,
      leadTimeDays: 1,
      receivedAt: '',
      status: 'PENDING',
      productDescription: '',
      unitPrice: 0,
      quantity: 1,
      deliveryTime: '',
      warranty: '',
      notes: '',
      attachmentUrl: ''
    };
    this.selectedFile = null;
    this.currentEditId = null;
    this.isEdit = false;
    this.errorMessage = null;
  }

  private handleError(err: any): void {
    this.errorMessage = err.error?.message || err.message || "Quotation Pipeline Error.";
    this.cdr.markForCheck();
  }
}