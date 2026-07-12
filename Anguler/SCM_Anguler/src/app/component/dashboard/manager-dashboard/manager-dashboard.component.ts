import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { ManagerResponseModel } from '../../shared/model/manager';
import { ManagerService } from '../../../service/manager.service';
import { LoginResponse } from '../../../auth/Model/authModel';

@Component({
  selector: 'app-manager-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './manager-dashboard.component.html',
  styleUrls: ['./manager-dashboard.component.css']
})
export class ManagerDashboardComponent implements OnInit {
  userName = '';

  kpis = [
    { label: 'Total Revenue', value: '৳4.8M', trend: 14, icon: 'bi-graph-up-arrow', color: 'success' },
    { label: 'Monthly Expenses', value: '৳1.9M', trend: -2, icon: 'bi-cash-stack', color: 'danger' },
    { label: 'Net Profit Margin', value: '60.4%', trend: 6, icon: 'bi-percent', color: 'primary' },
    { label: 'Employee Attendance', value: '92.6%', trend: 1.2, icon: 'bi-people', color: 'info' }
  ];

  approvals = [
    { id: 'REQ-102', type: 'Procurement Budget Raise', requester: 'S. Khan (Procurement)', amount: 150000, date: 'Today' },
    { id: 'REQ-104', type: 'Import Shipping Document Waiver', requester: 'A. Rahman (Commercial)', amount: 0, date: 'Today' },
    { id: 'REQ-105', type: 'Supplier Rate Revision Contract', requester: 'M. Ali (Supplier)', amount: 22000, date: 'Yesterday' }
  ];

  deptPerformance = [
    { name: 'Sourcing & SCM', score: 85, color: 'success' },
    { name: 'Logistics & Fleet', score: 78, color: 'primary' },
    { name: 'Quality Control', score: 92, color: 'purple' },
    { name: 'Commercial Imports', score: 70, color: 'warning' }
  ];


  userId!: number;

  manager: ManagerResponseModel | null = null;

  user: LoginResponse | null = null;


  constructor(
    private storage: StorageService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private managerService: ManagerService,
  ) { }

  ngOnInit(): void {

    const user = this.storage.getUser();

    if (!user) {
      return;
    }

    this.userName = user.name;
    this.userId = user.userId;

    console.log(this.userId);

    this.loadManager();
  }


  loadManager(): void {
    console.log("User ID: " + this.userId)
    this.managerService.getManagerByUserId(this.userId).subscribe({
      next: (res) => {
        this.manager = res;
        console.log(this.manager);
        this.storage.saveData(KEYS.MANAGER, res);
        this.cdr.markForCheck();

      },
      error: (err) => console.log(err)
    });
  }

  approve(id: string): void {
    this.approvals = this.approvals.filter(a => a.id !== id);
    alert(`Authorized approval request ${id}`);
    this.cdr.markForCheck();
  }

  reject(id: string): void {
    this.approvals = this.approvals.filter(a => a.id !== id);
    alert(`Declined approval request ${id}`);
    this.cdr.markForCheck();
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }
}
