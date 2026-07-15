import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { ManagerResponseModel } from '../../shared/model/manager';
import { ManagerService } from '../../../service/manager.service';
import { LoginResponse } from '../../../auth/Model/authModel';
import { PurchaseRequisitionService } from '../../../service/purchase-requisition.service';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { InvoiceService } from '../../../service/invoice.service';
import { WarehouseService } from '../../../service/warehouse.service';
import { DeliveryTripService } from '../../../service/delivery-trip.service';
import { QcInspectionService } from '../../../service/qc-inspection.service';
import { NotificationService } from '../../../system/service/notification.service';
import { ActivityLogService } from '../../../service/activity.log.service';
import { NotificationModel } from '../../../system/NotificationModel';
import { ActivityLogModel } from '../../shared/model/ActivityLogModel';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';

@Component({
  selector: 'app-manager-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './manager-dashboard.component.html',
  styleUrls: ['./manager-dashboard.component.css'],
})
export class ManagerDashboardComponent implements OnInit {
  userName = '';
  showSettings = false;
  isLoading = true;

  kpis = [
    { label: 'Total Revenue', value: '৳0', trend: 0, icon: 'bi-graph-up-arrow', color: 'success' },
    {
      label: 'Pending Approvals',
      value: '0',
      trend: 0,
      icon: 'bi-shield-exclamation',
      color: 'warning',
    },
    { label: 'Active Warehouses', value: '0', trend: 0, icon: 'bi-building', color: 'info' },
    { label: 'Total Invoices', value: '0', trend: 0, icon: 'bi-receipt', color: 'primary' },
  ];

  totalRevenue = 0;
  totalExpenses = 0;
  pendingApprovalsCount = 0;
  warehouseCount = 0;

  approvals: any[] = [];
  notifications: NotificationModel[] = [];
  activities: ActivityLogModel[] = [];

  deptPerformance = [
    { name: 'Sourcing & SCM', score: 0, color: 'success' },
    { name: 'Logistics & Fleet', score: 0, color: 'primary' },
    { name: 'Quality Control', score: 0, color: 'purple' },
    { name: 'Commercial Imports', score: 0, color: 'warning' },
  ];

  chartMonths: string[] = [];
  chartRevenue: number[] = [];
  chartExpenses: number[] = [];
  chartMax = 1;

  userId!: number;
  manager: ManagerResponseModel | null = null;
  user: LoginResponse | null = null;

  constructor(
    private storage: StorageService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private managerService: ManagerService,
    private prService: PurchaseRequisitionService,
    private poService: PurchaseOrderService,
    private invoiceService: InvoiceService,
    private warehouseService: WarehouseService,
    private deliveryTripService: DeliveryTripService,
    private qcService: QcInspectionService,
    private notificationService: NotificationService,
    private activityLogService: ActivityLogService,
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name;
    this.userId = user.userId;
    this.loadManager();
    this.loadAllData();
  }

  loadAllData(): void {
    this.isLoading = true;
    let completed = 0;
    const totalCalls = 7;
    const checkDone = () => {
      completed++;
      if (completed >= totalCalls) {
        this.isLoading = false;
        this.buildChartData();
        this.cdr.markForCheck();
      }
    };

    this.loadPurchaseRequisitions(checkDone);
    this.loadPurchaseOrders(checkDone);
    this.loadInvoices(checkDone);
    this.loadWarehouses(checkDone);
    this.loadDeliveryTrips(checkDone);
    this.loadQcInspections(checkDone);
    this.loadNotifications(checkDone);
    this.loadActivityLogs();
  }

  loadPurchaseRequisitions(done: () => void): void {
    this.prService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        const pending = all.filter((r: any) => r.approvalStatus === 'PENDING');
        const approved = all.filter((r: any) => r.approvalStatus === 'APPROVED');
        this.pendingApprovalsCount = pending.length;
        this.kpis[1] = {
          label: 'Pending Approvals',
          value: `${pending.length}`,
          trend: 0,
          icon: 'bi-shield-exclamation',
          color: 'warning',
        };
        this.approvals = pending.slice(0, 5).map((r: any) => ({
          id: r.id,
          type: 'Purchase Requisition',
          requester: r.requestedByName || r.requestedBy || 'System',
          amount: r.quantityRequired || 0,
          date: r.requiredByDate || 'N/A',
        }));
        const sourcingScore = all.length > 0 ? Math.round((approved.length / all.length) * 100) : 0;
        this.deptPerformance[0].score = sourcingScore;
        this.cdr.markForCheck();
        done();
      },
      error: () => done(),
    });
  }

  loadPurchaseOrders(done: () => void): void {
    this.poService.findAll().subscribe({
      next: (data) => {
        const orders = data || [];
        this.totalExpenses = orders.reduce((sum: number, o: any) => sum + (o.totalAmount || 0), 0);
        this.cdr.markForCheck();
        done();
      },
      error: () => done(),
    });
  }

  loadInvoices(done: () => void): void {
    this.invoiceService.findAll().subscribe({
      next: (data) => {
        const invoices = data || [];
        const issued = invoices.filter((inv: any) => inv.invoiceStatus === 'ISSUED');
        this.totalRevenue = invoices
          .filter((inv: any) => inv.invoiceStatus === 'ISSUED')
          .reduce((sum: number, inv: any) => sum + (inv.totalAmount || 0), 0);
        this.kpis[0] = {
          label: 'Total Revenue',
          value: `৳${this.formatNumber(this.totalRevenue)}`,
          trend: 0,
          icon: 'bi-graph-up-arrow',
          color: 'success',
        };
        this.kpis[3] = {
          label: 'Total Invoices',
          value: `${invoices.length}`,
          trend: 0,
          icon: 'bi-receipt',
          color: 'primary',
        };
        const commercialScore =
          invoices.length > 0 ? Math.round((issued.length / invoices.length) * 100) : 0;
        this.deptPerformance[3].score = commercialScore;
        this.cdr.markForCheck();
        done();
      },
      error: () => done(),
    });
  }

  loadWarehouses(done: () => void): void {
    this.warehouseService.getAll().subscribe({
      next: (data) => {
        const warehouses = data || [];
        this.warehouseCount = warehouses.length;
        this.kpis[2] = {
          label: 'Active Warehouses',
          value: `${this.warehouseCount}`,
          trend: 0,
          icon: 'bi-building',
          color: 'info',
        };
        this.cdr.markForCheck();
        done();
      },
      error: () => done(),
    });
  }

  loadDeliveryTrips(done: () => void): void {
    this.deliveryTripService.findAll().subscribe({
      next: (data) => {
        const trips = data || [];
        const delivered = trips.filter((t: any) => t.status === 'DELIVERED');
        const logisticsScore =
          trips.length > 0 ? Math.round((delivered.length / trips.length) * 100) : 0;
        this.deptPerformance[1].score = logisticsScore;
        this.cdr.markForCheck();
        done();
      },
      error: () => done(),
    });
  }

  loadQcInspections(done: () => void): void {
    this.qcService.findAll().subscribe({
      next: (data) => {
        const inspections = data || [];
        const passed = inspections.filter(
          (i: any) => i.result === 'GOOD' || i.result === 'VERY_GOOD',
        );
        const qcScore =
          inspections.length > 0 ? Math.round((passed.length / inspections.length) * 100) : 0;
        this.deptPerformance[2].score = qcScore;
        this.cdr.markForCheck();
        done();
      },
      error: () => done(),
    });
  }

  loadNotifications(done: () => void): void {
    this.notificationService.findAll().subscribe({
      next: (data) => {
        this.notifications = (data || []).slice(0, 5);
        this.cdr.markForCheck();
        done();
      },
      error: () => done(),
    });
  }

  loadActivityLogs(): void {
    this.activityLogService.findAll().subscribe({
      next: (data) => {
        this.activities = (data || []).slice(0, 5);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  buildChartData(): void {
    const monthNames = [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec',
    ];
    const now = new Date();
    const months: string[] = [];
    const revMap: Record<string, number> = {};
    const expMap: Record<string, number> = {};

    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
      months.push(monthNames[d.getMonth()]);
      revMap[key] = 0;
      expMap[key] = 0;
    }

    this.invoiceService.findAll().subscribe({
      next: (invoices) => {
        (invoices || []).forEach((inv: any) => {
          if (inv.invoiceStatus === 'ISSUED' && inv.issuedAt) {
            const d = new Date(inv.issuedAt);
            const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
            if (revMap[key] !== undefined) revMap[key] += inv.totalAmount || 0;
          }
        });

        this.poService.findAll().subscribe({
          next: (orders) => {
            (orders || []).forEach((po: any) => {
              if (po.createdAt) {
                const d = new Date(po.createdAt);
                const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
                if (expMap[key] !== undefined) expMap[key] += po.totalAmount || 0;
              }
            });

            this.chartMonths = months;
            this.chartRevenue = months.map((_, i) => {
              const key = Object.keys(revMap)[i];
              return revMap[key] || 0;
            });
            this.chartExpenses = months.map((_, i) => {
              const key = Object.keys(expMap)[i];
              return expMap[key] || 0;
            });
            this.chartMax = Math.max(...this.chartRevenue, ...this.chartExpenses, 1);
            this.cdr.markForCheck();
          },
        });
      },
    });
  }

  formatNumber(num: number): string {
    if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
    if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
    return num.toLocaleString();
  }

  getChartY(value: number): number {
    return 160 - (value / this.chartMax) * 140;
  }

  loadManager(): void {
    this.managerService.getManagerByUserId(this.userId).subscribe({
      next: (res) => {
        this.manager = res;
        this.storage.saveData(KEYS.MANAGER, res);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  approve(id: string): void {
    this.prService.approve(+id).subscribe({
      next: () => {
        this.approvals = this.approvals.filter((a) => a.id !== id);
        this.pendingApprovalsCount = Math.max(0, this.pendingApprovalsCount - 1);
        this.kpis[1].value = `${this.pendingApprovalsCount}`;
        this.cdr.markForCheck();
      },
      error: (err) => alert(err.error?.message || 'Approval failed'),
    });
  }

  reject(id: string): void {
    this.prService.rejectOrCancel(+id, 'REJECT').subscribe({
      next: () => {
        this.approvals = this.approvals.filter((a) => a.id !== id);
        this.pendingApprovalsCount = Math.max(0, this.pendingApprovalsCount - 1);
        this.kpis[1].value = `${this.pendingApprovalsCount}`;
        this.cdr.markForCheck();
      },
      error: (err) => alert(err.error?.message || 'Rejection failed'),
    });
  }

  getActionIcon(action: string): string {
    const map: Record<string, string> = {
      CREATE: 'bi-plus-circle text-success',
      UPDATE: 'bi-pencil-square text-primary',
      DELETE: 'bi-trash text-danger',
      LOGIN: 'bi-box-arrow-in-right text-info',
    };
    return map[action] || 'bi-circle text-secondary';
  }

  getTimeAgo(dateStr: string): string {
    if (!dateStr) return '';
    const diff = Date.now() - new Date(dateStr).getTime();
    const mins = Math.floor(diff / 60000);
    if (mins < 1) return 'Just now';
    if (mins < 60) return `${mins}m ago`;
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) return `${hrs}h ago`;
    const days = Math.floor(hrs / 24);
    return `${days}d ago`;
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['']);
  }
}
