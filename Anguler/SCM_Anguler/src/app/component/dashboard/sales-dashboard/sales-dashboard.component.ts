import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-sales-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sales-dashboard.component.html',
  styleUrls: ['./sales-dashboard.component.css']
})
export class SalesDashboardComponent implements OnInit {
  userName = '';

  kpis = [
    { label: 'Sales Today', value: '৳840,000.00', trend: 22, icon: 'bi-cash-coin', color: 'success' },
    { label: 'Pipeline Leads', value: '45 Active', trend: 10, icon: 'bi-funnel', color: 'primary' },
    { label: 'Conversion Rate', value: '28.4%', trend: 5, icon: 'bi-graph-up', color: 'info' },
    { label: 'Pending Quotations', value: '11 Quotes', trend: -8, icon: 'bi-file-earmark-medical', color: 'warning' }
  ];

  leads = [
    { company: 'Abul Khair Group', contact: 'M. Hossain', value: 500000, stage: 'Proposal Sent', probability: '80%' },
    { company: 'Beximco Apparels', contact: 'F. Chowdhury', value: 1200000, stage: 'Negotiation', probability: '65%' },
    { company: 'Square Textiles', contact: 'S. Rayhan', value: 850000, stage: 'Discovery', probability: '30%' }
  ];

  customers = [
    { name: 'Apex Footwear Ltd', contact: '+8801712...', ordersCount: 14, spent: 450000 },
    { name: 'Ananta Garments', contact: '+8801823...', ordersCount: 9, spent: 280000 }
  ];

  constructor(
    private storage: StorageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (user) {
      this.userName = user.name || 'Sales Officer';
    }
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }
}
