import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-commercial-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './commercial-dashboard.component.html',
  styleUrls: ['./commercial-dashboard.component.css']
})
export class CommercialDashboardComponent implements OnInit {
  userName = '';

  kpis = [
    { label: 'Active Import LCs', value: '12 LCs', trend: 20, icon: 'bi-bank', color: 'primary' },
    { label: 'LC Outstanding Dues', value: '৳5.8M', trend: 15, icon: 'bi-wallet2', color: 'warning' },
    { label: 'Customs Pending', value: '3 Shipments', trend: -50, icon: 'bi-file-earmark-lock', color: 'danger' },
    { label: 'Docs Approved today', value: '6 Sets', trend: 12, icon: 'bi-file-earmark-check', color: 'success' }
  ];

  lcs = [
    { lcNumber: 'LC-BKB-9921', bank: 'Standard Chartered Bank', value: 3400000, status: 'Approved', type: 'Import' },
    { lcNumber: 'LC-EBL-8871', bank: 'Eastern Bank Ltd', value: 2400000, status: 'Document Verification', type: 'Import' },
    { lcNumber: 'LC-HSBC-0021', bank: 'HSBC Dhaka', value: 12000000, status: 'Under Review', type: 'Export' }
  ];

  documents = [
    { name: 'Commercial Invoice #CI-99', type: 'PDF', status: 'Approved', date: 'Today' },
    { name: 'Bill of Lading #BL-8012', type: 'PDF', status: 'Pending Review', date: 'Today' },
    { name: 'Certificate of Origin #CO-332', type: 'Scan', status: 'Approved', date: 'Yesterday' }
  ];

  constructor(
    private storage: StorageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (user) {
      this.userName = user.name || 'Commercial Officer';
    }
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }
}
