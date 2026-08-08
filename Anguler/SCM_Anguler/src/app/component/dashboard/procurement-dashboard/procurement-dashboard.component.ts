import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
import { NotificationModel } from '../../../system/NotificationModel';
import { PurchaseOrderResponseModel } from '../../shared/model/purchaseOrderModel';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';

@Component({
  selector: 'app-procurement-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './procurement-dashboard.component.html',
  styleUrls: ['./procurement-dashboard.component.css'],
})
export class ProcurementDashboardComponent implements OnInit {
  userName = '';
  userId!: number;
  procurement: ProcurementResponseDTO | null = null;
  user: LoginResponse | null = null;
  showSettings = false;

  // Key Metrics (KPIs)
  totalSpend = 0;
  pendingPOs = 0;
  approvedPOs = 0;
  activeSuppliers = 0;
  approvedPRs = 0;
  
  totalInvoices = 0;
  totalProducts = 0;
  totalRFQs = 0;
  totalPRs = 0;

  // View All Modal State
  isViewAllModalOpen = false;
  viewAllModalTitle = '';
  viewAllModalType = '';
  viewAllModalData: any[] = [];
  modalSearchText: string = '';
  modalSearchDate: string = '';

  rfqSearchText: string = '';
  rfqSearchDate: string = '';

  get displayRfqs() {
    // Bypass monthly filter: "no change monthly data"
    let filtered = this.rawRFQs;
    
    if (this.rfqSearchText && this.rfqSearchText.trim() !== '') {
      const searchLower = this.rfqSearchText.toLowerCase().trim();
      filtered = filtered.filter(q => {
        const values = Object.values(q).join(' ').toLowerCase();
        return values.includes(searchLower);
      });
    }

    if (this.rfqSearchDate) {
      filtered = filtered.filter(q => {
        const dateStr = q.createdAt || q.date || q.createdAtDate || '';
        if (!dateStr) return false;
        return String(dateStr).startsWith(this.rfqSearchDate);
      });
    }

    const mapped = filtered.map((q: any) => ({
      id: q.id || q.quotationId,
      item: q.productName || q.productDescription || 'N/A',
      qty: q.quantity || 0,
      status: q.status || 'PENDING',
      supplier: q.supplierName || 'Pending Sourcing',
    }));
    
    if (!this.rfqSearchText && !this.rfqSearchDate) {
      return mapped.slice(0, 5);
    }
    return mapped;
  }

  selectedDashboardPeriod: string = 'All Time';
  rawPRs: any[] = [];
  rawRFQs: any[] = [];
  rawPOs: any[] = [];
  rawProducts: any[] = [];

  setDashboardPeriod(period: string) {
    this.selectedDashboardPeriod = period;
    this.aggregateData();
  }

  filterByPeriod(data: any[]): any[] {
    if (this.selectedDashboardPeriod === 'All Time') return data;
    
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth();
    
    return data.filter(item => {
      const dateStr = item.createdAt || item.date || item.createdAtDate || item.expectedDeliveryDate || '';
      if (!dateStr) return true;
      
      const d = new Date(dateStr);
      if (isNaN(d.getTime())) return true;
      
      const itemYear = d.getFullYear();
      const itemMonth = d.getMonth();
      
      switch (this.selectedDashboardPeriod) {
        case 'This Month': return itemYear === currentYear && itemMonth === currentMonth;
        case 'Last Month':
          const lastMonth = currentMonth === 0 ? 11 : currentMonth - 1;
          const lastMonthYear = currentMonth === 0 ? currentYear - 1 : currentYear;
          return itemYear === lastMonthYear && itemMonth === lastMonth;
        case 'This Year': return itemYear === currentYear;
        case 'Last Year': return itemYear === currentYear - 1;
        case 'January': return itemYear === currentYear && itemMonth === 0;
        case 'February': return itemYear === currentYear && itemMonth === 1;
        case 'March': return itemYear === currentYear && itemMonth === 2;
        case 'April': return itemYear === currentYear && itemMonth === 3;
        case 'May': return itemYear === currentYear && itemMonth === 4;
        case 'June': return itemYear === currentYear && itemMonth === 5;
        case 'July': return itemYear === currentYear && itemMonth === 6;
        case 'August': return itemYear === currentYear && itemMonth === 7;
        case 'September': return itemYear === currentYear && itemMonth === 8;
        case 'October': return itemYear === currentYear && itemMonth === 9;
        case 'November': return itemYear === currentYear && itemMonth === 10;
        case 'December': return itemYear === currentYear && itemMonth === 11;
        default: return true;
      }
    });
  }

  aggregatePRs() {
    const all = this.filterByPeriod(this.rawPRs);
    const prCount = all.length;
    this.totalPRs = prCount;
    const prApproved = all.filter((r: any) => r.approvalStatus === 'APPROVED' || r.approvalStatus === 'FULFILLED').length;
    this.approvedPRs = prApproved;
    this.kpis[0] = { ...this.kpis[0], value: `${prCount} Requisitions` };
    if (prCount > 0 && prApproved > 0) {
      const prev = prCount - Math.round(prCount * 0.12);
      this.kpis[0].trend = prev > 0 ? Math.round(((prCount - prev) / prev) * 100) : prCount;
      this.kpis[0].trendDir = 'up';
    }
    this.allTotalPRsData = all;
  }

  aggregateRFQs() {
    const all = this.filterByPeriod(this.rawRFQs);
    const rfqCount = all.length;
    this.totalRFQs = rfqCount;
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
    this.allActiveRFQsData = all;
  }

  aggregateProducts() {
    const all = this.filterByPeriod(this.rawProducts);
    const products = all.filter((p: any) => p.quantity <= (p.reorderPoint || 10));
    this.totalProducts = all.length;
    this.shortages = products.slice(0, 5).map((p: any) => ({
      item: p.name || 'Product',
      stock: `${p.quantity || 0} Units`,
      threshold: `${p.reorderPoint || 10} Units`,
      urgency: (p.quantity || 0) === 0 ? 'CRITICAL' : 'HIGH',
    }));
  }

  aggregatePOs() {
    const all = this.filterByPeriod(this.rawPOs);
    const poCount = all.length;
    const poPending = all.filter((po: any) => po.status === 'ISSUED' || po.status === 'DRAFT').length;
    this.pendingPOs = poPending;
    this.allPendingPOsData = all.filter((po: any) => po.status === 'ISSUED' || po.status === 'DRAFT');
    
    const approvedCount = all.filter((po: any) => po.status === 'APPROVED' || po.status === 'RECEIVED').length;
    this.approvedPOs = approvedCount;
    this.allApprovedPOsData = all.filter((po: any) => po.status === 'APPROVED' || po.status === 'RECEIVED');

    this.recentPOs = all.slice(0, 5);

    const totalSpend = all.reduce((sum: number, po: any) => sum + (po.totalAmount || 0), 0);
    this.totalSpend = totalSpend;

    this.kpis[2] = { ...this.kpis[2], value: `${poPending} Pending` };
    if (poCount > 0) {
      const prev = poCount - Math.round(poCount * 0.15);
      this.kpis[2].trend = prev > 0 ? Math.round(((poCount - prev) / prev) * 100) : poCount;
      this.kpis[2].trendDir = 'up';
    }

    const received = all.filter((po: any) => po.status === 'RECEIVED').length;
    const budgetPct = poCount > 0 ? Math.round((received / poCount) * 100) : 0;
    this.kpis[3] = { ...this.kpis[3], value: `${budgetPct}%` };
    this.kpis[3].trend = budgetPct > 0 ? Math.min(budgetPct, 100) : 0;
    this.kpis[3].trendDir = 'up';
  }


  aggregateData() {
    this.aggregatePRs();
    this.aggregateRFQs();
    this.aggregatePOs();
    this.aggregateProducts();
    this.buildCostAnalytics();
    this.buildQuickInsights();
    this.cdr.markForCheck();
  }
  
  donutSegments: any[] = [];
  upcomingDeadlines: any[] = [];
  topSuppliers: any[] = [];

  quickInsights: any[] = [];
  
  allPendingPOsData: any[] = [];
  allApprovedPOsData: any[] = [];
  allTotalPRsData: any[] = [];
  allActiveRFQsData: any[] = [];
  allActiveSuppliersData: any[] = [];

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

  costCategories: { label: string; value: number; pct: number; color?: string }[] = [];
  costTotal = 0;

  rfqs: any[] = [];
  shortages: any[] = [];
  notifications: any[] = [];
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
  }

  // Opens the View All Modal with specific data
  openViewAllModal(title: string, type: string, data: any[]): void {
    this.viewAllModalTitle = title;
    this.viewAllModalType = type;
    this.viewAllModalData = data;
    this.modalSearchText = '';
    this.modalSearchDate = '';
    this.isViewAllModalOpen = true;
  }

  // Closes the View All Modal
  closeViewAllModal(): void {
    this.isViewAllModalOpen = false;
    this.modalSearchText = '';
    this.modalSearchDate = '';
  }

  // Dynamic filter for modal data
  get filteredModalData(): any[] {
    if (!this.viewAllModalData) return [];
    
    let filtered = this.viewAllModalData;
    
    // Filter by text search
    if (this.modalSearchText && this.modalSearchText.trim() !== '') {
      const searchLower = this.modalSearchText.toLowerCase().trim();
      filtered = filtered.filter(item => {
        // Deep stringify search across all object values
        const values = Object.values(item).join(' ').toLowerCase();
        return values.includes(searchLower);
      });
    }

    // Filter by date
    if (this.modalSearchDate) {
      filtered = filtered.filter(item => {
        // Check known date fields
        const dateStr = item.createdAt || item.date || item.createdAtDate || '';
        if (!dateStr) return false;
        
        // Match standard format YYYY-MM-DD
        return String(dateStr).startsWith(this.modalSearchDate);
      });
    }
    
    return filtered;
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
        this.rawPRs = data || [];
        this.aggregatePRs();
        this.buildCostAnalytics();
        this.cdr.markForCheck();
      },
    });

    this.quotationService.findAll().subscribe({
      next: (data) => {
        this.rawRFQs = data || [];
        this.aggregateRFQs();
        this.cdr.markForCheck();
      },
    });

    this.productService.findAll().subscribe({
      next: (data) => {
        this.rawProducts = data || [];
        this.aggregateProducts();
        this.cdr.markForCheck();
      },
    });

    this.poService.findAll().subscribe({
      next: (data) => {
        this.rawPOs = data || [];
        this.aggregatePOs();
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
        const allInvoices = data || [];
        this.totalInvoices = allInvoices.length;
        const invoiceTotal = allInvoices.reduce(
          (sum: number, inv: any) => sum + (inv.totalAmount || 0),
          0,
        );
        if (invoiceTotal > totalSpend) {
          this.totalSpend = invoiceTotal;
        }
        this.cdr.markForCheck();
      },
    });

    // Dummy Data Initializations
    this.activeSuppliers = 15;
    this.allActiveSuppliersData = [
      { name: 'TechCorp Industries', rating: '4.8', activePOs: 3 },
      { name: 'Global Office Solutions', rating: '4.5', activePOs: 2 },
      { name: 'Apex Logistics', rating: '4.9', activePOs: 5 },
      { name: 'Quantum Materials', rating: '4.2', activePOs: 1 }
    ];

    this.upcomingDeadlines = [
      { item: 'Office Supplies Restock', type: 'PO-2024-001', date: '2024-09-15', urgency: 'High' },
      { item: 'IT Equipment Delivery', type: 'PO-2024-005', date: '2024-09-18', urgency: 'Medium' }
    ];

    this.topSuppliers = [
      { name: 'TechCorp Industries', amount: 45000, percentage: 65 },
      { name: 'Global Office Solutions', amount: 25000, percentage: 25 }
    ];

  }

  buildQuickInsights() {
    const insights = [];

    const prCount = this.allTotalPRsData.length;
    const prApproved = this.approvedPRs;
    const approvalRate = prCount > 0 ? Math.round((prApproved / prCount) * 100) : 0;
    insights.push({
      label: 'PR Approval Rate',
      value: `${approvalRate}%`,
      icon: 'bi-check2-circle'
    });

    const shortagesCount = this.shortages.length;
    insights.push({
      label: 'Critical Shortages',
      value: `${shortagesCount} Items`,
      icon: 'bi-exclamation-triangle'
    });

    insights.push({
      label: 'Pending Orders',
      value: `${this.pendingPOs} POs`,
      icon: 'bi-clock-history'
    });

    insights.push({
      label: 'Budget Sourced',
      value: this.kpis[3] ? this.kpis[3].value : '0%',
      icon: 'bi-wallet2'
    });

    this.quickInsights = insights.slice(0, 4);
  }

  buildCostAnalytics() {
    const categories = [
      { label: 'Sourcing Cost', key: 'sourcing', color: '#28a745' },
      { label: 'Logistics Cost', key: 'logistics', color: '#0d6efd' },
      { label: 'Material Cost', key: 'material', color: '#6f42c1' },
      { label: 'Other Cost', key: 'other', color: '#ffc107' },
    ];

    const poTotal = this.totalSpend;
    const values = [
      Math.round(poTotal * 0.35),
      Math.round(poTotal * 0.25),
      Math.round(poTotal * 0.25),
      Math.round(poTotal * 0.15),
    ];

    this.costTotal = poTotal;
    
    let cumulativePercent = 0;
    this.donutSegments = categories.map((c, i) => {
      const pct = poTotal > 0 ? Math.round((values[i] / poTotal) * 100) : 0;
      const strokeDasharray = `${pct} ${100 - pct}`;
      const strokeDashoffset = 25 - cumulativePercent;
      cumulativePercent += pct;
      return {
        label: c.label,
        value: values[i],
        pct: pct,
        color: c.color,
        dasharray: strokeDasharray,
        dashoffset: strokeDashoffset
      };
    });
    
    this.costCategories = this.donutSegments;
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
