import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { SupplierService } from '../../../service/supplier.service';
import { SupplierResponseDTO } from '../../shared/model/supplierModel';
import { LoginResponse } from '../../../auth/Model/authModel';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { PurchaseOrderResponseModel } from '../../shared/model/purchaseOrderModel';
import { QuotationService } from '../../../service/quatation.service';
import { QuotationResponseModel } from '../../shared/model/quatationModel';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';
import { NotificationService } from '../../../system/service/notification.service';
import { NotificationModel } from '../../../system/NotificationModel';
import { ActivityLogService } from '../../../service/activity.log.service';
import { ActivityLogModel } from '../../shared/model/ActivityLogModel';
import { InvoiceService } from '../../../service/invoice.service';
import { InvoiceResponseModel } from '../../shared/model/invoiceModel';

@Component({
  selector: 'app-supplier-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './supplier-dashboard.component.html',
  styleUrls: ['./supplier-dashboard.component.css'],
})
export class SupplierDashboardComponent implements OnInit {
  userName = '';
  userId!: number;
  supplier: SupplierResponseDTO | null = null;
  user: LoginResponse | null = null;

  kpis = [
    { label: 'Purchase Orders', value: '0 POs', trend: 0, icon: 'bi-inboxes', color: 'primary' },
    {
      label: 'Pending Delivery',
      value: '0 Consignments',
      trend: 0,
      icon: 'bi-truck-flatbed',
      color: 'warning',
    },
    {
      label: 'Outstanding Payments',
      value: '৳0',
      trend: 0,
      icon: 'bi-currency-exchange',
      color: 'success',
    },
    { label: 'Supply Accuracy', value: '0%', trend: 0, icon: 'bi-patch-check', color: 'success' },
  ];

  pos: any[] = [];
  rfqs: any[] = [];
  notifications: NotificationModel[] = [];
  activities: ActivityLogModel[] = [];

  showSettings = false;
  loading = true;

  outstandingPayments = 0;
  supplyAccuracy = 0;
  pendingDeliveries = 0;

  constructor(
    private storage: StorageService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private supplierService: SupplierService,
    private poService: PurchaseOrderService,
    private quotationService: QuotationService,
    private notificationService: NotificationService,
    private activityLogService: ActivityLogService,
    private invoiceService: InvoiceService,
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name || 'Supplier Node';
    this.userId = user.userId;
    this.loadSupplier();
    this.loadDashboardData();
    this.loadNotifications();
    this.loadActivities();
  }

  loadDashboardData() {
    this.loading = true;

    this.poService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        const delivered = all.filter((o: PurchaseOrderResponseModel) => o.status === 'RECEIVED');
        this.pendingDeliveries = all.filter(
          (o: PurchaseOrderResponseModel) => o.status === 'ISSUED',
        ).length;

        if (all.length > 0) {
          this.supplyAccuracy = Math.round((delivered.length / all.length) * 100);
        }

        this.kpis[0] = { ...this.kpis[0], value: `${all.length} POs` };
        this.kpis[1] = { ...this.kpis[1], value: `${this.pendingDeliveries} Consignments` };
        this.kpis[3] = { ...this.kpis[3], value: `${this.supplyAccuracy}%` };

        this.pos = all.slice(0, 5).map((o: PurchaseOrderResponseModel) => ({
          poNumber: o.poNumber || `PO-${o.id}`,
          date: o.createdAt || 'N/A',
          amount: o.totalAmount || 0,
          deliveryDue: o.expectedDeliveryDate || 'N/A',
          status: o.status || 'DRAFT',
        }));
        this.cdr.markForCheck();
      },
    });

    this.quotationService.findAll().subscribe({
      next: (data) => {
        this.rfqs = (data || []).slice(0, 3).map((q: QuotationResponseModel) => ({
          id: q.quotationNumber || `RFQ-${q.id}`,
          item: q.productName || 'N/A',
          closingDate: q.validUntil || 'N/A',
          status: q.status || 'PENDING',
        }));
        this.cdr.markForCheck();
      },
    });

    this.invoiceService.findAll().subscribe({
      next: (data) => {
        const invoices = data || [];
        this.outstandingPayments = invoices.reduce(
          (sum: number, inv: InvoiceResponseModel) => sum + (inv.dueAmount || 0),
          0,
        );
        this.kpis[2] = { ...this.kpis[2], value: `৳${this.outstandingPayments.toLocaleString()}` };
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
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

  loadActivities(): void {
    this.activityLogService.findByUserId(this.userId.toString()).subscribe({
      next: (data) => {
        this.activities = (data || []).slice(0, 5);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  getActionIcon(action: string): string {
    const icons: Record<string, string> = {
      CREATE: 'bi-plus-circle text-success',
      UPDATE: 'bi-pencil-square text-primary',
      DELETE: 'bi-trash text-danger',
      LOGIN: 'bi-box-arrow-in-right text-info',
    };
    return icons[action] || 'bi-clock text-secondary';
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      DRAFT: 'bg-secondary text-white',
      ISSUED: 'bg-primary text-white',
      PARTIALLY_RECEIVED: 'bg-info text-white',
      RECEIVED: 'bg-success text-white',
      CANCELLED: 'bg-danger text-white',
    };
    return map[status] || 'bg-secondary text-white';
  }

  getRfqStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'bg-warning text-dark',
      UNDER_REVIEW: 'bg-info text-dark',
      APPROVED: 'bg-success text-white',
      REJECTED: 'bg-danger text-white',
      EXPIRED: 'bg-secondary text-white',
    };
    return map[status] || 'bg-secondary text-white';
  }

  loadSupplier(): void {
    this.supplierService.getSupplierByUserId(this.userId).subscribe({
      next: (res) => {
        this.supplier = res;
        this.storage.saveData(KEYS.SUPPLIER, res);
        this.cdr.markForCheck();
      },
      error: (err) => console.log(err),
    });
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }
}
