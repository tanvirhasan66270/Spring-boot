import { ChangeDetectorRef, Component } from '@angular/core';
import { QuotationRequestModel, QuotationResponseModel } from '../../shared/model/quatationModel';
import { QuotationService } from '../../../service/quatation.service';
import { AddProductService } from '../../../service/add-product.service';
import { SupplierService } from '../../../service/supplier.service';
import { PurchaseRequisitionService } from '../../../service/purchase-requisition.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-quatation.component',
  imports: [CommonModule,FormsModule],
  templateUrl: './quatation.component.html',
  styleUrl: './quatation.component.css',
})
export class QuatationComponent {


quotations: QuotationResponseModel[] = [];
  products: any[] = [];
  suppliers: any[] = [];
  requisitions: any[] = [];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;
  selectedFile: File | null = null;

  quotation: QuotationRequestModel = {
    supplierId: 0,
    productIds: 0,
    productName: '',
    purchaseRequisitionId: 0,
    validUntil: '',
    leadTimeDays: 1,
    isSelected: false,
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
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadQuotations();
    this.loadProducts();
    this.loadSuppliers();
    this.loadRequisitions();
  }

  loadQuotations() {
    this.service.findAll().subscribe({ next: (data) => { this.quotations = data || []; this.cdr.markForCheck(); } });
  }

  loadProducts() {
    this.productService.findAll().subscribe({ next: (data) => this.products = data || [] });
  }

  loadSuppliers() {
    this.supplierService.findAll().subscribe({ next: (data) => this.suppliers = data || [] });
  }

  loadRequisitions() {
    this.requisitionService.findAll().subscribe({ next: (data) => this.requisitions = data || [] });
  }

  onProductChange(event: any) {
    const id = +event.target.value;
    const targetProduct = this.products.find(p => p.id === id);
    if (targetProduct) {
      this.quotation.productName = targetProduct.name; // স্ন্যাপশট নেম অটো সিঙ্ক
    }
  }

  onFileChange(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  openDrawer() { this.reset(); this.isEdit = false; this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.reset(); this.cdr.markForCheck(); }

  save() {
    this.errorMessage = null;

    if (this.quotation.supplierId === 0 || this.quotation.productIds === 0 || this.quotation.purchaseRequisitionId === 0) {
      this.errorMessage = "Validation Exception: SCM cluster mappings (Supplier, Product, PR Node) are mandatory.";
      return;
    }

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.quotation).subscribe({
        next: () => { alert("Quotation entry updated successfully."); this.closeDrawer(); this.loadQuotations(); },
        error: (err) => this.errorMessage = err.error?.message || "Modification pipeline fault."
      });
    } else {
      this.service.save(this.quotation, this.selectedFile).subscribe({
        next: () => { alert("New supplier quotation authorized and logged."); this.closeDrawer(); this.loadQuotations(); },
        error: (err) => this.errorMessage = err.error?.message || "Deployment exception."
      });
    }
  }

  edit(o: QuotationResponseModel) {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;
    this.quotation = {
      supplierId: o.supplierId,
      productIds: o.productIds,
      productName: o.productName,
      purchaseRequisitionId: o.purchaseRequisitionId,
      validUntil: o.validUntil,
      leadTimeDays: o.leadTimeDays,
      isSelected: o.isSelected,
      receivedAt: o.receivedAt,
      status: o.status,
      productDescription: o.productDescription,
      unitPrice: o.unitPrice,
      quantity: o.quantity,
      deliveryTime: o.deliveryTime,
      warranty: o.warranty,
      notes: o.notes || '',
      attachmentUrl: o.attachmentUrl || ''
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Definitively wipe this supplier quotation envelope instance?")) {
      this.service.delete(id).subscribe({
        next: () => { alert("Quotation node pruned successfully."); this.loadQuotations(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  reset() {
    this.quotation = {
      supplierId: 0,
      productIds: 0,
      productName: '',
      purchaseRequisitionId: 0,
      validUntil: '',
      leadTimeDays: 1,
      isSelected: false,
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
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }

}
