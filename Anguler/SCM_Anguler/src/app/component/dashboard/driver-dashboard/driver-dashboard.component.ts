import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { DriverService } from '../../../service/driver.service';
import { DriverResponseModel } from '../../shared/model/driverModel';
import { LoginResponse } from '../../../auth/Model/authModel';
import { DeliveryTripService } from '../../../service/delivery-trip.service';
import { VehicleService } from '../../../service/vehicle.service';
import { VehicleResponseModel } from '../../shared/model/vehicleModel';
import { NotificationService } from '../../../system/service/notification.service';
import { NotificationModel } from '../../../system/NotificationModel';
import { ActivityLogService } from '../../../service/activity.log.service';
import { ActivityLogModel } from '../../shared/model/ActivityLogModel';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';

@Component({
  selector: 'app-driver-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './driver-dashboard.component.html',
  styleUrls: ['./driver-dashboard.component.css'],
})
export class DriverDashboardComponent implements OnInit {
  userName = '';
  userId!: number;
  driver: DriverResponseModel | null = null;
  user: LoginResponse | null = null;
  showSettings = false;

  fuelLevel = 0;
  vehicleStatus = 'N/A';
  totalDeliveries = 0;
  totalTrips = 0;
  deliveredCount = 0;
  inTransitCount = 0;
  pendingCount = 0;
  cancelledCount = 0;
  distanceCovered = 0;

  vehicle: VehicleResponseModel | null = null;

  notifications: NotificationModel[] = [];
  activities: ActivityLogModel[] = [];

  kpis = [
    { label: 'Total Trips', value: '0', trend: 0, icon: 'bi-geo-alt', color: 'primary' },
    {
      label: 'Vehicle Health',
      value: 'N/A',
      trend: 0,
      icon: 'bi-wrench-adjustable',
      color: 'success',
    },
    { label: 'Fuel Level', value: 'N/A', trend: 0, icon: 'bi-fuel-pump', color: 'warning' },
    { label: 'Distance Covered', value: '0 km', trend: 0, icon: 'bi-speedometer2', color: 'info' },
  ];

  routes: any[] = [];
  loading = true;

  constructor(
    private storage: StorageService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private driverService: DriverService,
    private tripService: DeliveryTripService,
    private vehicleService: VehicleService,
    private notificationService: NotificationService,
    private activityLogService: ActivityLogService,
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name || 'Driver';
    this.userId = user.userId;
    this.loadDriver();
    this.loadTrips();
    this.loadVehicle();
    this.loadNotifications();
    this.loadActivities();
  }

  loadTrips() {
    this.tripService.findAll().subscribe({
      next: (data) => {
        const allTrips = data || [];
        this.totalTrips = allTrips.length;
        this.deliveredCount = allTrips.filter((t: any) => t.status === 'DELIVERED').length;
        this.inTransitCount = allTrips.filter((t: any) => t.status === 'IN_TRANSIT').length;
        this.pendingCount = allTrips.filter((t: any) => t.status === 'PENDING').length;
        this.cancelledCount = allTrips.filter((t: any) => t.status === 'CANCELLED').length;
        this.totalDeliveries = this.deliveredCount;
        this.distanceCovered = this.deliveredCount * 35;

        this.kpis[0] = {
          label: 'Total Trips',
          value: `${this.totalTrips}`,
          trend: 0,
          icon: 'bi-geo-alt',
          color: 'primary',
        };

        this.kpis[3] = {
          label: 'Distance Covered',
          value: `${this.distanceCovered} km`,
          trend: 0,
          icon: 'bi-speedometer2',
          color: 'info',
        };

        const trips = allTrips.slice(0, 8);
        this.routes = trips.map((t: any, i: number) => ({
          seq: i + 1,
          destination: t.customerAddress || 'Unknown',
          load: t.remarks || 'Standard Load',
          status: t.status || 'PENDING',
          plateNumber: t.vehiclePlateNumber || 'N/A',
          customer: t.recipientName || 'N/A',
          createdAt: t.createdAt,
        }));
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  loadDriver(): void {
    this.driverService.getDriverByUserId(this.userId).subscribe({
      next: (res) => {
        this.driver = res;
        if (res) {
          this.kpis[1] = {
            label: 'Vehicle Health',
            value: res.vehicleType || 'N/A',
            trend: 0,
            icon: 'bi-wrench-adjustable',
            color: 'success',
          };
        }
        this.storage.saveData(KEYS.DRIVER, res);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  loadVehicle(): void {
    this.vehicleService.findAll().subscribe({
      next: (vehicles) => {
        const assigned = (vehicles || []).find((v) => v.driverId === this.userId);
        if (assigned) {
          this.vehicle = assigned;
          this.fuelLevel = assigned.fuelLevel;
          this.vehicleStatus = assigned.status;
          this.kpis[2] = {
            label: 'Fuel Level',
            value: `${assigned.fuelLevel}%`,
            trend: 0,
            icon: 'bi-fuel-pump',
            color:
              assigned.fuelLevel <= 20
                ? 'danger'
                : assigned.fuelLevel <= 50
                  ? 'warning'
                  : 'success',
          };
        }
        this.cdr.markForCheck();
      },
      error: () => {},
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
      error: () => {
        this.activityLogService.findAll().subscribe({
          next: (data) => {
            this.activities = (data || []).slice(0, 5);
            this.cdr.markForCheck();
          },
          error: () => {},
        });
      },
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'DELIVERED':
        return 'bg-success text-white';
      case 'IN_TRANSIT':
        return 'bg-primary text-white';
      case 'PENDING':
        return 'bg-warning text-dark';
      case 'CANCELLED':
        return 'bg-danger text-white';
      default:
        return 'bg-secondary text-white';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'DELIVERED':
        return 'Delivered';
      case 'IN_TRANSIT':
        return 'In Transit';
      case 'PENDING':
        return 'Pending';
      case 'CANCELLED':
        return 'Cancelled';
      default:
        return status;
    }
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'SHIPMENT':
        return 'bi-truck';
      case 'TRIP_ALERT':
        return 'bi-exclamation-triangle';
      case 'REPORT_APPROVED':
        return 'bi-check-circle';
      default:
        return 'bi-bell';
    }
  }

  getActivityIcon(action: string): string {
    switch (action) {
      case 'CREATE':
        return 'bi-plus-circle text-success';
      case 'UPDATE':
        return 'bi-pencil text-primary';
      case 'DELETE':
        return 'bi-trash text-danger';
      case 'LOGIN':
        return 'bi-box-arrow-in-right text-info';
      default:
        return 'bi-clock text-secondary';
    }
  }

  getTimeAgo(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const mins = Math.floor(diff / 60000);
    if (mins < 1) return 'Just now';
    if (mins < 60) return `${mins}m ago`;
    const hours = Math.floor(mins / 60);
    if (hours < 24) return `${hours}h ago`;
    const days = Math.floor(hours / 24);
    return `${days}d ago`;
  }

  toggleSettings(): void {
    this.showSettings = !this.showSettings;
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['']);
  }
}
