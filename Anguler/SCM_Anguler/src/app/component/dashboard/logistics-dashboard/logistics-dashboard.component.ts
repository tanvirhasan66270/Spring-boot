import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { LogisticsOfficerService } from '../../../service/logistics-officer.service';
import { LogisticsOfficerResponseModel } from '../../shared/model/logisticsOfficer';
import { LoginResponse } from '../../../auth/Model/authModel';
import { ShipmentService } from '../../../service/shipment.service';
import { WarehouseService } from '../../../service/warehouse.service';
import { NotificationService } from '../../../system/service/notification.service';
import { ActivityLogService } from '../../../service/activity.log.service';
import { DeliveryTripService } from '../../../service/delivery-trip.service';
import { InventoryService } from '../../../service/inventory.service';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';
import { NotificationModel } from '../../../system/NotificationModel';
import { ActivityLogModel } from '../../shared/model/ActivityLogModel';

@Component({
  selector: 'app-logistics-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './logistics-dashboard.component.html',
  styleUrls: ['./logistics-dashboard.component.css'],
})
export class LogisticsDashboardComponent implements OnInit {
  userName = '';
  userId!: number;
  logisticsOfficer: LogisticsOfficerResponseModel | null = null;
  user: LoginResponse | null = null;
  showSettings = false;
  loading = true;

  totalShipments = 0;
  activeTrips = 0;
  inventoryMovements = 0;
  warehouseCapacityPercent = 0;

  kpis = [
    {
      label: 'Outbound Shipments',
      value: '0 Active',
      trend: 0,
      icon: 'bi-truck',
      color: 'primary',
    },
    {
      label: 'Delivery Trips',
      value: '0 Trips',
      trend: 0,
      icon: 'bi-signpost-split',
      color: 'success',
    },
    {
      label: 'Warehouse Capacity',
      value: '0%',
      trend: 0,
      icon: 'bi-building-up',
      color: 'warning',
    },
    {
      label: 'Inventory Movements',
      value: '0 Logs',
      trend: 0,
      icon: 'bi-arrow-left-right',
      color: 'info',
    },
  ];

  shipments: any[] = [];
  warehouses: any[] = [];
  inventories: any[] = [];
  notifications: NotificationModel[] = [];
  activities: ActivityLogModel[] = [];

  constructor(
    private storage: StorageService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private logisticsOfficerService: LogisticsOfficerService,
    private shipmentService: ShipmentService,
    private warehouseService: WarehouseService,
    private notificationService: NotificationService,
    private activityLogService: ActivityLogService,
    private deliveryTripService: DeliveryTripService,
    private inventoryService: InventoryService,
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name || 'Logistics Officer';
    this.userId = user.userId;
    this.loadLogisticsOfficer();
    this.loadDashboardData();
    this.loadNotifications();
    this.loadActivityLogs();
  }

  loadDashboardData() {
    this.shipmentService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        this.totalShipments = all.length;
        const active = all.filter(
          (s: any) =>
            s.status === 'IN_TRANSIT' || s.status === 'DISPATCHING' || s.status === 'PENDING',
        );
        this.kpis[0] = {
          ...this.kpis[0],
          value: `${active.length} Active`,
          trend: active.length,
        };
        this.shipments = all.slice(0, 5).map((s: any) => ({
          id: s.id,
          vehicle: s.vehicleNumber || 'N/A',
          destination: s.origin || 'N/A',
          eta: s.estimatedDelivery || 'Pending',
          status: s.status || 'PENDING',
        }));
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });

    this.deliveryTripService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        const activeTrips = all.filter(
          (t: any) => t.status === 'IN_TRANSIT' || t.status === 'PENDING',
        );
        this.activeTrips = activeTrips.length;
        this.kpis[1] = {
          ...this.kpis[1],
          value: `${activeTrips.length} Trips`,
          trend: activeTrips.length,
        };
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    this.warehouseService.getAll().subscribe({
      next: (data) => {
        const all = data || [];
        this.warehouses = all.map((w: any) => ({
          id: w.id,
          name: w.name || 'Warehouse',
          capacity: w.capacity || 0,
          location: w.location || '',
        }));
        this.computeWarehouseCapacity();
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    this.inventoryService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        this.inventories = all;
        this.inventoryMovements = all.length;
        this.kpis[3] = {
          ...this.kpis[3],
          value: `${all.length} Logs`,
          trend: all.length,
        };
        this.computeWarehouseCapacity();
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  private computeWarehouseCapacity(): void {
    if (this.warehouses.length === 0) return;

    const usedByWarehouse: Record<number, number> = {};
    this.inventories.forEach((inv: any) => {
      const whId = inv.warehouseId;
      if (!usedByWarehouse[whId]) usedByWarehouse[whId] = 0;
      usedByWarehouse[whId] += inv.quantityOnHand || 0;
    });

    let totalUsed = 0;
    let totalCap = 0;
    for (const wh of this.warehouses) {
      totalCap += wh.capacity || 0;
      totalUsed += usedByWarehouse[wh.id] || 0;
    }

    this.warehouseCapacityPercent =
      totalCap > 0 ? Math.min(100, Math.round((totalUsed / totalCap) * 100)) : 0;
    const capacityDiff = this.warehouseCapacityPercent - 80;

    this.kpis[2] = {
      ...this.kpis[2],
      value: `${this.warehouseCapacityPercent}%`,
      trend: capacityDiff,
    };

    this.warehouses = this.warehouses.map((w) => ({
      ...w,
      usedPercent:
        w.capacity > 0
          ? Math.min(100, Math.round(((usedByWarehouse[w.id] || 0) / w.capacity) * 100))
          : 0,
    }));

    this.cdr.markForCheck();
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

  loadActivityLogs(): void {
    this.activityLogService.findByModule('SHIPMENT').subscribe({
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

  toggleSettings(): void {
    this.showSettings = !this.showSettings;
  }

  closeSettings(): void {
    this.showSettings = false;
  }

  getNotificationIcon(type: string): string {
    const icons: Record<string, string> = {
      SHIPMENT: 'bi-truck',
      TRIP_ALERT: 'bi-exclamation-triangle',
      REPORT_APPROVED: 'bi-check-circle',
    };
    return icons[type] || 'bi-bell';
  }

  getNotificationColor(type: string): string {
    const colors: Record<string, string> = {
      SHIPMENT: 'text-primary',
      TRIP_ALERT: 'text-warning',
      REPORT_APPROVED: 'text-success',
    };
    return colors[type] || 'text-secondary';
  }

  getActionIcon(action: string): string {
    const icons: Record<string, string> = {
      CREATE: 'bi-plus-circle',
      UPDATE: 'bi-pencil',
      DELETE: 'bi-trash',
      LOGIN: 'bi-box-arrow-in-right',
    };
    return icons[action] || 'bi-circle';
  }

  getActionColor(action: string): string {
    const colors: Record<string, string> = {
      CREATE: 'text-success',
      UPDATE: 'text-primary',
      DELETE: 'text-danger',
      LOGIN: 'text-info',
    };
    return colors[action] || 'text-secondary';
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  getCapacityTrendText(): string {
    const diff = this.warehouseCapacityPercent - 80;
    if (diff > 0) return `+${diff}% above target`;
    if (diff < 0) return `${diff}% below target`;
    return 'At target capacity';
  }

  loadLogisticsOfficer(): void {
    this.logisticsOfficerService.getLogisticsOfficerByUserId(this.userId).subscribe({
      next: (res) => {
        this.logisticsOfficer = res;
        this.storage.saveData(KEYS.LOGISTICS_OFFICER, res);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['']);
  }
}
