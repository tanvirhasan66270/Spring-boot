import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-procurement-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './procurement-dashboard.component.html',
  styleUrls: ['./procurement-dashboard.component.css']
})
export class ProcurementDashboardComponent implements OnInit {
  userName = '';

  kpis = [
    { label: 'Purchase Requests', value: '18 Requisitions', trend: 20, icon: 'bi-file-earmark-text', color: 'teal' },
    { label: 'RFQ Sent Out', value: '12 Queries', trend: 5, icon: 'bi-envelope-check', color: 'info' },
    { label: 'Pending Purchase Orders', value: '6 Pending', trend: -12, icon: 'bi-cart-check', color: 'warning' },
    { label: 'Budget Sourced', value: '64.8%', trend: 4.5, icon: 'bi-wallet2', color: 'success' }
  ];

  rfqs = [
    { id: 'RFQ-902', item: 'Heavy Duty Needle Sewing Machine', qty: 2, status: 'Active', supplier: 'Vertex Machines Ltd' },
    { id: 'RFQ-899', item: 'Polyester Thread Spools Batch X', qty: 50, status: 'Draft', supplier: 'Pending Sourcing' },
    { id: 'RFQ-881', item: 'Universal Machinery Grease Tub', qty: 10, status: 'Awarded', supplier: 'Lubricants Corp' }
  ];

  shortages = [
    { item: 'Industrial Sewing Thread Spool', stock: '2 Units', threshold: '10 Units', urgency: 'CRITICAL' },
    { item: 'Premium Anti-Rust Coating', stock: '5 Litres', threshold: '15 Litres', urgency: 'HIGH' }
  ];

  constructor(
    private storage: StorageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (user) {
      this.userName = user.name || 'Procurement Officer';
    }
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }
}
