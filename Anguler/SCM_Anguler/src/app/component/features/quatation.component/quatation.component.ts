import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { QuotationRequestModel, QuotationResponseModel } from '../../shared/model/quatationModel';
import { QuotationService } from '../../../service/quatation.service';
import { AddProductService } from '../../../service/add-product.service';
import { SupplierService } from '../../../service/supplier.service';
import { PurchaseRequisitionService } from '../../../service/purchase-requisition.service';
import { environment } from '../../../../environment/environment';

@Component({
  selector: 'app-quatation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quatation.component.html',
  styleUrl: './quatation.component.css',
})
export class QuatationComponent implements OnInit {

  // ==========================================
  // Image Storage Base Endpoints
  // ==========================================
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

  // 🎯 আপনার আপডেট করা ফ্রন্টএন্ড মডেল স্ট্রাকচারের সাথে পুরোপুরি সিঙ্কড অবজেক্ট
  quotation: QuotationRequestModel = {
    supplierId: 0,
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

  // ==========================================
  // Image Render Resolution
  // ==========================================
  getImageUrl(fileName: string | null | undefined): string {
    if (!fileName) {
      return 'assets/no-image.png';
    }
    return this.imageBaseUrl + fileName;
  }

  // ==========================================
  // Core SCM Sourcing Fetch Operations
  // ==========================================
  loadQuotations(): void {
    this.service.findAll().subscribe({
      next: (data) => {
        this.quotations = data || [];
        this.cdr.markForCheck();
      },
      error: (err) => this.handleError(err)
    });
  }

  loadProducts(): void {
    this.productService.findAll().subscribe({
      next: (data) => {
        this.products = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  loadSuppliers(): void {
    this.supplierService.findAll().subscribe({
      next: (data) => {
        this.suppliers = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  loadRequisitions(): void {
    this.requisitionService.findAll().subscribe({
      next: (data) => {
        this.requisitions = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  // ==========================================
  // Event & Form Stream Emitters
  // ==========================================
  onFileChange(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
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

  // ==========================================
  // Persistence Dispatch Logic (CRUD Operations)
  // ==========================================
  save(): void {
    this.errorMessage = null;

    // 🎯 রিকোয়েস্ট মডেলের আইডি ভ্যালিডেশন গার্ডস
    if (
      this.quotation.supplierId === 0 ||
      this.quotation.purchaseRequisitionId === 0
    ) {
      this.errorMessage = "Validation Fault: Target Supplier and Purchase Requisition node mapping are mandatory.";
      this.cdr.markForCheck();
      return;
    }

    if (this.isEdit && this.currentEditId != null) {
      // 🎯 আপডেট প্রসেস (কন্ট্রোলারের স্ট্যান্ডার্ড @RequestBody JSON চেইনের সাথে সিঙ্কড)
     this.service.save(this.quotation, this.selectedFile).subscribe({
  next: () => {
    alert("Quotation document node registered into logistics gateway.");
    this.closeDrawer();
    this.loadQuotations();
  },
  error: (err) => this.handleError(err) // 👈 এখানেও '=>' হবে
});
    } else {
      // 🎯 ক্রিয়েট প্রসেস (কন্ট্রোলারের @RequestPart String quotationJson + MultipartFile এর সাথে সিঙ্কড)
      this.service.save(this.quotation, this.selectedFile).subscribe({
        next: () => {
          alert("Quotation document node registered into logistics gateway.");
          this.closeDrawer();
          this.loadQuotations();
        },
        error: (err) =>  this.handleError(err)
      });
    }
  }

  edit(o: QuotationResponseModel): void {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;

    // 🎯 রেসপন্স মডেল থেকে রিকোয়েস্ট মডেলে প্রপার্টি রি-ম্যাপিং
    this.quotation = {
      supplierId: o.supplierId,
      purchaseRequisitionId: o.purchaseRequisitionId,
      leadTimeDays: o.leadTimeDays,
      isSelected: o.isSelected,
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
    if (!confirm("Are you sure you want to permanently delete this quotation bid profile? This action is locked into security matrices!")) {
      return;
    }

    this.service.delete(id).subscribe({
      next: () => {
        alert("Quotation envelope successfully purged from directories.");
        this.loadQuotations();
      },
      error: (err) => alert(err.error?.message || err.message || "Deletion transaction runtime failure.")
    });
  }

  reset(): void {
    this.quotation = {
      supplierId: 0,
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

  private handleError(err: any): void {
    this.errorMessage = err.error?.message || err.message || "Quotation Sourcing Pipeline Timeout.";
    this.cdr.markForCheck();
  }
}