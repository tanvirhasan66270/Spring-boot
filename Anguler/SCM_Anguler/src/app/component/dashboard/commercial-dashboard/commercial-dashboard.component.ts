import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { CommercialOfficerService } from '../../../service/commercial-officer.service';
import { CommercialOfficerResponseModel } from '../../shared/model/commercialOfficer';
import { LoginResponse } from '../../../auth/Model/authModel';
import { LetterOfCreditService } from '../../../service/letterofcradit.service';
import { LetterOfCreditResponseModel } from '../../shared/model/letterOfCraditModel';
import { ShipmentService } from '../../../service/shipment.service';
import { ShipmentResponseModel } from '../../shared/model/shipmentModel';
import { NotificationService } from '../../../system/service/notification.service';
import { NotificationModel } from '../../../system/NotificationModel';
import { ActivityLogService } from '../../../service/activity.log.service';
import { ActivityLogModel } from '../../shared/model/ActivityLogModel';
import { InvoiceService } from '../../../service/invoice.service';
import { InvoiceResponseModel } from '../../shared/model/invoiceModel';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';

@Component({
  selector: 'app-commercial-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent],
  templateUrl: './commercial-dashboard.component.html',
  styleUrls: ['./commercial-dashboard.component.css'],
})
export class CommercialDashboardComponent implements OnInit {
  userName = '';
  showSettings = false;
  loading = true;
  today = new Date();

  kpis = [
    { label: 'Active Import LCs', value: '0 LCs', trend: 0, icon: 'bi-bank', color: 'primary' },
    { label: 'LC Outstanding Dues', value: '৳0', trend: 0, icon: 'bi-wallet2', color: 'warning' },
    {
      label: 'Customs Pending',
      value: '0 Shipments',
      trend: 0,
      icon: 'bi-file-earmark-lock',
      color: 'danger',
    },
    {
      label: 'Docs Approved Today',
      value: '0 Sets',
      trend: 0,
      icon: 'bi-file-earmark-check',
      color: 'success',
    },
  ];

  lcs: LetterOfCreditResponseModel[] = [];
  documents: { name: string; type: string; date: string; status: string; url: string }[] = [];
  notifications: NotificationModel[] = [];
  activities: ActivityLogModel[] = [];
  invoices: InvoiceResponseModel[] = [];

  totalLCValue = 0;
  pendingCustoms = 0;
  approvedDocs = 0;

  userId!: number;
  commercialOfficer: CommercialOfficerResponseModel | null = null;
  user: LoginResponse | null = null;

  constructor(
    private storage: StorageService,
    private commercialOfficerService: CommercialOfficerService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private lcService: LetterOfCreditService,
    private shipmentService: ShipmentService,
    private notificationService: NotificationService,
    private activityLogService: ActivityLogService,
    private invoiceService: InvoiceService,
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name;
    this.userId = user.userId;
    this.loadCommercialOfficer();
    this.loadDashboardData();
    this.loadNotifications();
    this.loadActivityLogs();
    this.loadInvoices();
  }

  loadDashboardData() {
    this.documents = [];
    this.lcService.findAll().subscribe({
      next: (data) => {
        const all = data || [];
        const activeLCs = all.filter((lc) => lc.lcStatus !== 'CANCELLED');
        this.totalLCValue = activeLCs.reduce((sum: number, lc) => sum + (lc.amount || 0), 0);
        this.kpis[0] = { ...this.kpis[0], value: `${activeLCs.length} LCs` };
        this.kpis[1] = { ...this.kpis[1], value: `৳${this.totalLCValue.toLocaleString()}` };
        this.lcs = activeLCs.slice(0, 5);
        this.buildLCDocuments(all);
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });

    this.shipmentService.findAll().subscribe({
      next: (data) => {
        const all = (data || []) as any[];
        const pending = all.filter((s) => s.status === 'PENDING' || s.status === 'CUSTOMS');
        this.pendingCustoms = pending.length;
        this.kpis[2] = { ...this.kpis[2], value: `${pending.length} Shipments` };
        this.buildShipmentDocuments(all);
        this.cdr.markForCheck();
      },
    });
  }

  buildLCDocuments(lcs: LetterOfCreditResponseModel[]) {
    const lcdocs = lcs
      .filter((lc) => lc.documentVaultUrl)
      .map((lc) => ({
        name: `LC Document - ${lc.lcNumber}`,
        type: 'Letter of Credit',
        date: lc.createdAt ? new Date(lc.createdAt).toLocaleDateString() : 'N/A',
        status: lc.lcStatus === 'OPENED' ? 'Approved' : 'Pending Review',
        url: lc.documentVaultUrl,
      }));
    this.documents = [...this.documents, ...lcdocs];
  }

  buildShipmentDocuments(shipments: ShipmentResponseModel[]) {
    const shipDocs = shipments
      .filter((s) => s.podFileUrl)
      .map((s) => ({
        name: `POD - ${s.shipmentNumber}`,
        type: 'Shipment',
        date: s.createdAt ? new Date(s.createdAt).toLocaleDateString() : 'N/A',
        status: 'Approved',
        url: s.podFileUrl,
      }));
    this.documents = [...this.documents, ...shipDocs];
    this.approvedDocs = this.documents.filter((d) => d.status === 'Approved').length;
    this.kpis[3] = { ...this.kpis[3], value: `${this.approvedDocs} Sets` };
  }

  loadInvoices() {
    this.invoiceService.findAll().subscribe({
      next: (data) => {
        this.invoices = (data || []).slice(0, 5);
        this.cdr.markForCheck();
      },
    });
  }

  loadNotifications() {
    this.notificationService.findAll().subscribe({
      next: (data) => {
        this.notifications = (data || []).slice(0, 8);
        this.cdr.markForCheck();
      },
    });
  }

  loadActivityLogs() {
    this.activityLogService.findAll().subscribe({
      next: (data) => {
        this.activities = (data || []).slice(0, 8);
        this.cdr.markForCheck();
      },
    });
  }

  loadCommercialOfficer(): void {
    this.commercialOfficerService.getCommercialOfficerByUserId(this.userId).subscribe({
      next: (res) => {
        this.commercialOfficer = res;
        this.storage.saveData(KEYS.COMMERCIAL_OFFICER, res);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  toggleSettings(): void {
    this.showSettings = !this.showSettings;
    this.cdr.markForCheck();
  }

  closeSettings(): void {
    this.showSettings = false;
    this.cdr.markForCheck();
  }

  getLCStatusClass(status: string): string {
    switch (status) {
      case 'DRAFT':
        return 'bg-secondary-subtle text-secondary';
      case 'OPENED':
        return 'bg-success-subtle text-success';
      case 'AMENDED':
        return 'bg-warning-subtle text-warning';
      case 'EXPIRED':
        return 'bg-danger-subtle text-danger';
      case 'CANCELLED':
        return 'bg-dark-subtle text-dark';
      default:
        return 'bg-secondary-subtle text-secondary';
    }
  }

  getNotifIcon(type: string): string {
    switch (type) {
      case 'SHIPMENT':
        return 'bi-truck text-primary';
      case 'TRIP_ALERT':
        return 'bi-exclamation-triangle text-warning';
      case 'REPORT_APPROVED':
        return 'bi-check-circle text-success';
      default:
        return 'bi-bell text-secondary';
    }
  }

  getActionIcon(action: string): string {
    switch (action) {
      case 'CREATE':
        return 'bi-plus-circle text-success';
      case 'UPDATE':
        return 'bi-pencil-square text-primary';
      case 'DELETE':
        return 'bi-trash text-danger';
      case 'LOGIN':
        return 'bi-box-arrow-in-right text-info';
      default:
        return 'bi-activity text-secondary';
    }
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['']);
    this.cdr.markForCheck();
  }
}
