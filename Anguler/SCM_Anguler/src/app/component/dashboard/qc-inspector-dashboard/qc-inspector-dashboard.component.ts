import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { QcInspectorService } from '../../../service/qc-inspactor.service';
import { LoginResponse } from '../../../auth/Model/authModel';
import { QcInspectionService } from '../../../service/qc-inspection.service';
import { NotificationService } from '../../../system/service/notification.service';
import { ActivityLogService } from '../../../service/activity.log.service';
import { GoodRecivedNoteService } from '../../../service/good-recived-note.service';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';
import { NotificationModel } from '../../../system/NotificationModel';
import { ActivityLogModel } from '../../../component/shared/model/ActivityLogModel';

@Component({
  selector: 'app-qc-inspector-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './qc-inspector-dashboard.component.html',
  styleUrls: ['./qc-inspector-dashboard.component.css'],
})
export class QCInspectorDashboardComponent implements OnInit {
  userName = '';
  userId!: number;
  qcInspector: any = null;
  user: LoginResponse | null = null;
  showSettings = false;
  loading = true;

  kpis = [
    {
      label: 'Pending Inspection',
      value: '0 Batches',
      trend: 0,
      trendDir: 'up',
      icon: 'bi-clipboard-pulse',
      color: 'purple',
    },
    {
      label: 'Passed Today',
      value: '0 Items',
      trend: 0,
      trendDir: 'up',
      icon: 'bi-check-circle',
      color: 'success',
    },
    {
      label: 'Defects Rate',
      value: '0%',
      trend: 0,
      trendDir: 'down',
      icon: 'bi-x-circle',
      color: 'danger',
    },
    {
      label: "Today's Audit",
      value: '0 Logs',
      trend: 0,
      trendDir: 'up',
      icon: 'bi-journal-check',
      color: 'info',
    },
  ];

  queue: any[] = [];
  defects: any[] = [];
  passRate = 0;
  failRate = 0;
  totalInspections = 0;
  passedCount = 0;
  failedCount = 0;

  notifications: NotificationModel[] = [];
  activities: ActivityLogModel[] = [];

  constructor(
    private storage: StorageService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private qcInspectorService: QcInspectorService,
    private inspectionService: QcInspectionService,
    private notificationService: NotificationService,
    private activityLogService: ActivityLogService,
    private grnService: GoodRecivedNoteService,
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name || 'QC Inspector';
    this.userId = user.userId;
    this.loadQcInspector();
    this.loadDashboardData();
    this.loadNotifications();
    this.loadActivityLogs();
    this.loadPendingGRNs();
  }

  loadDashboardData() {
    this.inspectionService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        this.totalInspections = all.length;
        const pending = all.filter((i: any) => i.result === 'PENDING' || i.status === 'PENDING');
        const passed = all.filter((i: any) => i.result === 'GOOD' || i.result === 'VERY_GOOD');
        const failed = all.filter((i: any) => i.result === 'BAD');
        const totalDefects = all.reduce((sum: number, i: any) => sum + (i.defectsFound || 0), 0);
        const totalSamples = all.reduce((sum: number, i: any) => sum + (i.sampleSize || 1), 0);

        this.passedCount = passed.length;
        this.failedCount = failed.length;

        if (this.totalInspections > 0) {
          this.passRate = parseFloat(((passed.length / this.totalInspections) * 100).toFixed(1));
          this.failRate = parseFloat(((failed.length / this.totalInspections) * 100).toFixed(1));
        }

        this.kpis[0] = {
          ...this.kpis[0],
          value: `${pending.length} Batches`,
          trend: pending.length,
          trendDir: pending.length > 0 ? 'up' : 'down',
        };
        this.kpis[1] = {
          ...this.kpis[1],
          value: `${passed.length} Items`,
          trend: passed.length,
          trendDir: 'up',
        };
        this.kpis[2] = {
          ...this.kpis[2],
          value: totalSamples > 0 ? `${((totalDefects / totalSamples) * 100).toFixed(1)}%` : '0%',
          trend: totalDefects,
          trendDir: totalDefects > 0 ? 'down' : 'up',
        };
        this.kpis[3] = {
          ...this.kpis[3],
          value: `${all.length} Logs`,
          trend: all.length,
          trendDir: 'up',
        };

        this.queue = all.slice(0, 5).map((i: any) => ({
          batchId: `B-${i.id}`,
          product: i.productName || `Product #${i.productId}`,
          sampleSize: i.sampleSize || 0,
          defectsFound: i.defectsFound || 0,
          status: i.result || 'PENDING',
        }));

        this.defects = failed.slice(0, 3).map((i: any, idx: number) => ({
          code: `DF-${String(idx + 1).padStart(2, '0')}`,
          description: i.defectDescription || 'No description',
          count: i.defectsFound || 0,
          severity: (i.defectsFound || 0) > 3 ? 'CRITICAL' : 'HIGH',
        }));

        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  loadPendingGRNs(): void {
    this.grnService.findAll().subscribe({
      next: (data) => {
        const grns = data || [];
        const pendingGRNs = grns.filter(
          (g: any) => g.status === 'PENDING' || g.qcStatus === 'PENDING' || g.qcStatus === null,
        );
        this.kpis[0] = {
          ...this.kpis[0],
          value: `${pendingGRNs.length} Batches`,
          trend: pendingGRNs.length,
          trendDir: pendingGRNs.length > 0 ? 'up' : 'down',
        };
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  loadNotifications(): void {
    this.notificationService.findAll().subscribe({
      next: (data) => {
        this.notifications = (data || []).slice(0, 5);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  loadActivityLogs(): void {
    this.activityLogService.findByModule('QC').subscribe({
      next: (data) => {
        this.activities = (data || []).slice(0, 5);
        this.cdr.markForCheck();
      },
      error: () => {
        this.activityLogService.findAll().subscribe({
          next: (data) => {
            this.activities = (data || []).slice(0, 5);
            this.cdr.markForCheck();
          },
          error: () => {},
        });
      },
    });
  }

  pass(batchId: string): void {
    const batch = this.queue.find((b) => b.batchId === batchId);
    if (batch) {
      batch.status = 'GOOD';
      batch.defectsFound = 0;
      this.passedCount++;
      this.recomputeRates();
      this.cdr.markForCheck();
    }
  }

  fail(batchId: string): void {
    const batch = this.queue.find((b) => b.batchId === batchId);
    if (batch) {
      batch.status = 'BAD';
      batch.defectsFound = 4;
      this.failedCount++;
      this.recomputeRates();
      this.cdr.markForCheck();
    }
  }

  private recomputeRates(): void {
    if (this.totalInspections > 0) {
      this.passRate = parseFloat(((this.passedCount / this.totalInspections) * 100).toFixed(1));
      this.failRate = parseFloat(((this.failedCount / this.totalInspections) * 100).toFixed(1));
    }
  }

  toggleSettings(): void {
    this.showSettings = !this.showSettings;
  }

  closeSettings(): void {
    this.showSettings = false;
  }

  getNotificationIcon(type: string): string {
    const icons: Record<string, string> = {
      SHIPMENT: 'bi-truck',
      TRIP_ALERT: 'bi-exclamation-triangle',
      REPORT_APPROVED: 'bi-check-circle',
      QC: 'bi-shield-check',
      GRN: 'bi-clipboard-check',
    };
    return icons[type] || 'bi-bell';
  }

  getNotificationColor(type: string): string {
    const colors: Record<string, string> = {
      SHIPMENT: 'text-primary',
      TRIP_ALERT: 'text-warning',
      REPORT_APPROVED: 'text-success',
      QC: 'text-purple',
      GRN: 'text-info',
    };
    return colors[type] || 'text-secondary';
  }

  getActionIcon(action: string): string {
    const icons: Record<string, string> = {
      CREATE: 'bi-plus-circle',
      UPDATE: 'bi-pencil',
      DELETE: 'bi-trash',
      LOGIN: 'bi-box-arrow-in-right',
    };
    return icons[action] || 'bi-circle';
  }

  getActionColor(action: string): string {
    const colors: Record<string, string> = {
      CREATE: 'text-success',
      UPDATE: 'text-primary',
      DELETE: 'text-danger',
      LOGIN: 'text-info',
    };
    return colors[action] || 'text-secondary';
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  loadQcInspector(): void {
    this.qcInspectorService.getQcInspectorByUserId(this.userId).subscribe({
      next: (res) => {
        this.qcInspector = res;
        this.storage.saveData(KEYS.QC_INSPECTOR, res);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['']);
  }
}
