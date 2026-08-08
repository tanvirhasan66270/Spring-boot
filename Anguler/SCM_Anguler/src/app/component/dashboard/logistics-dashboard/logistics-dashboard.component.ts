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
import { DeliveryTripService } from '../../../service/delivery-trip.service';
import { InventoryService } from '../../../service/inventory.service';
import { StockMovementService } from '../../../service/stock-movement.service';
import { VehicleService } from '../../../service/vehicle.service';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';
import { NotificationModel } from '../../../system/NotificationModel';

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

  // Key Metrics
  totalShipments = 0;
  activeShipments = 0;
  delayedShipments = 0;
  
  totalTrips = 0;
  activeTrips = 0;
  
  totalVehicles = 0;
  availableVehicles = 0;
  
  warehouseCapacityPercent = 0;
  
  totalMovements = 0;
  movementsToday = 0;
  inventoryTrend = 0;
  
  shipmentsTrend = 0;
  delayedTrend = 0;
  tripsTrend = 0;
  warehouseTrend = 0;

  // Grid Data
  dispatchSchedule: any[] = [];
  liveTimeline: any[] = [];
  dispatchQueue: any[] = [];
  
  // Chart Data
  fleetStats = { available: { val: 0, pct: 0, offset: 0, dash: '0 100' }, onRoute: { val: 0, pct: 0, offset: 0, dash: '0 100' }, maintenance: { val: 0, pct: 0, offset: 0, dash: '0 100' }, offline: { val: 0, pct: 0, offset: 0, dash: '0 100' } };
  inventoryStats = { in: { val: 0, pct: 0, offset: 0, dash: '0 100' }, out: { val: 0, pct: 0, offset: 0, dash: '0 100' }, transfer: { val: 0, pct: 0, offset: 0, dash: '0 100' } };
  
  // Shipment Performance Line Chart Data
  shipmentPerformancePoints = "M 0 140 L 500 140";
  shipmentPerformanceFill = "M 0 140 L 500 140 Z";
  shipmentDataPoints: any[] = [];
  shipmentHistoryStats = { total: 0, delivered: 0, inTransit: 0, delayed: 0 };
  chartLabels: { x: number, text: string }[] = [];
  
  monthsList = [
    { value: 0, label: 'January' }, { value: 1, label: 'February' }, { value: 2, label: 'March' },
    { value: 3, label: 'April' }, { value: 4, label: 'May' }, { value: 5, label: 'June' },
    { value: 6, label: 'July' }, { value: 7, label: 'August' }, { value: 8, label: 'September' },
    { value: 9, label: 'October' }, { value: 10, label: 'November' }, { value: 11, label: 'December' }
  ];
  selectedPerformanceMonth = new Date().getMonth();
  selectedPerformanceYear = new Date().getFullYear();
  allShipments: any[] = [];

  warehouses: any[] = [];
  alerts: any[] = [];
  
  // Modal State
  showActivityModal = false;
  loadingActivities = false;
  
  showWarehouseModal = false;
  showShipmentModal = false;
  showDispatchModal = false;
  showScheduleModal = false;
  showMapModal = false;
  
  hasActiveTrackingDevice = false;

  constructor(
    private storage: StorageService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private logisticsOfficerService: LogisticsOfficerService,
    private shipmentService: ShipmentService,
    private warehouseService: WarehouseService,
    private notificationService: NotificationService,
    private deliveryTripService: DeliveryTripService,
    private inventoryService: InventoryService,
    private vehicleService: VehicleService,
    private stockMovementService: StockMovementService
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
  }

  loadDashboardData() {
    // 1. Shipments
    this.shipmentService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        this.allShipments = all;
        this.totalShipments = all.length;
        
        const active = all.filter((s: any) => s.status === 'IN_TRANSIT' || s.status === 'DISPATCHING' || s.status === 'PENDING');
        this.activeShipments = active.length;
        
        const delayed = all.filter((s: any) => s.status === 'DELAYED');
        this.delayedShipments = delayed.length || 0;
        
        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(yesterday.getDate() - 1);
        
        const shipmentsToday = all.filter((s: any) => {
            if (!s.createdAt) return false;
            const d = new Date(s.createdAt);
            return d.getDate() === today.getDate() && d.getMonth() === today.getMonth() && d.getFullYear() === today.getFullYear();
        });
        const shipmentsYesterday = all.filter((s: any) => {
            if (!s.createdAt) return false;
            const d = new Date(s.createdAt);
            return d.getDate() === yesterday.getDate() && d.getMonth() === yesterday.getMonth() && d.getFullYear() === yesterday.getFullYear();
        });
        
        if (shipmentsYesterday.length > 0) {
            this.shipmentsTrend = Math.round(((shipmentsToday.length - shipmentsYesterday.length) / shipmentsYesterday.length) * 100);
        } else if (shipmentsToday.length > 0) {
            this.shipmentsTrend = 100;
        } else {
            this.shipmentsTrend = 0;
        }
        
        const delayedToday = shipmentsToday.filter((s: any) => s.status === 'DELAYED').length;
        const delayedYesterday = shipmentsYesterday.filter((s: any) => s.status === 'DELAYED').length;
        this.delayedTrend = delayedToday - delayedYesterday;
        
        // Build Dispatch Queue
        this.dispatchQueue = all.slice(0, 5).map((s: any, index: number) => ({
          id: s.id || `SHP-125${6 + index}`,
          customer: s.receiverName || 'Apex Logistics',
          driver: s.driverName || 'Rahim Uddin',
          vehicle: s.vehicleNumber || `TR-0${index + 1}`,
          destination: s.destination || s.origin || 'Dhaka',
          priority: index < 2 ? 'High' : (index === 2 ? 'Low' : 'Medium'),
          eta: s.estimatedDelivery || '10:30 AM',
          status: s.status || 'PENDING',
        }));

        // Build Live Timeline dynamically
        this.liveTimeline = all.slice(0, 4).map((s: any, index: number) => {
            const colors: any = { 'PENDING': 'warning', 'DISPATCHING': 'primary', 'IN_TRANSIT': 'primary', 'DELIVERED': 'success', 'DELAYED': 'danger' };
            const statusLabel: any = { 'PENDING': 'Processing', 'DISPATCHING': 'Dispatching', 'IN_TRANSIT': 'In Transit', 'DELIVERED': 'Delivered', 'DELAYED': 'Delayed' };
            
            const currentStatus = s.status || 'PENDING';
            let etaText = 'ETA: 4h';
            let timeStr = '10:30 AM';
            
            if (s.estimatedDelivery) {
              const d = new Date(s.estimatedDelivery);
              if (!isNaN(d.getTime())) {
                const now = new Date();
                const diffMs = d.getTime() - now.getTime();
                if (diffMs > 0) {
                   const hours = Math.round(diffMs / (1000 * 60 * 60));
                   etaText = `ETA: ${hours}h`;
                }
                timeStr = d.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
              }
            } else if (s.createdAt) {
               const d = new Date(s.createdAt);
               if (!isNaN(d.getTime())) {
                  timeStr = d.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
               }
            }

            const orderNum = s.shipmentNumber || s.poId || s.id || (index + 1);

            return {
                order: `#${orderNum}`,
                status: statusLabel[currentStatus] || 'Processing',
                time: currentStatus === 'DELIVERED' ? 'Completed' : etaText,
                time2: timeStr,
                color: colors[currentStatus] || 'success'
            };
        });
        
        if (this.liveTimeline.length === 0) {
            this.generateDummyTimeline();
        }

        this.generatePerformanceChartData(all);
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.generateDummyTimeline();
        this.loading = false;
        this.cdr.markForCheck();
      },
    });

    // 2. Delivery Trips
    this.deliveryTripService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        this.totalTrips = all.length || 0; // Removing fallback logic to make dynamic
        
        const active = all.filter((t: any) => t.status === 'IN_TRANSIT' || t.status === 'PENDING');
        this.activeTrips = active.length || 0; // Removing fallback

        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(yesterday.getDate() - 1);
        
        const tripsToday = all.filter((t: any) => {
            if (!t.createdAt && !t.estimatedArrival) return false;
            const d = new Date(t.createdAt || t.estimatedArrival);
            return d.getDate() === today.getDate() && d.getMonth() === today.getMonth() && d.getFullYear() === today.getFullYear();
        });
        const tripsYesterday = all.filter((t: any) => {
            if (!t.createdAt && !t.estimatedArrival) return false;
            const d = new Date(t.createdAt || t.estimatedArrival);
            return d.getDate() === yesterday.getDate() && d.getMonth() === yesterday.getMonth() && d.getFullYear() === yesterday.getFullYear();
        });
        
        if (tripsYesterday.length > 0) {
            this.tripsTrend = Math.round(((tripsToday.length - tripsYesterday.length) / tripsYesterday.length) * 100);
        } else if (tripsToday.length > 0) {
            this.tripsTrend = 100;
        } else {
            this.tripsTrend = 0;
        }

        this.dispatchSchedule = all.slice(0, 5).map((t: any) => ({
          tripId: t.tripNumber || `TR-${t.id || '01'}`,
          destination: t.destination || 'Dhaka',
          time: t.estimatedArrival || '10:30 AM',
          status: t.status || 'Scheduled'
        }));
        
        if (this.dispatchSchedule.length === 0) {
            const dests = ['Dhaka', 'Chattogram', 'Khulna', 'Rajshahi', 'Sylhet'];
            const times = ['10:30 AM', '12:00 PM', '02:30 PM', '04:00 PM', '05:30 PM'];
            const statuses = ['In Transit', 'Scheduled', 'Scheduled', 'Scheduled', 'Pending'];
            this.dispatchSchedule = [1,2,3,4,5].map((i, idx) => ({ tripId: `TR-0${i}`, destination: dests[idx], time: times[idx], status: statuses[idx] }));
        }

        this.cdr.markForCheck();
      },
      error: () => {},
    });

    // 3. Vehicles
    this.vehicleService.findAll().subscribe({
        next: (data) => {
            const all = data || [];
            this.totalVehicles = all.length;
            
            const available = all.filter((v: any) => v.status === 'AVAILABLE').length;
            const onRoute = all.filter((v: any) => v.status === 'IN_TRANSIT' || v.status === 'ON_ROUTE').length;
            const maintenance = all.filter((v: any) => v.status === 'MAINTENANCE').length;
            const offline = all.filter((v: any) => v.status === 'OFFLINE' || v.status === 'INACTIVE').length;
            
            this.availableVehicles = available;
            if (all.length > 0) {
                this.calculateFleetDonut(available, onRoute, maintenance, offline, all.length);
            }
            this.cdr.markForCheck();
        },
        error: () => {
            this.totalVehicles = 0;
            this.availableVehicles = 0;
        }
    });

    // 4. Warehouses
    this.warehouseService.getAll().subscribe({ next: (data) => {
        const all = data || [];
        const whNames = ['Warehouse A (Dhaka)', 'Warehouse B (Chattogram)', 'Warehouse C (Khulna)', 'Warehouse D (Rajshahi)'];
        this.warehouses = all.map((w: any, index: number) => ({
          id: w.id,
          name: w.name || whNames[index % whNames.length],
          capacity: w.capacity || 1000,
          location: w.location || '' }));
        if(this.warehouses.length === 0) {
            this.warehouses = whNames.map((name, i) => ({ id: i, name, capacity: 1000 }));
        }
        this.computeWarehouseCapacity();
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    // 5. Inventory (Only for warehouse capacity)
    this.inventoryService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        this.computeWarehouseCapacity(all);
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    // 5.5 Stock Movements (For Today's Inventory Movement Donut)
    this.stockMovementService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        
        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(yesterday.getDate() - 1);
        
        const movementsTodayList = all.filter((m: any) => {
            if (!m.movedAt) return false;
            const d = new Date(m.movedAt);
            return d.getDate() === today.getDate() && d.getMonth() === today.getMonth() && d.getFullYear() === today.getFullYear();
        });

        const movementsYesterdayList = all.filter((m: any) => {
            if (!m.movedAt) return false;
            const d = new Date(m.movedAt);
            return d.getDate() === yesterday.getDate() && d.getMonth() === yesterday.getMonth() && d.getFullYear() === yesterday.getFullYear();
        });
        
        this.totalMovements = movementsTodayList.length;
        
        let inCount = 0, outCount = 0, transferCount = 0;
        movementsTodayList.forEach((m: any) => {
            if (m.movementType === 'INWARD') inCount++;
            else if (m.movementType === 'OUTWARD') outCount++;
            else transferCount++; // TRANSFER, ADJUSTMENT, etc
        });
        
        this.calculateInventoryDonut(inCount, outCount, transferCount, this.totalMovements);

        if (movementsYesterdayList.length > 0) {
            this.inventoryTrend = Math.round(((this.totalMovements - movementsYesterdayList.length) / movementsYesterdayList.length) * 100);
        } else if (this.totalMovements > 0) {
            this.inventoryTrend = 100;
        } else {
            this.inventoryTrend = 0;
        }

        this.cdr.markForCheck();
      },
      error: () => {},
    });
    
    // 6. Alerts
    this.notificationService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        
        this.alerts = all.slice(0, 4).map((n: any) => {
            let bsType = 'info';
            let icon = 'bi-info-circle';
            
            if (n.type === 'SHIPMENT') { bsType = 'warning'; icon = 'bi-box-seam'; }
            else if (n.type === 'TRIP_ALERT') { bsType = 'danger'; icon = 'bi-exclamation-triangle-fill'; }
            else if (n.type === 'REPORT_APPROVED') { bsType = 'success'; icon = 'bi-check-circle-fill'; }
            else if (n.title?.toLowerCase().includes('capacity')) { bsType = 'danger'; icon = 'bi-exclamation-triangle-fill'; }
            else if (n.title?.toLowerCase().includes('delayed')) { bsType = 'warning'; icon = 'bi-clock-history'; }
            else if (n.title?.toLowerCase().includes('maintenance')) { bsType = 'warning'; icon = 'bi-tools'; }
            else if (n.title?.toLowerCase().includes('assigned')) { bsType = 'info'; icon = 'bi-calendar-check'; }
            
            let timeStr = 'Just now';
            if (n.createdAt) {
                const diffMs = new Date().getTime() - new Date(n.createdAt).getTime();
                const diffMins = Math.floor(diffMs / 60000);
                if (diffMins === 0) timeStr = 'Just now';
                else if (diffMins < 60) timeStr = `${diffMins} mins ago`;
                else if (diffMins < 1440) timeStr = `${Math.floor(diffMins/60)} hours ago`;
                else timeStr = `${Math.floor(diffMins/1440)} days ago`;
            }
            
            let displayMessage = n.title || 'System Notification';
            if (n.message && n.message !== n.title) {
                displayMessage += ` (${n.message})`;
            }

            return {
                type: bsType,
                message: displayMessage,
                time: timeStr,
                icon: icon
            };
        });

        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  // Calculate SVG attributes for Fleet Donut Chart
  private calculateFleetDonut(av: number, rt: number, mt: number, off: number, total: number) {
      if (total === 0) return;
      const avPct = Math.round((av / total) * 100);
      const rtPct = Math.round((rt / total) * 100);
      const mtPct = Math.round((mt / total) * 100);
      const offPct = Math.round((off / total) * 100);

      const offset1 = 25; // CSS starts at top (25 offset for rotate-90)
      const offset2 = 100 - avPct + offset1;
      const offset3 = offset2 - rtPct;
      const offset4 = offset3 - mtPct;

      this.fleetStats = {
          available: { val: av, pct: avPct, offset: offset1, dash: `${avPct} ${100 - avPct}` },
          onRoute: { val: rt, pct: rtPct, offset: offset2, dash: `${rtPct} ${100 - rtPct}` },
          maintenance: { val: mt, pct: mtPct, offset: offset3, dash: `${mtPct} ${100 - mtPct}` },
          offline: { val: off, pct: offPct, offset: offset4, dash: `${offPct} ${100 - offPct}` }
      };
  }

  // Calculate SVG attributes for Inventory Donut Chart
  private calculateInventoryDonut(invIn: number, out: number, tx: number, total: number) {
      if (total === 0) return;
      const inPct = Math.round((invIn / total) * 100);
      const outPct = Math.round((out / total) * 100);
      const txPct = Math.round((tx / total) * 100);

      const offset1 = 25;
      const offset2 = 100 - inPct + offset1;
      const offset3 = offset2 - outPct;

      this.inventoryStats = {
          in: { val: invIn, pct: inPct, offset: offset1, dash: `${inPct} ${100 - inPct}` },
          out: { val: out, pct: outPct, offset: offset2, dash: `${outPct} ${100 - outPct}` },
          transfer: { val: tx, pct: txPct, offset: offset3, dash: `${txPct} ${100 - txPct}` }
      };
  }

  onPerformanceMonthChange(event: any) {
      this.selectedPerformanceMonth = +event.target.value;
      this.generatePerformanceChartData(this.allShipments);
  }

  private generatePerformanceChartData(shipments: any[]) {
      const xSpacing = 500 / 5; // 5 segments for 6 points
      const yMax = 120; // max height (lower is higher on SVG)
      
      const year = this.selectedPerformanceYear;
      const month = this.selectedPerformanceMonth;
      const daysInMonth = new Date(year, month + 1, 0).getDate();
      const monthShort = new Date(year, month, 1).toLocaleString('default', { month: 'short' });
      
      const days = [1, Math.floor(daysInMonth*0.2), Math.floor(daysInMonth*0.4), Math.floor(daysInMonth*0.6), Math.floor(daysInMonth*0.8), daysInMonth];
      
      this.chartLabels = days.map((day, i) => {
          let x = i * xSpacing;
          if (i === 5) x = 465; // adjust last label to fit SVG
          return { x, text: `${monthShort} ${day.toString().padStart(2, '0')}` };
      });

      const vals = [0, 0, 0, 0, 0, 0];
      const currentMonthShipments = shipments.filter(s => {
          if (!s.createdAt) return false;
          const d = new Date(s.createdAt);
          return d.getMonth() === month && d.getFullYear() === year;
      });

      if (currentMonthShipments.length > 0) {
          currentMonthShipments.forEach(s => {
              const d = new Date(s.createdAt);
              const day = d.getDate();
              let closestIdx = 0;
              let minDiff = daysInMonth + 1;
              days.forEach((bDay, idx) => {
                  const diff = Math.abs(day - bDay);
                  if (diff < minDiff) {
                      minDiff = diff;
                      closestIdx = idx;
                  }
              });
              vals[closestIdx]++;
          });
      }
      
      const maxVal = Math.max(...vals, 1);
      
      this.shipmentDataPoints = vals.map((val, i) => ({
          x: i * xSpacing,
          y: yMax - ((val / maxVal) * (yMax - 20))
      }));

      let path = `M 0 140 L 0 ${this.shipmentDataPoints[0].y}`;
      let fillPath = `M 0 140 L 0 ${this.shipmentDataPoints[0].y}`;
      
      for (let i = 0; i < this.shipmentDataPoints.length - 1; i++) {
          const p1 = this.shipmentDataPoints[i];
          const p2 = this.shipmentDataPoints[i + 1];
          const cx = (p1.x + p2.x) / 2;
          const curve = ` C ${cx} ${p1.y}, ${cx} ${p2.y}, ${p2.x} ${p2.y}`;
          path += curve;
          fillPath += curve;
      }
      
      this.shipmentPerformancePoints = path;
      this.shipmentPerformanceFill = fillPath + ` L 500 140 Z`;
      
      this.shipmentHistoryStats = {
          total: shipments.length,
          delivered: shipments.filter((s:any)=>s.status==='DELIVERED').length,
          inTransit: shipments.filter((s:any)=>s.status==='IN_TRANSIT').length,
          delayed: shipments.filter((s:any)=>s.status==='DELAYED').length
      };
  }

  private computeWarehouseCapacity(inventories: any[] = []): void {
    if (this.warehouses.length === 0) return;

    const usedByWarehouse: Record<number, number> = {};
    inventories.forEach((inv: any) => {
      const whId = inv.warehouseId;
      if (!usedByWarehouse[whId]) usedByWarehouse[whId] = 0;
      usedByWarehouse[whId] += inv.quantityOnHand || 0;
    });

    let totalUsed = 0;
    let totalCap = 0;
    const mockPercents = [72, 41, 95, 63]; 
    
    this.warehouses = this.warehouses.map((w, i) => {
      totalCap += w.capacity || 0;
      let used = usedByWarehouse[w.id] || 0;
      let usedPercent = w.capacity > 0 ? Math.min(100, Math.round((used / w.capacity) * 100)) : 0;
      
      totalUsed += used;
      return { ...w, used, usedPercent };
    });

    this.warehouseCapacityPercent = totalCap > 0 ? Math.min(100, Math.round((totalUsed / totalCap) * 100)) : 0;
    this.cdr.markForCheck();
  }

  

  
  openWarehouseModal(event: Event): void {
    event.preventDefault();
    this.showWarehouseModal = true;
  }

  closeWarehouseModal(): void {
    this.showWarehouseModal = false;
  }
  
  openShipmentModal(event: Event): void {
    event.preventDefault();
    this.showShipmentModal = true;
  }

  closeShipmentModal(): void {
    this.showShipmentModal = false;
  }
  
  openDispatchModal(event: Event): void {
    event.preventDefault();
    this.showDispatchModal = true;
  }

  closeDispatchModal(): void {
    this.showDispatchModal = false;
  }
  
  openScheduleModal(event: Event): void {
    event.preventDefault();
    this.showScheduleModal = true;
  }

  closeScheduleModal(): void {
    this.showScheduleModal = false;
  }
  
  openMapModal(event: Event): void {
    event.preventDefault();
    this.showMapModal = true;
  }

  closeMapModal(): void {
    this.showMapModal = false;
  }
  
  generateDummyTimeline() {
      this.liveTimeline = [
          { order: '#1', status: 'Processing', time: 'ETA: 4h', time2: '10:30 AM', color: 'success' },
          { order: '#2', status: 'Processing', time: 'ETA: 4h', time2: '10:30 AM', color: 'success' }
      ];
  }

  toggleSettings(): void {
    this.showSettings = !this.showSettings;
  }

  closeSettings(): void {
    this.showSettings = false;
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

