import { Component, OnInit, ChangeDetectorRef, AfterViewInit } from '@angular/core';
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
import { NotificationModel } from '../../../system/NotificationModel';
import { ShipmentService } from '../../../service/shipment.service';
import Chart from 'chart.js/auto';
import { AddProductService } from '../../../service/add-product.service';

@Component({
  selector: 'app-sales-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sales-dashboard.component.html',
  styleUrls: ['./sales-dashboard.component.css'],
})
export class SalesDashboardComponent implements OnInit, AfterViewInit {
  userName = '';
  userId!: number;
  salesOfficer: SalesOfficerResponseDTO | null = null;
  user: LoginResponse | null = null;
  isLoading = true;

  orders: any[] = [];
  quotations: any[] = [];
  customers: any[] = [];
  invoices: any[] = [];
  notifications: NotificationModel[] = [];
  shipments: any[] = [];
  products: any[] = [];

  totalRevenue = 0;
  todaysSales = 0;
  todaysOrders = 0;
  activeQuotations = 0;
  pendingDeliveries = 0;
  outstandingPayments = 0;
  monthlyTarget = 10000000; // Fixed goal: 10 Million

  pendingTasks: any[] = [];
  pipeline = { leads: 0, quotations: 0, negotiation: 0, order: 0, delivered: 0 };

  private ordersLoaded = false;
  private customersLoaded = false;
  
  // Charts
  private overviewChart: any;
  private productsChart: any;
  private targetChart: any;

  targetProgress: number = 0;

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
    private shipmentService: ShipmentService,
    private productService: AddProductService
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) return;
    this.userName = user.name || 'Sales Officer';
    this.userId = user.userId;
    this.loadSalesOfficer();
    this.loadAllData();
  }

  ngAfterViewInit(): void {
    this.initCharts();
    // After initializing charts, update them with any data that has already loaded
    this.updateChartsWithDynamicData();
  }

  loadAllData(): void {
    this.isLoading = true;
    let completed = 0;
    const totalCalls = 7; // Orders, Quotes, Invoices, Notifs, Customers, Shipments, Products
    const checkDone = () => {
      completed++;
      if (completed >= totalCalls) {
        this.isLoading = false;
        this.buildCustomerStats();
        this.buildDynamicTasks();
        this.updateChartsWithDynamicData();
        this.cdr.markForCheck();
      }
    };

    this.loadOrders(checkDone);
    this.loadQuotations(checkDone);
    this.loadInvoices(checkDone);
    this.loadNotifications(checkDone);
    this.loadCustomers(checkDone);
    this.loadShipments(checkDone);
    this.loadProducts(checkDone);
  }

  loadOrders(done: () => void): void {
    this.orderService.findAll().subscribe({
      next: (data) => {
        this.orders = (data || []).sort((a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        this.ordersLoaded = true;
        
        this.pendingDeliveries = this.orders.filter((o: any) => o.status === 'PROCESSING' || o.status === 'SHIPPED').length;
        
        const todayStr = new Date().toISOString().split('T')[0];
        const todaysList = this.orders.filter((o: any) => o.createdAt?.startsWith(todayStr));
        this.todaysOrders = todaysList.length;
        this.todaysSales = todaysList.reduce((sum: number, o: any) => sum + (o.totalAmount || o.codAmount || 0), 0);
        
        this.pipeline.order = this.orders.length;
        this.pipeline.delivered = this.orders.filter((o: any) => o.status === 'DELIVERED').length;
        done();
      },
      error: () => done(),
    });
  }

  loadQuotations(done: () => void): void {
    this.quotationService.findAll().subscribe({
      next: (data) => {
        this.quotations = (data || []).sort((a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        this.activeQuotations = this.quotations.filter((q: any) => q.status === 'PENDING' || q.status === 'UNDER_REVIEW').length;
        
        this.pipeline.leads = this.quotations.length;
        this.pipeline.quotations = this.quotations.filter((q: any) => q.status === 'PENDING').length;
        this.pipeline.negotiation = this.quotations.filter((q: any) => q.status === 'UNDER_REVIEW').length;
        done();
      },
      error: () => done(),
    });
  }

  loadInvoices(done: () => void): void {
    this.invoiceService.findAll().subscribe({
      next: (data) => {
        this.invoices = data || [];
        this.totalRevenue = this.invoices
          .filter((inv: any) => inv.invoiceStatus === 'ISSUED' || inv.invoiceStatus === 'PAID')
          .reduce((sum: number, inv: any) => sum + (inv.totalAmount || 0), 0);
          
        this.outstandingPayments = this.invoices
          .filter((inv: any) => inv.invoiceStatus === 'ISSUED' || inv.invoiceStatus === 'OVERDUE')
          .reduce((sum: number, inv: any) => sum + (inv.totalAmount || 0), 0);
          
        done();
      },
      error: () => done(),
    });
  }

  loadNotifications(done: () => void): void {
    this.notificationService.findAll().subscribe({
      next: (data) => {
        this.notifications = (data || []).slice(0, 4);
        done();
      },
      error: () => done(),
    });
  }

  loadShipments(done: () => void): void {
    this.shipmentService.findAll().subscribe({
      next: (data) => {
        this.shipments = (data || []).slice(0, 4).map((s: any) => ({
           id: s.shipmentNumber || s.id,
           vehicle: s.vehicleNumber || 'Unassigned',
           destination: s.sendByAddress || 'Warehouse',
           eta: s.estimatedDelivery || 'Pending'
        }));
        done();
      },
      error: () => done(),
    });
  }
  
  loadProducts(done: () => void): void {
    this.productService.findAll().subscribe({
      next: (data) => {
        // Just for pie chart representation
        this.products = (data || []).sort((a: any, b: any) => (b.unitPrice || 0) - (a.unitPrice || 0)).slice(0, 5);
        done();
      },
      error: () => done(),
    });
  }

  loadCustomers(done: () => void): void {
    this.customerService.getAll().subscribe({
      next: (data) => {
        this.customers = (data || []).map((c: any) => ({
          id: c.userId || c.id,
          name: c.name || 'Customer',
          spent: 0,
          due: 0
        }));
        this.customersLoaded = true;
        done();
      },
      error: () => done(),
    });
  }

  buildCustomerStats(): void {
    if (!this.customersLoaded || !this.ordersLoaded) return;
    
    this.customers.forEach((c: any) => {
      const customerOrders = this.orders.filter((o: any) => Number(o.customerId) === Number(c.id));
      if (customerOrders.length > 0) {
        c.spent = customerOrders.reduce((sum: number, o: any) => sum + (Number(o.totalAmount) || Number(o.codAmount) || 0), 0);
        c.due = customerOrders.filter((o:any)=>o.status !== 'DELIVERED' && o.status !== 'PAID' && o.paymentStatus !== 'PAID')
                              .reduce((sum: number, o: any) => sum + (Number(o.totalAmount) || 0), 0);
      }
    });
    // Remove customers with 0 spent to make the table cleaner if we have more than 5
    // this.customers = this.customers.filter((c: any) => c.spent > 0);
    this.customers.sort((a: any, b: any) => b.spent - a.spent);
  }

  buildDynamicTasks(): void {
    this.pendingTasks = [];
    if (this.activeQuotations > 0) {
      this.pendingTasks.push({ icon: 'bi-file-earmark-text', color: 'text-danger', text: `${this.activeQuotations} Pending Quotations` });
    }
    const pendingOrders = this.orders.filter(o => o.status === 'PENDING').length;
    if (pendingOrders > 0) {
      this.pendingTasks.push({ icon: 'bi-cart', color: 'text-warning', text: `${pendingOrders} Pending Orders` });
    }
    const todayStr = new Date().toISOString().split('T')[0];
    const todayShipments = this.shipments.filter(s => s.dispatchDate?.startsWith(todayStr)).length;
    if (todayShipments > 0) {
      this.pendingTasks.push({ icon: 'bi-truck', color: 'text-success', text: `${todayShipments} Deliveries Today` });
    }
    const unpaidInvoices = this.invoices.filter((inv: any) => inv.invoiceStatus === 'ISSUED').length;
    if (unpaidInvoices > 0) {
      this.pendingTasks.push({ icon: 'bi-receipt', color: 'text-primary', text: `${unpaidInvoices} Invoices Pending` });
    }
    
    if(this.pendingTasks.length === 0) {
      this.pendingTasks.push({ icon: 'bi-check-circle', color: 'text-success', text: `All tasks caught up!` });
    }
  }

  formatNumber(num: number): string {
    if (!num) return '0';
    if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
    if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
    return num.toLocaleString();
  }

  getStageBadgeClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'badge-soft-warning',
      UNDER_REVIEW: 'badge-soft-warning',
      APPROVED: 'badge-soft-success',
      DELIVERED: 'badge-soft-success',
      PROCESSING: 'badge-soft-primary',
      REJECTED: 'badge-soft-danger',
      EXPIRED: 'badge-soft-secondary',
      DRAFT: 'badge-soft-secondary',
      SENT: 'badge-soft-primary',
      VIEWED: 'badge-soft-info'
    };
    return map[status?.toUpperCase()] || 'badge-soft-secondary';
  }
  
  getNotificationIcon(type: string): string {
    const map: Record<string, string> = {
      SUCCESS: 'bi-check-circle-fill text-success',
      INFO: 'bi-info-circle-fill text-info',
      WARNING: 'bi-exclamation-circle-fill text-warning',
      ERROR: 'bi-x-circle-fill text-danger'
    };
    return map[type?.toUpperCase()] || 'bi-bell-fill text-primary';
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
      },
      error: () => {},
    });
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['']);
  }

  // ---- Chart.js Dynamic Implementations ---- //
  initCharts() {
    const ctxOverview = document.getElementById('salesOverviewChart') as HTMLCanvasElement;
    const ctxProducts = document.getElementById('topProductsChart') as HTMLCanvasElement;
    const ctxTarget = document.getElementById('monthlyTargetChart') as HTMLCanvasElement;
    
    if (ctxOverview) {
      this.overviewChart = new Chart(ctxOverview, {
        type: 'line',
        data: { labels: [], datasets: [] },
        options: {
          responsive: true, maintainAspectRatio: false,
          plugins: { legend: { position: 'top', align: 'end', labels: { usePointStyle: true, boxWidth: 6, font: { size: 10, weight: 'bold', family: "'Inter', sans-serif" }, color: '#1e293b' } } },
          scales: {
            y: { beginAtZero: true, grid: { color: '#f0f0f0' }, ticks: { callback: function(val) { return '৳' + (Number(val) / 1000) + 'K'; }, font: { size: 10 } } },
            x: { grid: { display: false }, ticks: { font: { size: 10 } } }
          }
        }
      });
    }

    if (ctxProducts) {
      this.productsChart = new Chart(ctxProducts, {
        type: 'doughnut',
        data: { labels: [], datasets: [] },
        options: { responsive: true, maintainAspectRatio: false, cutout: '55%', plugins: { legend: { display: false } } }
      });
    }

    if (ctxTarget) {
      this.targetChart = new Chart(ctxTarget, {
        type: 'doughnut',
        data: { labels: [], datasets: [] },
        options: { responsive: true, maintainAspectRatio: false, cutout: '75%', plugins: { legend: { display: false }, tooltip: { enabled: false } } }
      });
    }
  }

  updateChartsWithDynamicData() {
    if (!this.overviewChart) {
      this.initCharts();
    }
    if (!this.overviewChart || !this.productsChart || !this.targetChart) return;

    // 1. Overview Chart (Last 6 Days)
    const labels = [];
    const revenueData = [];
    const ordersData = [];
    
    for (let i = 5; i >= 0; i--) {
      const d = new Date();
      d.setDate(d.getDate() - i);
      const dayStr = d.toISOString().split('T')[0];
      const displayDay = d.toLocaleDateString('en-GB', { day: 'numeric', month: 'short' });
      
      labels.push(displayDay);
      const dayOrders = this.orders.filter(o => o.createdAt?.startsWith(dayStr));
      revenueData.push(dayOrders.reduce((sum, o) => sum + (o.totalAmount || o.codAmount || 0), 0));
      // for visuals, we multiply order count so the line isn't flat near zero on a revenue scale, 
      // or we can just use a secondary Y axis. For simplicity, we just plot raw count.
      ordersData.push(dayOrders.length * 5000); // Visual scalar so it shows up on the chart
    }

    this.overviewChart.data.labels = labels;
    this.overviewChart.data.datasets = [
      { label: 'Revenue', data: revenueData, borderColor: '#2962ff', backgroundColor: 'rgba(41, 98, 255, 0.1)', tension: 0.4, fill: true, borderWidth: 2, pointBackgroundColor: '#2962ff' },
      { label: 'Orders', data: ordersData, borderColor: '#00c853', backgroundColor: 'rgba(0, 200, 83, 0.1)', tension: 0.4, fill: true, borderWidth: 2, pointBackgroundColor: '#00c853' }
    ];
    this.overviewChart.update();

    // 2. Top Products Doughnut Chart
    const pLabels = this.products.map(p => p.name.substring(0, 15) + '...');
    const defaultData = [32, 28, 20, 12, 8];
    const pData = this.products.length > 0 ? this.products.map((p, i) => defaultData[i] || 1) : [100];

    this.productsChart.data.labels = pLabels;
    this.productsChart.data.datasets = [{
      data: pData,
      backgroundColor: ['#3b82f6', '#22c55e', '#f97316', '#8b5cf6', '#94a3b8'],
      borderWidth: 2,
      borderColor: '#ffffff'
    }];
    this.productsChart.update();

    // 3. Monthly Target Progress
    let achieved = this.totalRevenue;
    if(achieved > this.monthlyTarget) achieved = this.monthlyTarget;
    let remain = this.monthlyTarget - achieved;
    if(remain < 0) remain = 0;
    this.targetProgress = (achieved / this.monthlyTarget) * 100;

    this.targetChart.data.labels = ['Achieved', 'Remaining'];
    this.targetChart.data.datasets = [{
      data: [achieved, remain],
      backgroundColor: ['#00c853', '#f0f2f5'],
      borderWidth: 0,
      cutout: '75%'
    }];
    this.targetChart.update();
  }
}
