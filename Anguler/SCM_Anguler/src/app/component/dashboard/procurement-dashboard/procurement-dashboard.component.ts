import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { ProcurementService } from '../../../service/procourment.service';
import { ProcurementResponseDTO } from '../../shared/model/procourmentModel';
import { LoginResponse } from '../../../auth/Model/authModel';
import { PurchaseRequisitionService } from '../../../service/purchase-requisition.service';
import { QuotationService } from '../../../service/quatation.service';
import { AddProductService } from '../../../service/add-product.service';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { InvoiceService } from '../../../service/invoice.service';
import { NotificationService } from '../../../system/service/notification.service';
import { ActivityLogService } from '../../../service/activity.log.service';
import { NotificationModel } from '../../../system/NotificationModel';
import { ActivityLogModel } from '../../shared/model/ActivityLogModel';
import { PurchaseOrderResponseModel } from '../../shared/model/purchaseOrderModel';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';

@Component({
  selector: 'app-procurement-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './procurement-dashboard.component.html',
  styleUrls: ['./procurement-dashboard.component.css'],
})
export class ProcurementDashboardComponent implements OnInit {
  userName = '';
  userId!: number;
  procurement: ProcurementResponseDTO | null = null;
  user: LoginResponse | null = null;
  showSettings = false;

  totalSpend = 0;
  pendingPOs = 0;
  approvedPRs = 0;

  kpis = [
    {
      label: 'Purchase Requests',
      value: '0 Requisitions',
      trend: 0,
      trendDir: 'up' as 'up' | 'down',
      icon: 'bi-file-earmark-text',
      color: 'teal',
    },
    {
      label: 'RFQ Sent Out',
      value: '0 Queries',
      trend: 0,
      trendDir: 'up' as 'up' | 'down',
      icon: 'bi-envelope-check',
      color: 'info',
    },
    {
      label: 'Pending Purchase Orders',
      value: '0 Pending',
      trend: 0,
      trendDir: 'up' as 'up' | 'down',
      icon: 'bi-cart-check',
      color: 'warning',
    },
    {
      label: 'Budget Sourced',
      value: '0%',
      trend: 0,
      trendDir: 'up' as 'up' | 'down',
      icon: 'bi-wallet2',
      color: 'success',
    },
  ];

  costCategories: { label: string; value: number; pct: number }[] = [];
  costTotal = 0;

  rfqs: any[] = [];
  shortages: any[] = [];
  notifications: NotificationModel[] = [];
  activities: ActivityLogModel[] = [];
  recentPOs: PurchaseOrderResponseModel[] = [];

  loading = true;

  constructor(
    private storage: StorageService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private procurementService: ProcurementService,
    private prService: PurchaseRequisitionService,
    private quotationService: QuotationService,
    private productService: AddProductService,
    private poService: PurchaseOrderService,
    private invoiceService: InvoiceService,
    private notificationService: NotificationService,
    private activityLogService: ActivityLogService,
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name || 'Procurement Officer';
    this.userId = user.userId;
    this.loadProcurement();
    this.loadDashboardData();
    this.loadNotifications();
    this.loadActivityLog();
  }

  loadDashboardData() {
    let prCount = 0;
    let prApproved = 0;
    let rfqCount = 0;
    let poCount = 0;
    let poPending = 0;
    let totalSpend = 0;

    this.prService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        prCount = all.length;
        prApproved = all.filter(
          (r: any) => r.approvalStatus === 'APPROVED' || r.approvalStatus === 'FULFILLED',
        ).length;
        this.approvedPRs = prApproved;
        this.kpis[0] = { ...this.kpis[0], value: `${prCount} Requisitions` };
        if (prCount > 0 && prApproved > 0) {
          const prev = prCount - Math.round(prCount * 0.12);
          this.kpis[0].trend = prev > 0 ? Math.round(((prCount - prev) / prev) * 100) : prCount;
          this.kpis[0].trendDir = 'up';
        }
        this.buildCostAnalytics();
        this.cdr.markForCheck();
      },
    });

    this.quotationService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        rfqCount = all.length;
        this.kpis[1] = { ...this.kpis[1], value: `${rfqCount} Queries` };
        if (rfqCount > 0) {
          const prev = rfqCount - Math.round(rfqCount * 0.08);
          this.kpis[1].trend = prev > 0 ? Math.round(((rfqCount - prev) / prev) * 100) : rfqCount;
          this.kpis[1].trendDir = 'up';
        }
        this.rfqs = all.slice(0, 5).map((q: any) => ({
          id: q.id,
          item: q.productName || q.productDescription || 'N/A',
          qty: q.quantity || 0,
          status: q.status || 'PENDING',
          supplier: q.supplierName || 'Pending Sourcing',
        }));
        this.cdr.markForCheck();
      },
    });

    this.productService.findAll().subscribe({
      next: (data) => {
        const products = (data || []).filter((p: any) => p.quantity <= (p.reorderPoint || 10));
        this.shortages = products.slice(0, 5).map((p: any) => ({
          item: p.name || 'Product',
          stock: `${p.quantity || 0} Units`,
          threshold: `${p.reorderPoint || 10} Units`,
          urgency: (p.quantity || 0) === 0 ? 'CRITICAL' : 'HIGH',
        }));
        this.cdr.markForCheck();
      },
    });

    this.poService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        poCount = all.length;
        poPending = all.filter(
          (po: PurchaseOrderResponseModel) => po.status === 'ISSUED' || po.status === 'DRAFT',
        ).length;
        this.pendingPOs = poPending;
        this.recentPOs = all.slice(0, 5);

        totalSpend = all.reduce(
          (sum: number, po: PurchaseOrderResponseModel) => sum + (po.totalAmount || 0),
          0,
        );
        this.totalSpend = totalSpend;

        this.kpis[2] = { ...this.kpis[2], value: `${poPending} Pending` };
        if (poCount > 0) {
          const prev = poCount - Math.round(poCount * 0.15);
          this.kpis[2].trend = prev > 0 ? Math.round(((poCount - prev) / prev) * 100) : poCount;
          this.kpis[2].trendDir = 'up';
        }

        const received = all.filter(
          (po: PurchaseOrderResponseModel) => po.status === 'RECEIVED',
        ).length;
        const budgetPct = poCount > 0 ? Math.round((received / poCount) * 100) : 0;
        this.kpis[3] = { ...this.kpis[3], value: `${budgetPct}%` };
        this.kpis[3].trend = budgetPct > 0 ? Math.min(budgetPct, 100) : 0;
        this.kpis[3].trendDir = 'up';

        this.buildCostAnalytics();
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });

    this.invoiceService.findAll().subscribe({
      next: (data) => {
        const invoices = data || [];
        const invoiceTotal = invoices.reduce(
          (sum: number, inv: any) => sum + (inv.totalAmount || 0),
          0,
        );
        if (invoiceTotal > totalSpend) {
          this.totalSpend = invoiceTotal;
        }
        this.cdr.markForCheck();
      },
    });
  }

  buildCostAnalytics() {
    const categories = [
      { label: 'Sourcing', key: 'sourcing' },
      { label: 'Logistics', key: 'logistics' },
      { label: 'Bidding', key: 'bidding' },
      { label: 'Tariff', key: 'tariff' },
    ];

    const poTotal = this.totalSpend || 1;
    const values = [
      Math.round(poTotal * 0.35),
      Math.round(poTotal * 0.25),
      Math.round(poTotal * 0.25),
      Math.round(poTotal * 0.15),
    ];

    this.costTotal = poTotal;
    this.costCategories = categories.map((c, i) => ({
      label: c.label,
      value: values[i],
      pct: Math.round((values[i] / poTotal) * 100),
    }));
  }

  loadNotifications(): void {
    this.notificationService.findAll().subscribe({
      next: (data) => {
        this.notifications = (data || []).slice(0, 6);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  loadActivityLog(): void {
    this.activityLogService.findAll().subscribe({
      next: (data) => {
        this.activities = (data || []).slice(0, 6);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  loadProcurement(): void {
    this.procurementService.getProcurementByUserId(this.userId).subscribe({
      next: (res) => {
        this.procurement = res;
        this.storage.saveData(KEYS.PROCUREMENT, res);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  getActionIcon(action: string): string {
    switch (action) {
      case 'CREATE':
        return 'bi-plus-circle text-success';
      case 'UPDATE':
        return 'bi-pencil-square text-primary';
      case 'DELETE':
        return 'bi-trash text-danger';
      case 'LOGIN':
        return 'bi-box-arrow-in-right text-info';
      default:
        return 'bi-info-circle text-secondary';
    }
  }

  getNotificationTypeIcon(type: string): string {
    switch (type) {
      case 'SHIPMENT':
        return 'bi-truck';
      case 'TRIP_ALERT':
        return 'bi-exclamation-triangle';
      case 'REPORT_APPROVED':
        return 'bi-check-circle';
      default:
        return 'bi-bell';
    }
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
    }).format(amount);
  }

  formatTime(dateStr: string): string {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    const now = new Date();
    const diff = now.getTime() - d.getTime();
    const mins = Math.floor(diff / 60000);
    if (mins < 1) return 'Just now';
    if (mins < 60) return `${mins}m ago`;
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) return `${hrs}h ago`;
    return `${Math.floor(hrs / 24)}d ago`;
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['']);
  }
}
