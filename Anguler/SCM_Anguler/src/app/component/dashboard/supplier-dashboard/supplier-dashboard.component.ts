import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-supplier-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './supplier-dashboard.component.html',
  styleUrls: ['./supplier-dashboard.component.css']
})
export class SupplierDashboardComponent implements OnInit {
  userName = '';

  kpis = [
    { label: 'Purchase Orders', value: '14 POs', trend: 16, icon: 'bi-inboxes', color: 'primary' },
    { label: 'Pending Delivery', value: '3 Consignments', trend: 0, icon: 'bi-truck-flatbed', color: 'warning' },
    { label: 'Outstanding Payments', value: '৳280,000.00', trend: 14, icon: 'bi-currency-exchange', color: 'success' },
    { label: 'Supply Accuracy', value: '98.8%', trend: 1.2, icon: 'bi-patch-check', color: 'success' }
  ];

  pos = [
    { poNumber: 'PO-8812', date: '2026-07-08', amount: 25000, deliveryDue: '15 July', status: 'CONFIRMED' },
    { poNumber: 'PO-8813', date: '2026-07-09', amount: 85000, deliveryDue: '19 July', status: 'PROCESSING' },
    { poNumber: 'PO-8798', date: '2026-06-30', amount: 55000, deliveryDue: 'Completed', status: 'SHIPPED' }
  ];

  rfqs = [
    { id: 'RFQ-902', item: 'Sewing Accessories Batch C', closingDate: '15 July', status: 'Pending Bid' },
    { id: 'RFQ-901', item: 'Polyester Thread Spools Batch X', closingDate: '13 July', status: 'Bid Submitted' }
  ];

  constructor(
    private storage: StorageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (user) {
      this.userName = user.name || 'Supplier Node';
    }
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }
}
