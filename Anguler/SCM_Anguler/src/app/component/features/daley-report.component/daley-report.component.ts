import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DailyReportResponseModel } from '../../shared/model/daley-report';
import { DailyReportService } from '../../../service/daley-report.service';

@Component({
  selector: 'app-daley-report.component',
  imports: [CommonModule, FormsModule],
  templateUrl: './daley-report.component.html',
  styleUrl: './daley-report.component.css',
})
  export class DailyReportComponent implements OnInit {

 reports: DailyReportResponseModel[] = [];
  warehouses: string[] = ['WH-DHAKA-01', 'WH-CHITTAGONG-02', 'WH-SYLHET-03'];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentReportId: number | null = null;

  // 🎯 ইমেজ ফাইলের লোকাল মেমোরি স্টেট
  selectedFile: File | null = null;

  // UI বাইন্ডিং মডেল (রিকোয়েস্ট মডেল ইন্টারফেস থেকে 'attachment' বাদ দিয়ে ফ্রন্টএন্ড ফর্ম অবজেক্ট জেনারেট করা হলো)
  formModel = {
    warehouseId: '',
    reportDate: new Date().toISOString().split('T')[0],
    totalTasksDone: 0,
    issuesLogged: 0,
    summary: ''
  };

  constructor(
    private service: DailyReportService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadReports();
  }

  loadReports() {
    this.service.findAll().subscribe({
      next: (data) => { this.reports = data || []; this.cdr.markForCheck(); },
      error: (err) => this.handleError(err)
    });
  }

  // 🎯 ফাইলে সিলেক্ট চেঞ্জ ইভেন্ট ট্র্যাকার
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  submitReport() {
    this.errorMessage = null;

    if (!this.formModel.warehouseId) {
      this.errorMessage = "Validation Error: Target Warehouse Node is mandatory.";
      this.cdr.markForCheck();
      return;
    }

    // 🎯 ফিক্স: ব্যাকএন্ড @ModelAttribute এর সাথে সিঙ্ক করতে FormData অবজেক্ট বিল্ড করা হলো
    const formData = new FormData();
    formData.append('warehouseId', this.formModel.warehouseId);
    formData.append('reportDate', this.formModel.reportDate);
    formData.append('totalTasksDone', this.formModel.totalTasksDone.toString());
    formData.append('issuesLogged', this.formModel.issuesLogged.toString());
    formData.append('summary', this.formModel.summary?.trim() || '');
    
    if (this.selectedFile) {
      formData.append('attachment', this.selectedFile); // ব্যাকএন্ড রিকোয়েস্ট DTO-র 'attachment' ফিল্ড নেম
    }

    if (this.isEdit && this.currentReportId) {
      this.service.update(this.currentReportId, formData).subscribe({
        next: () => { alert("Daily operation metrics and image proof updated."); this.closeDrawer(); this.loadReports(); },
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
        next: () => { alert("Report stamped and locked as APPROVED."); this.loadReports(); },
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
    this.selectedFile = null; // ওল্ড ফাইল স্টেট ক্লিন
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  deleteReport(id: number) {
    if (confirm("Terminate this report pointer from tracking grid?")) {
      this.service.delete(id).subscribe({
        next: () => { alert("Report trace erased."); this.loadReports(); },
        error: (err) => this.handleError(err)
      });
    }
  }

  openDrawer() { this.resetForm(); this.isDrawerOpen = true; }
  closeDrawer() { this.isDrawerOpen = false; this.resetForm(); }

  resetForm() {
    this.formModel = { warehouseId: '', reportDate: new Date().toISOString().split('T')[0], totalTasksDone: 0, issuesLogged: 0, summary: '' };
    this.selectedFile = null;
    this.isEdit = false;
    this.currentReportId = null;
    this.errorMessage = null;
  }

  private handleError(err: any) {
    this.errorMessage = err.error?.message || err.message || "EOD System Integration Interrupted.";
    this.cdr.markForCheck();
  }
}

