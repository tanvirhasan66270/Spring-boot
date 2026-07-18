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
import { PurchaseRequisitionService } from '../../../service/purchase-requisition.service';
import { purchaseRequisitionResponseModel } from '../../shared/model/purchase-requisionModel';

@Component({
  selector: 'app-supplier-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './supplier-dashboard.component.html',
  styleUrls:['./supplier-dashboard.component.css'],
})
export class SupplierDashboardComponent implements OnInit {
  userName = '';
  userId!: number;
  supplier: SupplierResponseDTO | null = null;
  user: LoginResponse | null = null;

  kpis = [
    { label: 'Purchase Orders', value: '0 POs', trend: 0, icon: 'bi-inboxes', color: 'primary' },
    { label: 'Pending Delivery', value: '0 Consignments', trend: 0, icon: 'bi-truck-flatbed', color: 'warning' },
    { label: 'Outstanding Payments', value: '৳0', trend: 0, icon: 'bi-currency-exchange', color: 'success' },
    { label: 'Supply Accuracy', value: '0%', trend: 0, icon: 'bi-patch-check', color: 'success' },
  ];

  pos: any[] = [];          
  receivedPOs: any[] = [];  
  rfqs: any[] = [];
  notifications: NotificationModel[] = [];
  activities: ActivityLogModel[] = [];
  
  lcOpen = false;
  lettersOfCredit: any[] = [];
  poRegistryOpen = false;
  allSupplierPOs: any[] = [];
  requisitions: any[] = []; 

  showSettings = false;
  loading = true;

  outstandingPayments = 0;
  supplyAccuracy = 0;
  pendingDeliveries = 0;

  constructor(
    private storage: StorageService,
    public router: Router, 
    private cdr: ChangeDetectorRef,
    private supplierService: SupplierService,
    private poService: PurchaseOrderService,
    private quotationService: QuotationService,
    private notificationService: NotificationService,
    private activityLogService: ActivityLogService,
    private invoiceService: InvoiceService,
    private prService: PurchaseRequisitionService
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name || 'Supplier Node';
    this.userId = user.userId;
    this.loadSupplier();
    this.loadNotifications();
    this.loadActivities();
  }

  isChildRouteActive(): boolean {
    return this.router.url.includes('supplier_profile');
  }

  onEditProfileTriggered(): void {
    this.showSettings = false; 
    
    // আপনার প্যারেন্ট লেআউট রাউটের উপর ভিত্তি করে নেভিগেট করবে
    // যদি আপনার রুট /supplier/dashboard হয়, তবে এটি হবে: ['/supplier/dashboard/supplier_profile']
    // এখানে আপনার বর্তমান সেশন ফ্রেমওয়ার্ক অনুযায়ী রিলেটিভ পাথ জেনারেট করা হলো
    this.router.navigate([this.router.url.split('/supplier_profile')[0] + '/supplier_profile']);
    this.cdr.markForCheck();
  }

  loadSupplier(): void {
    this.supplierService.getSupplierByUserId(this.userId).subscribe({
      next: (res) => {
        if (res) {
          this.supplier = res;
          this.storage.saveData(KEYS.SUPPLIER, res);
          this.loadDashboardData(res.id);
        }
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Profile verification failed:', err);
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  loadDashboardData(supplierId: number) {
    this.loading = true;

    this.poService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        const supplierSpecificOrders = all.filter((o: any) => {
          const sId = o.supplierId || (o.supplier ? o.supplier.id : null);
          return sId === supplierId;
        });

        const issuedOrders = supplierSpecificOrders.filter((o: PurchaseOrderResponseModel) => o.status === 'ISSUED');
        const delivered = supplierSpecificOrders.filter((o: PurchaseOrderResponseModel) => o.status === 'RECEIVED');
        this.pendingDeliveries = issuedOrders.length;

        if (supplierSpecificOrders.length > 0) {
          this.supplyAccuracy = Math.round((delivered.length / supplierSpecificOrders.length) * 100);
        }

        this.kpis[0] = { ...this.kpis[0], value: `${supplierSpecificOrders.length} POs` };
        this.kpis[1] = { ...this.kpis[1], value: `${this.pendingDeliveries} Consignments` };
        this.kpis[3] = { ...this.kpis[3], value: `${this.supplyAccuracy}%` };

        this.pos = issuedOrders.slice(0, 5).map((o: PurchaseOrderResponseModel) => ({
          id: o.id,
          poNumber: o.poNumber || `PO-${o.id}`,
          date: o.createdAt || 'N/A',
          amount: o.totalAmount || 0,
          deliveryDue: o.expectedDeliveryDate || 'N/A',
          status: o.status || 'ISSUED',
        }));

        this.receivedPOs = delivered.slice(0, 5).map((o: PurchaseOrderResponseModel) => ({
          id: o.id,
          poNumber: o.poNumber || `PO-${o.id}`,
          date: o.createdAt || 'N/A',
          amount: o.totalAmount || 0,
          deliveryDue: o.expectedDeliveryDate || 'N/A',
          status: o.status || 'RECEIVED',
        }));

        this.lettersOfCredit = delivered.map((o: any) => ({
          lcNumber: `LC-${100000 + o.id}`,
          poReference: o.poNumber || `PO-${o.id}`,
          expiryDate: o.expectedDeliveryDate ? new Date(new Date(o.expectedDeliveryDate).getTime() + (30 * 24 * 60 * 60 * 1000)) : 'N/A',
          amount: o.totalAmount || 0,
          status: 'OPEN'
        }));

        if (delivered.length > 0) {
          localStorage.setItem('hasReceivedPO', 'true');
        } else {
          localStorage.removeItem('hasReceivedPO');
        }

        this.cdr.markForCheck();
      },
      error: (err) => console.error('PO Stream Load Error:', err),
    });

    this.quotationService.findAll().subscribe({
      next: (data) => {
        const allRfqs = data || [];
        const supplierSpecificRfqs = allRfqs.filter((q: any) => {
          const sId = q.supplierId || (q.supplier ? q.supplier.id : null);
          return sId === supplierId;
        });

        this.rfqs = supplierSpecificRfqs.slice(0, 3).map((q: QuotationResponseModel) => ({
          id: q.quotationNumber || `RFQ-${q.id}`,
          item: q.productName || 'N/A',
          closingDate: q.validUntil || 'N/A',
          status: q.status || 'PENDING',
        }));
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Quotation Load Error:', err)
    });

    this.prService.findAll().subscribe({
      next: (data) => {
        const allRequisitions = data || [];
        const approvedOnly = allRequisitions.filter(
          (pr: purchaseRequisitionResponseModel) => pr.approvalStatus === 'APPROVED'
        );

        this.requisitions = approvedOnly.slice(0, 5).map((pr: purchaseRequisitionResponseModel) => ({
          id: pr.id,
          productNames: pr.productNames || [],
          supplierNames: pr.supplierNames || [], 
          quantity: pr.quantityRequired || 0,
          urgency: pr.urgencyLevel || 'LOW',
          deadline: pr.requiredByDate || 'N/A',
          status: pr.approvalStatus || 'APPROVED'
        }));
        
        this.cdr.markForCheck(); 
      },
      error: (err) => console.error('PR Load Error:', err)
    });

    this.invoiceService.findAll().subscribe({
      next: (data) => {
        const invoices = data || [];
        const supplierSpecificInvoices = invoices.filter((inv: any) => {
          const sId = inv.supplierId || (inv.supplier ? inv.supplier.id : null);
          return sId === supplierId;
        });

        this.outstandingPayments = supplierSpecificInvoices.reduce(
          (sum: number, inv: InvoiceResponseModel) => sum + (inv.dueAmount || 0),
          0,
        );
        this.kpis[2] = { ...this.kpis[2], value: `৳${this.outstandingPayments.toLocaleString()}` };
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Invoice Load Error:', err);
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  toggleBillingLCSection(event: Event) {
    event.preventDefault();
    this.lcOpen = !this.lcOpen;
    this.cdr.markForCheck();
  }

  updatePoStatus(id: number, nextStatus: 'RECEIVED' | 'CANCELLED') {
    this.poService.changeStatus(id, nextStatus).subscribe({
      next: () => {
        alert(`Purchase Order successfully marked as ${nextStatus}!`);
        if (this.supplier) {
          this.loadDashboardData(this.supplier.id);
        }
      },
      error: (err) => {
        console.error('Failed to change PO status matrix:', err);
        alert(err.error?.message || 'Transaction failed.');
      },
    });
  }

  togglePurchaseOrderRegistry(event: Event) {
    event.preventDefault();
    this.poRegistryOpen = !this.poRegistryOpen;

    if (this.poRegistryOpen && this.supplier) {
      this.loading = true; 
      this.poService.getOrdersBySupplierId(this.supplier.id).subscribe({
        next: (data) => {
          this.allSupplierPOs = (data || []).map((o: any) => ({
            poNumber: o.poNumber || `PO-${o.id}`,
            date: o.createdAt || 'N/A',
            amount: o.totalAmount || 0,
            deliveryDue: o.expectedDeliveryDate || 'N/A',
            status: o.status || 'DRAFT'
          }));
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Supplier PO history pipeline missing:', err);
          this.loading = false;
          this.cdr.markForCheck();
        }
      });
    }
  }

  loadNotifications(): void {
    this.notificationService.findAll().subscribe({
      next: (data) => {
        this.notifications = (data || []).slice(0, 5);
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Notification Stream Error:', err),
    });
  }

  loadActivities(): void {
    this.activityLogService.findByUserId(this.userId.toString()).subscribe({
      next: (data) => {
        this.activities = (data || []).slice(0, 5);
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Activity Log Stream Error:', err),
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

  getPrStatusClass(status: string): string {
    const map: Record<string, string> = {
      APPROVED: 'bg-success-subtle text-success border border-success',
      PENDING: 'bg-warning-subtle text-warning border border-warning',
      REJECTED: 'bg-danger-subtle text-danger border border-danger',
      CANCELLED: 'bg-dark-subtle text-dark border border-dark',
    };
    return map[status] || 'bg-warning-subtle text-warning border border-warning';
  }

  logout(): void {
    localStorage.removeItem('hasReceivedPO');
    this.storage.clearSession();
    this.router.navigate(['']);
  }
}