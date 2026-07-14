import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MessageRequestModel, MessageResponseModel } from '../../massageModel';
import { MessageService } from '../../service/massage.service';
import { StorageService } from '../../../auth/auth_service/storage.service';
import { ManagerService } from '../../../service/manager.service';
import { LogisticsOfficerService } from '../../../service/logistics-officer.service';
import { ProcurementService } from '../../../service/procourment.service';
import { DriverService } from '../../../service/driver.service';
import { CustomerService } from '../../../service/customer.service';
import { SupplierService } from '../../../service/supplier.service';
import { CommercialOfficerService } from '../../../service/commercial-officer.service';
import { SalesOfficerService } from '../../../service/sales-officer.service';
import { QcInspectorService } from '../../../service/qc-inspactor.service';

@Component({
  selector: 'app-massage',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './massage.component.html',
  styleUrl: './massage.component.css',
})
export class MassageComponent implements OnInit {
  messages: MessageResponseModel[] = [];
  isDrawerOpen = false;
  errorMessage: string | null = null;
  currentUserRole: string = '';
  availableUsers: Array<{ id: string; name: string }> = [];

  formModel: MessageRequestModel = {
    recipientId: '',
    subject: '',
    body: '',
    priority: 'MEDIUM',
  };

  constructor(
    private service: MessageService,
    private storage: StorageService,
    private managerService: ManagerService,
    private logisticsService: LogisticsOfficerService,
    private procurementService: ProcurementService,
    private driverService: DriverService,
    private customerService: CustomerService,
    private supplierService: SupplierService,
    private commercialService: CommercialOfficerService,
    private salesService: SalesOfficerService,
    private qcInspectorService: QcInspectorService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    const user = this.storage.getUser();
    if (user) {
      this.currentUserRole = user.role;
    }
    this.loadInbox();
    this.loadAvailableUsers();
  }

  canSelectUser(): boolean {
    const restrictedRoles = ['DRIVER', 'CUSTOMER', 'SUPPLIER'];
    return !restrictedRoles.includes(this.currentUserRole.toUpperCase());
  }

  loadAvailableUsers() {
    if (!this.canSelectUser()) return;

    this.managerService.findAll().subscribe({
      next: (data) => {
        const users = (data || []).map((u: any) => ({
          id: (u.userId || u.id).toString(),
          name: u.name || u.managerName,
        }));
        this.availableUsers = [...this.availableUsers, ...users];
        this.cdr.markForCheck();
      },
    });
    this.logisticsService.findAll().subscribe({
      next: (data) => {
        const users = (data || []).map((u: any) => ({
          id: (u.userId || u.id).toString(),
          name: u.name || u.officerName,
        }));
        this.availableUsers = [...this.availableUsers, ...users];
        this.cdr.markForCheck();
      },
    });
    this.procurementService.findAll().subscribe({
      next: (data) => {
        const users = (data || []).map((u: any) => ({
          id: (u.userId || u.id).toString(),
          name: u.name || u.procurementName,
        }));
        this.availableUsers = [...this.availableUsers, ...users];
        this.cdr.markForCheck();
      },
    });
    this.commercialService.findAll().subscribe({
      next: (data) => {
        const users = (data || []).map((u: any) => ({
          id: (u.userId || u.id).toString(),
          name: u.name || u.officerName,
        }));
        this.availableUsers = [...this.availableUsers, ...users];
        this.cdr.markForCheck();
      },
    });
    this.salesService.findAll().subscribe({
      next: (data) => {
        const users = (data || []).map((u: any) => ({
          id: (u.userId || u.id).toString(),
          name: u.name || u.officerName,
        }));
        this.availableUsers = [...this.availableUsers, ...users];
        this.cdr.markForCheck();
      },
    });
  }

  loadInbox() {
    this.errorMessage = null;
    this.service.getInbox().subscribe({
      next: (data) => {
        this.messages = data || [];
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message ||
          err.error?.detail ||
          err.message ||
          'Unable to load inbox messages.';
        this.cdr.markForCheck();
      },
    });
  }

  openMessage(m: MessageResponseModel) {
    if (m.status === 'UNREAD') {
      this.service.markAsRead(m.id).subscribe({
        next: () => {
          m.status = 'READ';
          this.cdr.markForCheck();
        },
      });
    }
    alert(`From: ${m.senderName}\nSubject: ${m.subject}\n\n${m.body}`);
  }

  submitMessage() {
    this.errorMessage = null;

    if (!this.canSelectUser()) {
      this.formModel.recipientId = null;
    }

    this.service.send(this.formModel).subscribe({
      next: () => {
        alert('SCM Matrix Message routed successfully.');
        this.closeDrawer();
        this.loadInbox();
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || err.error?.detail || err.message || 'Unable to send message.';
        this.cdr.markForCheck();
      },
    });
  }

  openDrawer() {
    this.isDrawerOpen = true;
  }
  closeDrawer() {
    this.isDrawerOpen = false;
    this.formModel = { recipientId: '', subject: '', body: '', priority: 'MEDIUM' };
  }
}
