import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-driver-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './driver-dashboard.component.html',
  styleUrls: ['./driver-dashboard.component.css']
})
export class DriverDashboardComponent implements OnInit {
  userName = '';

  kpis = [
    { label: 'Trips Today', value: '4 Deliveries', trend: 20, icon: 'bi-geo-alt', color: 'primary' },
    { label: 'Vehicle Health', value: '88% Health', trend: 0, icon: 'bi-wrench-adjustable', color: 'success' },
    { label: 'Fuel level', value: '78% Full', trend: -5, icon: 'bi-fuel-pump', color: 'warning' },
    { label: 'Distance covered', value: '142.6 km', trend: 15, icon: 'bi-speedometer2', color: 'info' }
  ];

  routes = [
    { seq: 1, destination: 'Dhaka Warehouse A (Outbound pickup)', load: '12 Crates', status: 'Completed' },
    { seq: 2, destination: 'Chittagong Distribution Hub (Consignment #4)', load: '45.5 kg cargo', status: 'In Progress' },
    { seq: 3, destination: 'Comilla Delivery Terminal', load: '3 Packages', status: 'Pending' },
    { seq: 4, destination: 'Feni Supply Depot (Inbound return)', load: 'Empty Pallets', status: 'Pending' }
  ];

  constructor(
    private storage: StorageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (user) {
      this.userName = user.name || 'Driver Node';
    }
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }
}
