import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { SalesOfficerService } from '../../../service/sales-officer.service';
import { SalesOfficerResponseDTO } from '../../shared/model/salesOfficerModel';
import { LoginResponse } from '../../../auth/Model/authModel';
import { CustomerOrderService } from '../../../service/customer-order.service';
import { CustomerService } from '../../../service/customer.service';
import { QuotationService } from '../../../service/quatation.service';
import { InvoiceService } from '../../../service/invoice.service';
import { NotificationService } from '../../../system/service/notification.service';
import { ActivityLogService } from '../../../service/activity.log.service';
import { NotificationModel } from '../../../system/NotificationModel';
import { ActivityLogModel } from '../../shared/model/ActivityLogModel';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';

@Component({
  selector: 'app-sales-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './sales-dashboard.component.html',
  styleUrls: ['./sales-dashboard.component.css'],
})
export class SalesDashboardComponent implements OnInit {
  userName = '';
  userId!: number;
  salesOfficer: SalesOfficerResponseDTO | null = null;
  user: LoginResponse | null = null;
  showSettings = false;
  isLoading = true;

  totalRevenue = 0;
  conversionRate = 0;
  pendingQuotes = 0;

  kpis = [
    { label: 'Total Revenue', value: '৳0', trend: 0, icon: 'bi-cash-coin', color: 'success' },
    { label: 'Pipeline Leads', value: '0 Active', trend: 0, icon: 'bi-funnel', color: 'primary' },
    { label: 'Conversion Rate', value: '0%', trend: 0, icon: 'bi-graph-up', color: 'info' },
    {
      label: 'Pending Quotations',
      value: '0 Quotes',
      trend: 0,
      icon: 'bi-file-earmark-medical',
      color: 'warning',
    },
  ];

  leads: any[] = [];
  customers: any[] = [];
  notifications: NotificationModel[] = [];
  activities: ActivityLogModel[] = [];

  private ordersLoaded = false;
  private customersLoaded = false;
  private orders: any[] = [];

  constructor(
    private storage: StorageService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private salesOfficerService: SalesOfficerService,
    private orderService: CustomerOrderService,
    private customerService: CustomerService,
    private quotationService: QuotationService,
    private invoiceService: InvoiceService,
    private notificationService: NotificationService,
    private activityLogService: ActivityLogService,
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name || 'Sales Officer';
    this.userId = user.userId;
    this.loadSalesOfficer();
    this.loadAllData();
  }

  loadAllData(): void {
    this.isLoading = true;
    let completed = 0;
    const totalCalls = 5;
    const checkDone = () => {
      completed++;
      if (completed >= totalCalls) {
        this.isLoading = false;
        this.buildCustomerStats();
        this.cdr.markForCheck();
      }
    };

    this.loadOrders(checkDone);
    this.loadQuotations(checkDone);
    this.loadInvoices(checkDone);
    this.loadNotifications(checkDone);
    this.loadActivityLogs(checkDone);
    this.loadCustomers();
  }

  loadOrders(done: () => void): void {
    this.orderService.findAll().subscribe({
      next: (data) => {
        this.orders = data || [];
        this.ordersLoaded = true;
        const completed = this.orders.filter((o: any) => o.status === 'DELIVERED').length;
        const orderCount = this.orders.length;
        this.conversionRate = orderCount > 0 ? Math.round((completed / orderCount) * 100) : 0;
        this.kpis[2] = {
          label: 'Conversion Rate',
          value: `${this.conversionRate}%`,
          trend: 0,
          icon: 'bi-graph-up',
          color: 'info',
        };
        this.cdr.markForCheck();
        done();
      },
      error: () => done(),
    });
  }

  loadQuotations(done: () => void): void {
    this.quotationService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        const pending = all.filter(
          (q: any) => q.status === 'PENDING' || q.status === 'UNDER_REVIEW',
        );
        this.pendingQuotes = pending.length;
        this.kpis[3] = {
          label: 'Pending Quotations',
          value: `${this.pendingQuotes} Quotes`,
          trend: 0,
          icon: 'bi-file-earmark-medical',
          color: 'warning',
        };

        const totalQuoted = all.reduce((sum: number, q: any) => sum + (q.totalPrice || 0), 0);
        this.kpis[1] = {
          label: 'Pipeline Leads',
          value: `${all.length} Active`,
          trend: 0,
          icon: 'bi-funnel',
          color: 'primary',
        };

        this.leads = all.slice(0, 10).map((q: any) => ({
          company: q.supplierName || 'Unknown Supplier',
          contact: 'N/A',
          value: q.totalPrice || 0,
          stage: this.mapQuotationStage(q.status),
          probability: this.computeProbability(q.status),
          status: q.status,
        }));

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
        this.totalRevenue = invoices
          .filter((inv: any) => inv.invoiceStatus === 'ISSUED')
          .reduce((sum: number, inv: any) => sum + (inv.totalAmount || 0), 0);
        this.kpis[0] = {
          label: 'Total Revenue',
          value: `৳${this.formatNumber(this.totalRevenue)}`,
          trend: 0,
          icon: 'bi-cash-coin',
          color: 'success',
        };
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

  loadActivityLogs(done: () => void): void {
    this.activityLogService.findAll().subscribe({
      next: (data) => {
        this.activities = (data || []).slice(0, 5);
        this.cdr.markForCheck();
        done();
      },
      error: () => done(),
    });
  }

  loadCustomers(): void {
    this.customerService.getAll().subscribe({
      next: (data) => {
        this.customers = (data || []).slice(0, 5).map((c: any) => ({
          id: c.id || c.userId,
          name: c.name || 'Customer',
          contact: c.phone || 'N/A',
          ordersCount: 0,
          spent: 0,
        }));
        this.customersLoaded = true;
        this.buildCustomerStats();
        this.cdr.markForCheck();
      },
    });
  }

  buildCustomerStats(): void {
    if (!this.customersLoaded || !this.ordersLoaded) {
      return;
    }
    this.customers.forEach((c: any) => {
      const customerOrders = this.orders.filter((o: any) => o.customerId === c.id);
      c.ordersCount = customerOrders.length;
      c.spent = customerOrders.reduce(
        (sum: number, o: any) => sum + (o.totalAmount || o.codAmount || 0),
        0,
      );
    });
    this.customers.sort((a: any, b: any) => b.spent - a.spent);
    this.cdr.markForCheck();
  }

  mapQuotationStage(status: string): string {
    const stageMap: Record<string, string> = {
      PENDING: 'Prospecting',
      UNDER_REVIEW: 'Qualification',
      APPROVED: 'Proposal',
      REJECTED: 'Closed Lost',
      EXPIRED: 'Closed Lost',
    };
    return stageMap[status] || 'Prospecting';
  }

  computeProbability(status: string): string {
    const probMap: Record<string, string> = {
      PENDING: '20%',
      UNDER_REVIEW: '40%',
      APPROVED: '80%',
      REJECTED: '0%',
      EXPIRED: '0%',
    };
    return probMap[status] || '10%';
  }

  formatNumber(num: number): string {
    if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
    if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
    return num.toLocaleString();
  }

  getStageBadgeClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'bg-warning-subtle text-warning',
      UNDER_REVIEW: 'bg-info-subtle text-info',
      APPROVED: 'bg-success-subtle text-success',
      REJECTED: 'bg-danger-subtle text-danger',
      EXPIRED: 'bg-secondary-subtle text-secondary',
    };
    return map[status] || 'bg-secondary-subtle text-secondary';
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

  loadSalesOfficer(): void {
    this.salesOfficerService.getSalesOfficerByUserId(this.userId).subscribe({
      next: (res) => {
        this.salesOfficer = res;
        this.storage.saveData(KEYS.SALES_OFFICER, res);
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
