import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  QCChecklistRequestModel,
  QCInspectionRequestModel,
  QCInspectionResponseModel,
} from '../../shared/model/qc-inspection';
import { QcInspectionService } from '../../../service/qc-inspection.service';
import { GoodRecivedNoteService } from '../../../service/good-recived-note.service';
import { AddProductService } from '../../../service/add-product.service';
import { QcInspectorService } from '../../../service/qc-inspactor.service';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-qc-inspection',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './qc-inspection.component.html',
  styleUrl: './qc-inspection.component.css',
})
export class QcInspectionComponent implements OnInit {
  inspections: QCInspectionResponseModel[] = [];
  grns: any[] = [];
  products: any[] = [];
  inspectors: any[] = [];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;
  selectedFile: File | null = null;
  currentUserId: number = 0;

  inspection: QCInspectionRequestModel = {
    grnId: 0,
    productId: 0,
    inspectionType: 'VISUAL',
    inspectedBy: 0,
    sampleSize: 5,
    defectsFound: 0,
    defectDescription: '',
    result: 'GOOD',
    certificateRef: '',
    labTestReport: '',
    inspectedAt: '',
    checklists: [],
  };

  constructor(
    private service: QcInspectionService,
    private grnService: GoodRecivedNoteService,
    private productService: AddProductService,
    private qcInspectorService: QcInspectorService,
    private storage: StorageService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    const user = this.storage.getUser();
    if (user) {
      this.currentUserId = user.userId;
      this.inspection.inspectedBy = user.userId;
    }
    this.loadInspections();
    this.loadGRNs();
    this.loadProducts();
    this.loadInspectors();
  }

  loadInspections() {
    this.service.findAll().subscribe({
      next: (data) => {
        this.inspections = data || [];
        this.cdr.markForCheck();
      },
    });
  }

  loadGRNs() {
    this.grnService.findAll().subscribe({ next: (data) => (this.grns = data || []) });
  }

  loadProducts() {
    this.productService.findAll().subscribe({ next: (data) => (this.products = data || []) });
  }

  loadInspectors() {
    this.qcInspectorService.findAll().subscribe({
      next: (data) => {
        this.inspectors = (data || []).map((i: any) => ({
          id: i.userId || i.id,
          name: i.name || i.inspectorName,
          designation: i.designation || 'QC Inspector',
        }));
        this.cdr.markForCheck();
      },
      error: () => {
        this.inspectors = [];
      },
    });
  }

  onFileChange(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  addChecklistRow() {
    const row: QCChecklistRequestModel = {
      checkpointName: '',
      isPassed: true,
      remarks: '',
    };
    this.inspection.checklists.push(row);
    this.cdr.markForCheck();
  }

  removeChecklistRow(idx: number) {
    this.inspection.checklists.splice(idx, 1);
    this.cdr.markForCheck();
  }

  openDrawer() {
    this.reset();
    this.isEdit = false;
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }
  closeDrawer() {
    this.isDrawerOpen = false;
    this.reset();
    this.cdr.markForCheck();
  }

  save() {
    this.errorMessage = null;

    if (
      !this.inspection.grnId ||
      +this.inspection.grnId === 0 ||
      !this.inspection.productId ||
      +this.inspection.productId === 0 ||
      !this.inspection.inspectedBy ||
      +this.inspection.inspectedBy === 0
    ) {
      this.errorMessage =
        'Validation Fault: Linked GRN Code, Target Product, and Inspector are mandatory.';
      this.cdr.markForCheck();
      return;
    }

    if (!this.inspection.inspectedAt) {
      this.errorMessage = 'Validation Fault: Inspection Execution Date is mandatory.';
      this.cdr.markForCheck();
      return;
    }

    // 🎯 জ্যাকসন ম্যাপিং ও এনাম প্রোটেকশন পার্সিং পেলোড
    const payload: QCInspectionRequestModel = {
      ...this.inspection,
      grnId: +this.inspection.grnId,
      productId: +this.inspection.productId,
      inspectedBy: +this.inspection.inspectedBy,
      sampleSize: +this.inspection.sampleSize,
      defectsFound: +this.inspection.defectsFound,
      result: this.inspection.result ? this.inspection.result.toUpperCase() : 'GOOD',
      checklists: this.inspection.checklists.map((c) => ({
        checkpointName: c.checkpointName || 'General Checkpoint',
        isPassed: String(c.isPassed) === 'true', // পিওর বুলিয়ান কাস্টিং
        remarks: c.remarks || '',
      })),
    };

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, payload, this.selectedFile).subscribe({
        next: () => {
          alert('QC Audit Ledger updated successfully.');
          this.closeDrawer();
          this.loadInspections();
        },
        error: (err) => this.handleErrorLog(err),
      });
    } else {
      this.service.save(payload, this.selectedFile).subscribe({
        next: () => {
          alert('New Quality Control Audit authorized & compiled.');
          this.closeDrawer();
          this.loadInspections();
        },
        error: (err) => this.handleErrorLog(err),
      });
    }
  }

  private handleErrorLog(err: any) {
    console.error('Backend Payload Crash Log:', err);
    this.errorMessage =
      err.error?.message || err.message || '400 Bad Request: Structural mapping exception.';
    this.cdr.markForCheck();
  }

  edit(o: QCInspectionResponseModel) {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;
    this.inspection = {
      grnId: o.grnId,
      productId: o.productId,
      inspectionType: o.inspectionType,
      inspectedBy: o.inspectedBy,
      sampleSize: o.sampleSize,
      defectsFound: o.defectsFound,
      defectDescription: o.defectDescription || '',
      result: o.result,
      certificateRef: o.certificateRef || '',
      labTestReport: o.labTestReport || '',
      inspectedAt: o.inspectedAt,
      checklists: o.checklists
        ? o.checklists.map((c) => ({
            checkpointName: c.checkpointName,
            isPassed: c.isPassed,
            remarks: c.remarks,
          }))
        : [],
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm('Definitively remove this QC Record along with all its structural checklists?')) {
      this.service.delete(id).subscribe({
        next: () => {
          alert('QC Matrix node successfully pruned.');
          this.loadInspections();
        },
        error: (err) => alert(err.error?.message || err.message),
      });
    }
  }

  reset() {
    this.inspection = {
      grnId: 0,
      productId: 0,
      inspectionType: 'VISUAL',
      inspectedBy: this.currentUserId,
      sampleSize: 5,
      defectsFound: 0,
      defectDescription: '',
      result: 'GOOD',
      certificateRef: '',
      labTestReport: '',
      inspectedAt: '',
      checklists: [],
    };
    this.selectedFile = null;
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}
