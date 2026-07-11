import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { StorageService } from '../../../auth/auth_service/storage.service';
import { CustomerOrderService } from '../../../service/customer-order.service';
import { CustomerOrderResponseModel } from '../../shared/model/customerOrder';
import { LoginResponse } from '../../../auth/Model/authModel';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.css']
})
export class CustomerDashboardComponent implements OnInit, OnDestroy {
  user: LoginResponse | null = null;
  userId!: number;
  customerOrders: CustomerOrderResponseModel[] = [];
  
  stats = { total: 0, active: 0, completed: 0, pending: 0, cancelled: 0, duePayments: 0 };
  recentOrders: CustomerOrderResponseModel[] = [];
  walletBalance = 15000; // Simulated wallet balance in BDT
  monthlyExpenses = [12000, 18000, 24500, 15000, 22000, 19800]; // Mock monthly spending trend

  recommendations = [
    { name: 'Industrial Sewing Thread Spool', price: 450, rating: 4.8, image: 'bi-box-seam' },
    { name: 'Universal Machinery Grease Tub', price: 380, rating: 4.5, image: 'bi-box-seam' },
    { name: 'Heavy Duty Needle Set', price: 150, rating: 4.7, image: 'bi-box' }
  ];

  constructor(
    private storage: StorageService,
    private orderService: CustomerOrderService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.user = this.storage.getUser();
    if (this.user?.userId) {
      this.userId = this.user.userId;
      this.loadDashboardData();
    }
  }

  ngOnDestroy(): void {}

  loadDashboardData(): void {
    this.orderService.findAll().subscribe({
      next: (orders) => {
        this.customerOrders = orders ? orders.filter(o => o.customerId === this.userId) : [];
        
        this.stats.total = this.customerOrders.length;
        this.stats.pending = this.customerOrders.filter(o => o.status === 'PENDING').length;
        this.stats.active = this.customerOrders.filter(o => ['CONFIRMED', 'PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY'].includes(o.status)).length;
        this.stats.completed = this.customerOrders.filter(o => o.status === 'DELIVERED').length;
        this.stats.cancelled = this.customerOrders.filter(o => o.status === 'CANCELLED').length;
        this.stats.duePayments = this.customerOrders.filter(o => o.paymentStatus !== 'PAID').length;

        this.recentOrders = [...this.customerOrders]
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
          .slice(0, 5);

        this.cdr.markForCheck();
      },
      error: (err) => console.error("Customer metrics fetching failed:", err)
    });
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }
}
