import { ChangeDetectorRef, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


import {
  QuotationRequestModel,
  QuotationResponseModel
} from '../../shared/model/quatationModel';

import { QuotationService } from '../../../service/quatation.service';
import { AddProductService } from '../../../service/add-product.service';
import { SupplierService } from '../../../service/supplier.service';
import { PurchaseRequisitionService } from '../../../service/purchase-requisition.service';
import { environment } from '../../../../environment/environment';

@Component({
  selector: 'app-quatation.component',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quatation.component.html',
  styleUrl: './quatation.component.css',
})
export class QuatationComponent {

  // ==========================
  // Image Base URL
  // ==========================

  readonly imageBaseUrl = environment.imgUrl + "quotation/";

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
  ) {}

  ngOnInit(): void {
    this.loadQuotations();
    this.loadProducts();
    this.loadSuppliers();
    this.loadRequisitions();
  }

  // ==========================
  // Image URL
  // ==========================

  getImageUrl(fileName: string | null | undefined): string {

    if (!fileName) {
      return 'assets/no-image.png';
    }

    return this.imageBaseUrl + fileName;
  }

  // ==========================
  // Load Data
  // ==========================

  loadQuotations(): void {
    this.service.findAll().subscribe({
      next: (data) => {
        this.quotations = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  loadProducts(): void {
    this.productService.findAll().subscribe({
      next: data => this.products = data || []
    });
  }

  loadSuppliers(): void {
    this.supplierService.findAll().subscribe({
      next: data => this.suppliers = data || []
    });
  }

  loadRequisitions(): void {
    this.requisitionService.findAll().subscribe({
      next: data => this.requisitions = data || []
    });
  }

  onProductChange(event: any): void {

    const id = +event.target.value;

    const target = this.products.find(p => p.id === id);

    if (target) {
      this.quotation.productName = target.name;
    }
  }

  onFileChange(event: any): void {

    if (event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  openDrawer(): void {
    this.reset();
    this.isEdit = false;
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  closeDrawer(): void {
    this.isDrawerOpen = false;
    this.reset();
    this.cdr.markForCheck();
  }

  save(): void {

    this.errorMessage = null;

    if (
      this.quotation.supplierId === 0 ||
      this.quotation.productIds === 0 ||
      this.quotation.purchaseRequisitionId === 0
    ) {
      this.errorMessage =
        "Supplier, Product and Purchase Requisition are required.";
      return;
    }

    if (this.isEdit && this.currentEditId != null) {

      this.service.update(this.currentEditId, this.quotation).subscribe({

        next: () => {
          alert("Quotation Updated Successfully");
          this.closeDrawer();
          this.loadQuotations();
        },

        error: err =>
          this.errorMessage =
            err.error?.message || "Update Failed"

      });

    } else {

      this.service.save(this.quotation, this.selectedFile).subscribe({

        next: () => {
          alert("Quotation Saved Successfully");
          this.closeDrawer();
          this.loadQuotations();
        },

        error: err =>
          this.errorMessage =
            err.error?.message || "Save Failed"

      });

    }
  }

  edit(o: QuotationResponseModel): void {

    this.currentEditId = o.id;
    this.isEdit = true;

    this.quotation = {

      supplierId: o.supplierId,
      productIds: o.productIds,
      productName: o.productName,
      purchaseRequisitionId: o.purchaseRequisitionId,
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

  delete(id: number): void {

    if (!confirm("Delete this quotation?")) {
      return;
    }

    this.service.delete(id).subscribe({

      next: () => {
        alert("Deleted Successfully");
        this.loadQuotations();
      },

      error: err =>
        alert(err.error?.message || err.message)

    });
  }

  reset(): void {

    this.quotation = {

      supplierId: 0,
      productIds: 0,
      productName: '',
      purchaseRequisitionId: 0,
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
    this.currentEditId = null;
    this.isEdit = false;
    this.errorMessage = null;
  }

}