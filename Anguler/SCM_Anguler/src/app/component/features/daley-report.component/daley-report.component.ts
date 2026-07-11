import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DailyReportResponseModel } from '../../shared/model/daley-report';
import { DailyReportService } from '../../../service/daley-report.service';
import { WarehouseService } from '../../../service/warehouse.service';
import { environment } from '../../../../environment/environment';

@Component({
  selector: 'app-daley-report.component',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './daley-report.component.html',
  styleUrl: './daley-report.component.css',
})
export class DailyReportComponent implements OnInit {

  reports: DailyReportResponseModel[] = [];
  warehouses: any[] = [];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentReportId: number | null = null;

  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null; 

  readonly imageBaseUrl = environment.apiUrl + "reports/";

  formModel = {
    warehouseId: '',
    reportDate: new Date().toISOString().split('T')[0],
    totalTasksDone: 0,
    issuesLogged: 0,
    summary: ''
  };

  constructor(
    private service: DailyReportService,
    private warehouseService: WarehouseService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadReports();
    this.loadWarehouses();
  }

  loadReports() {
    this.service.findAll().subscribe({
      next: (data) => { 
        this.reports = data || []; 
        this.cdr.markForCheck(); 
      },
      error: (err) => this.handleError(err)
    });
  }

  loadWarehouses() {
    this.warehouseService.getAll().subscribe({
      next: (data) => { 
        this.warehouses = data || []; 
        this.cdr.markForCheck(); 
      }
    });
  }


  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (!file) return;

    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
      this.cdr.markForCheck();
    };
    reader.readAsDataURL(file);
  }

  removeSelectedFile(fileInput: HTMLInputElement) {
    this.selectedFile = null;
    this.imagePreview = null;
    fileInput.value = '';
    this.cdr.markForCheck();
  }

  isImageAvailable(imageName: string | null | undefined): boolean {
    return !!imageName && imageName.trim().length > 0;
  }

  getImageUrl(imageName: string | null | undefined): string {
    return imageName ? `${this.imageBaseUrl}${imageName}` : '';
  }

  onImageError(event: Event): void {
    const target = event.target as HTMLImageElement | null;
    if (target) {
      target.style.display = 'none';
    }
  }

 
  submitReport() {
    this.errorMessage = null;

    if (!this.formModel.warehouseId) {
      this.errorMessage = "Validation Error: Target Warehouse Node is mandatory.";
      this.cdr.markForCheck();
      return;
    }

    const formData = new FormData();

    const reportDto = {
      warehouseId: this.formModel.warehouseId,
      reportDate: this.formModel.reportDate,
      totalTasksDone: this.formModel.totalTasksDone,
      issuesLogged: this.formModel.issuesLogged,
      summary: this.formModel.summary?.trim() || ''
    };

    formData.append(
      'report',
      new Blob([JSON.stringify(reportDto)], { type: 'application/json' })
    );
    
    // ফাইল পার্ট সংযুক্তিকরণ
    if (this.selectedFile) {
      formData.append('attachment', this.selectedFile);
    }

    if (this.isEdit && this.currentReportId) {
      this.service.update(this.currentReportId, formData).subscribe({
        next: () => { 
          alert("Daily operation metrics and image proof updated successfully."); 
          this.closeDrawer(); 
          this.loadReports(); 
        },
        error: (err) => this.handleError(err)
      });
    } else {
      this.service.create(formData).subscribe({
        next: (res) => { 
          alert(`EOD Report dispatched. Notified Authorities Count: ${res.notifiedAuthorities?.length || 0}`); 
          this.closeDrawer(); 
          this.loadReports(); 
        },
        error: (err) => this.handleError(err)
      });
    }
  }

  approveReport(id: number) {
    if (confirm("Are you sure you want to officially APPROVE and lock this operational report?")) {
      this.service.approve(id).subscribe({
        next: () => { 
          alert("Report stamped and locked as APPROVED."); 
          this.loadReports(); 
        },
        error: (err) => this.handleError(err)
      });
    }
  }

  edit(report: DailyReportResponseModel) {
    if (report.reportStatus === 'APPROVED') {
      alert("Access Denied! Approved EOD reports are locked permanently.");
      return;
    }
    
    this.isEdit = true;
    this.currentReportId = report.id;
    this.formModel = {
      warehouseId: report.warehouseId,
      reportDate: report.reportDate,
      totalTasksDone: report.totalTasksDone,
      issuesLogged: report.issuesLogged,
      summary: report.summary || ''
    };
    
    this.selectedFile = null;
    this.imagePreview = report.attachmentUrl ? this.getImageUrl(report.attachmentUrl) : null; 
    
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  deleteReport(id: number) {
    if (confirm("Terminate this report pointer from tracking grid?")) {
      this.service.delete(id).subscribe({
        next: () => { 
          alert("Report trace erased."); 
          this.cdr.markForCheck();
        },
        error: (err) => this.handleError(err)
      });
    }
  }

  openDrawer() { 
    this.resetForm(); 
    this.isEdit = false; 
    this.isDrawerOpen = true; 
    this.cdr.markForCheck();
  }

  closeDrawer() { 
    this.isDrawerOpen = false; 
    this.resetForm(); 
    this.cdr.markForCheck();
  }

  resetForm() {
    this.formModel = { 
      warehouseId: '', 
      reportDate: new Date().toISOString().split('T')[0], 
      totalTasksDone: 0, 
      issuesLogged: 0, 
      summary: '' 
    };
    this.selectedFile = null;
    this.imagePreview = null;
    this.isEdit = false;
    this.currentReportId = null;
    this.errorMessage = null;
  }

  private handleError(err: any) {
    this.errorMessage = err.error?.message || err.message || "EOD System Integration Interrupted.";
    this.cdr.markForCheck();
  }
}