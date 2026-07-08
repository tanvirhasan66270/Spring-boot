import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LoginResponse } from '../../../auth/Model/authModel';
import { CustomerOrderResponseModel } from '../../shared/model/customerOrder';
import { AuthService } from '../../../auth/auth_service/auth-service';
import { StorageService, KEYS } from '../../../auth/auth_service/storage.service';
import { CustomerOrderService } from '../../../service/customer-order.service';

import { environment } from '../../../../encironment/environment';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './customer-dashboard.component.html',
  styleUrl: './customer-dashboard.component.css'
})
export class CustomerDashboardComponent implements OnInit {

  user: LoginResponse | null = null;
  userId!: number;
  customerOrders: CustomerOrderResponseModel[] = [];

  stats = { total: 0, pending: 0, active: 0, delivered: 0, duePayments: 0 };
  recentOrders: CustomerOrderResponseModel[] = [];
  
  readonly imageUrl = environment.apiUrl.replace(/api\/$/, '') + 'uploads/customer/';

  constructor(
    private storage: StorageService,
    private auth: AuthService,
    private orderService: CustomerOrderService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.user = this.storage.getUser();
    if (this.user?.userId) {
      this.userId = this.user.userId;
      this.loadDashboardData();
    }
  }

  loadDashboardData(): void {
    this.orderService.findAll().subscribe({
      next: (orders) => {
        this.customerOrders = orders ? orders.filter(o => o.customerId === this.userId) : [];
        
        // 📊 KPI Metrics Calculations Engine
        this.stats.total = this.customerOrders.length;
        this.stats.pending = this.customerOrders.filter(o => o.status === 'PENDING').length;
        this.stats.active = this.customerOrders.filter(o => ['CONFIRMED', 'PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY'].includes(o.status)).length;
        this.stats.delivered = this.customerOrders.filter(o => o.status === 'DELIVERED').length;
        
        // COD বা অনলাইন বকেয়া পেমেন্ট ট্র্যাকিং
        this.stats.duePayments = this.customerOrders.filter(o => o.paymentStatus !== 'PAID').length;

        // রিসেন্ট ৫টি অর্ডার টাইমস্ট্যাম্প অনুযায়ী সর্টিং
        this.recentOrders = [...this.customerOrders]
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
          .slice(0, 5);

        this.cdr.markForCheck();
      },
      error: (err) => console.error("Cluster metrics fetching falled:", err)
    });
  }

  logout(): void {
    this.auth.logout();
    this.storage.removeData(KEYS.CUSTOMER);
  }
}