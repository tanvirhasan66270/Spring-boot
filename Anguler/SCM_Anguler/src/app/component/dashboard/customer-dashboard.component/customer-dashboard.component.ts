import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { CustomerOrderService } from '../../../service/customer-order.service';
import { CustomerOrderResponseModel } from '../../shared/model/customerOrder';
import { LoginResponse } from '../../../auth/Model/authModel';
import { CustomerService } from '../../../service/customer.service';
import { CustomerResponseModel } from '../../shared/model/customerModel';
import { AddProductService } from '../../../service/add-product.service';
import { ProductResponseModel } from '../../shared/model/addProduct';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';
import { NotificationService } from '../../../system/service/notification.service';
import { NotificationModel } from '../../../system/NotificationModel';
import { ActivityLogService } from '../../../service/activity.log.service';
import { ActivityLogModel } from '../../shared/model/ActivityLogModel';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.css'],
})
export class CustomerDashboardComponent implements OnInit, OnDestroy {
  user: LoginResponse | null = null;
  userId!: number;
  customer: CustomerResponseModel | null = null;
  customerOrders: CustomerOrderResponseModel[] = [];
  userName = '';

  stats = { total: 0, active: 0, completed: 0, pending: 0, cancelled: 0, duePayments: 0 };
  recentOrders: CustomerOrderResponseModel[] = [];
  walletBalance = 0;
  dueAmountTotal = 0;
  monthlyExpenses: number[] = [];
  monthLabels: string[] = [];
  recommendations: any[] = [];

  notifications: NotificationModel[] = [];
  activities: ActivityLogModel[] = [];

  showSettings = false;
  loading = true;

  deliveredPercent = 0;
  processingPercent = 0;
  cancelledPercent = 0;

  donutDelivered = '0 282';
  donutProcessing = '0 282';
  donutCancelled = '0 282';
  donutOffsetProcessing = 0;
  donutOffsetCancelled = 0;

  chartPath = '';
  chartDots: { x: number; y: number }[] = [];
  chartMaxY = 1;

  constructor(
    private storage: StorageService,
    private orderService: CustomerOrderService,
    private cutomerService: CustomerService,
    private productService: AddProductService,
    private notificationService: NotificationService,
    private activityLogService: ActivityLogService,
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name;
    this.userId = user.userId;
    this.loadCustomer();
    this.loadDashboardData();
    this.loadRecommendations();
    this.loadNotifications();
    this.loadActivities();
  }

  loadCustomer(): void {
    this.cutomerService.getCustomerByUserId(this.userId).subscribe({
      next: (res) => {
        this.customer = res;
        this.storage.saveData(KEYS.CUSTOMER, res);
        this.cdr.markForCheck();
      },
      error: (err) => console.log(err),
    });
  }

  ngOnDestroy(): void {}

  loadDashboardData(): void {
    this.loading = true;
    this.orderService.findAll().subscribe({
      next: (orders) => {
        this.customerOrders = orders ? orders.filter((o) => o.customerId === this.userId) : [];

        this.stats.total = this.customerOrders.length;
        this.stats.pending = this.customerOrders.filter((o) => o.status === 'PENDING').length;
        this.stats.active = this.customerOrders.filter((o) =>
          ['CONFIRMED', 'PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY'].includes(o.status),
        ).length;
        this.stats.completed = this.customerOrders.filter((o) => o.status === 'DELIVERED').length;
        this.stats.cancelled = this.customerOrders.filter((o) => o.status === 'CANCELLED').length;
        this.stats.duePayments = this.customerOrders.filter(
          (o) => o.paymentStatus !== 'PAID',
        ).length;

        this.walletBalance = this.customerOrders
          .filter((o) => o.paymentStatus === 'PAID')
          .reduce((sum, o) => sum + (o.codAmount || 0), 0);

        this.dueAmountTotal = this.customerOrders
          .filter((o) => o.paymentStatus !== 'PAID')
          .reduce((sum, o) => sum + (parseFloat(o.dueAmount as any) || 0), 0);

        const totalOrders = this.customerOrders.length;
        const deliveredCount = this.customerOrders.filter((o) => o.status === 'DELIVERED').length;
        const processingCount = this.customerOrders.filter((o) =>
          ['CONFIRMED', 'PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY', 'PENDING'].includes(o.status),
        ).length;
        const cancelledCount = this.customerOrders.filter((o) => o.status === 'CANCELLED').length;

        if (totalOrders > 0) {
          this.deliveredPercent = Math.round((deliveredCount / totalOrders) * 100);
          this.processingPercent = Math.round((processingCount / totalOrders) * 100);
          this.cancelledPercent = Math.round((cancelledCount / totalOrders) * 100);
        }

        const total = 282;
        const dDelivered = Math.round((this.deliveredPercent / 100) * total);
        const dProcessing = Math.round((this.processingPercent / 100) * total);
        const dCancelled = total - dDelivered - dProcessing;
        this.donutDelivered = `${dDelivered} ${total}`;
        this.donutProcessing = `${dProcessing} ${total}`;
        this.donutCancelled = `${dCancelled} ${total}`;
        this.donutOffsetProcessing = -dDelivered;
        this.donutOffsetCancelled = -(dDelivered + dProcessing);

        const now = new Date();
        const shortMonths = [
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
        this.monthlyExpenses = [];
        this.monthLabels = [];
        for (let i = 5; i >= 0; i--) {
          const month = new Date(now.getFullYear(), now.getMonth() - i, 1);
          const monthStr = `${month.getFullYear()}-${String(month.getMonth() + 1).padStart(2, '0')}`;
          const monthTotal = this.customerOrders
            .filter((o) => o.createdAt && o.createdAt.startsWith(monthStr))
            .reduce((sum, o) => sum + (o.codAmount || 0), 0);
          this.monthlyExpenses.push(monthTotal);
          this.monthLabels.push(shortMonths[month.getMonth()]);
        }

        this.computeChartPath();

        this.recentOrders = [...this.customerOrders]
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
          .slice(0, 5);

        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Customer metrics fetching failed:', err);
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  computeChartPath(): void {
    const data = this.monthlyExpenses;
    if (!data.length) return;

    this.chartMaxY = Math.max(...data, 1);

    const svgW = 460;
    const svgH = 160;
    const padX = 40;
    const padY = 20;
    const usableW = svgW - padX * 2;
    const usableH = svgH - padY * 2;

    this.chartDots = data.map((val, i) => {
      const x = padX + (i / (data.length - 1 || 1)) * usableW;
      const y = padY + usableH - (val / this.chartMaxY) * usableH;
      return { x, y };
    });

    if (this.chartDots.length < 2) return;

    let path = `M ${this.chartDots[0].x} ${this.chartDots[0].y}`;
    for (let i = 1; i < this.chartDots.length; i++) {
      const prev = this.chartDots[i - 1];
      const curr = this.chartDots[i];
      const cpx1 = prev.x + (curr.x - prev.x) * 0.4;
      const cpx2 = curr.x - (curr.x - prev.x) * 0.4;
      path += ` C ${cpx1} ${prev.y}, ${cpx2} ${curr.y}, ${curr.x} ${curr.y}`;
    }
    this.chartPath = path;
  }

  loadRecommendations(): void {
    this.productService.findAll().subscribe({
      next: (data) => {
        this.recommendations = (data || []).slice(0, 3).map((p: ProductResponseModel) => ({
          name: p.name || 'Product',
          price: p.sellingPrice || 0,
          rating:
            Math.round(((p.sellingPrice || 0) / Math.max(p.unitCost || 1, 1)) * 10) / 10 > 5
              ? 5
              : Math.round(((p.sellingPrice || 0) / Math.max(p.unitCost || 1, 1)) * 10) / 10 || 4.0,
          image: 'bi-box-seam',
        }));
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

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['']);
  }
}
