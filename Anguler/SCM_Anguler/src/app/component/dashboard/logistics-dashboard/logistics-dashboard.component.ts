import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-logistics-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './logistics-dashboard.component.html',
  styleUrls: ['./logistics-dashboard.component.css']
})
export class LogisticsDashboardComponent implements OnInit {
  userName = '';

  kpis = [
    { label: 'Outbound Shipments', value: '18 Active', trend: 14, icon: 'bi-truck', color: 'primary' },
    { label: 'Warehouse Capacity', value: '78.5%', trend: 4, icon: 'bi-building-up', color: 'warning' },
    { label: 'Inventory Movements', value: '34 Logs', trend: 8, icon: 'bi-arrow-left-right', color: 'info' },
    { label: 'Est. Delivery Transit', value: '1.2 Days', trend: -8, icon: 'bi-speedometer2', color: 'success' }
  ];

  shipments = [
    { id: 'SH-8840', vehicle: 'D-112 (Hino Truck)', destination: 'Sylhet Warehouse', eta: '4 Hours', status: 'IN_TRANSIT' },
    { id: 'SH-8839', vehicle: 'D-909 (Covered Van)', destination: 'Jessore Outlet', eta: 'Completed', status: 'DELIVERED' },
    { id: 'SH-8841', vehicle: 'D-334 (Micro-Carrier)', destination: 'Mymensingh Station', eta: '12 Hours', status: 'DISPATCHING' }
  ];

  warehouses = [
    { name: 'Dhaka Central Depot', used: 88, total: '10,000 sq.ft' },
    { name: 'Chittagong Hub A', used: 64, total: '15,000 sq.ft' }
  ];

  constructor(
    private storage: StorageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (user) {
      this.userName = user.name || 'Logistics Officer';
    }
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }
}
