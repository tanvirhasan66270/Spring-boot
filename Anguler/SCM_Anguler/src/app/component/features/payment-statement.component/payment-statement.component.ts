import { ChangeDetectorRef, Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentStatementService } from '../../../service/payment-statement.service';
import { CustomerOrderService } from '../../../service/customer-order.service';
import { PaymentStatementResponse, PaymentStatementRequest } from '../../shared/model/PaymentStatementModel';
import { CustomerOrderResponseModel } from '../../shared/model/customerOrder';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-payment-statement',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-statement.component.html',
  styleUrl: './payment-statement.component.css'
})
export class PaymentStatementComponent implements OnInit {
  orders: CustomerOrderResponseModel[] = [];
  selectedOrderId: number | null = null;
  payments: PaymentStatementResponse[] = [];
  
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;
  errorMessage: string | null = null;
  userRole: string = '';

  isStatusModalOpen = false;
  selectedPaymentForStatus: PaymentStatementResponse | null = null;
  newIssueStatus: string = 'PENDING_VERIFICATION';
  availableStatuses: string[] = ['PENDING_VERIFICATION', 'CONFIRMED_BY_OFFICER', 'FAILED_OR_REJECTED'];

  @ViewChild('fileInput') fileInput!: ElementRef;
  selectedFile: File | null = null;

  payment: PaymentStatementRequest = {
    customerOrderId: 0,
    paidAmount: 0,
    paymentMethod: 'CASH'
  };

  constructor(
    private service: PaymentStatementService,
    private orderService: CustomerOrderService,
    private storage: StorageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (user) {
      this.userRole = user.role;
    }
    this.loadOrders();
  }

  loadOrders() {
    this.orderService.findAll().subscribe({
      next: (data) => {
        this.orders = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  onOrderSelect() {
    if (this.selectedOrderId) {
      this.loadPaymentsByOrder(this.selectedOrderId);
    } else {
      this.payments = [];
    }
  }

  loadPaymentsByOrder(orderId: number) {
    this.service.getPaymentsByOrderId(orderId).subscribe({
      next: (data) => {
        this.payments = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  openDrawer() {
    this.reset();
    this.isEdit = false;
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  closeDrawer() {
    this.isDrawerOpen = false;
    this.reset();
    this.cdr.markForCheck();
  }

  onFileChange(event: any) {
    const fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      this.selectedFile = fileList[0];
    }
  }

  save() {
    this.errorMessage = null;

    if (this.payment.customerOrderId === 0) {
      this.errorMessage = "Please select a Customer Order.";
      return;
    }
    if (this.payment.paidAmount <= 0) {
      this.errorMessage = "Paid amount must be greater than zero.";
      return;
    }

    const formData = new FormData();
    // Converting the object into Blob as required by @RequestPart("payment") in Spring
    formData.append('payment', new Blob([JSON.stringify(this.payment)], { type: 'application/json' }));
    
    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    } else {
      // Backend expects 'image' part. We provide an empty blob if optional in edit, but for ADD it might be required.
      // The controller says @RequestPart("image") without required=false for ADD, so it might fail if null.
      // We'll append an empty blob if no file to avoid bad request, though user should really select a file.
      formData.append('image', new Blob([], { type: 'application/octet-stream' }), 'empty.png');
    }

    if (this.isEdit && this.currentEditId) {
      this.service.updatePayment(this.currentEditId, formData).subscribe({
        next: () => {
          alert("Payment updated successfully.");
          this.closeDrawer();
          this.onOrderSelect(); // refresh table
        },
        error: (err) => this.handleError(err)
      });
    } else {
      this.service.addPayment(formData).subscribe({
        next: () => {
          alert("Payment created successfully.");
          this.closeDrawer();
          // If the selected order in the top dropdown matches the one we just added to, refresh it
          if (this.selectedOrderId == this.payment.customerOrderId) {
            this.onOrderSelect();
          }
        },
        error: (err) => this.handleError(err)
      });
    }
  }

  edit(p: PaymentStatementResponse) {
    this.isEdit = true;
    this.currentEditId = p.id;
    this.payment = {
      customerOrderId: p.customerOrderId,
      paidAmount: p.paidAmount,
      paymentMethod: p.paymentMethod
    };
    this.selectedFile = null;
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Are you sure you want to delete this payment statement?")) {
      this.service.deletePayment(id).subscribe({
        next: () => {
          alert("Payment deleted.");
          this.onOrderSelect();
        },
        error: (err) => this.handleError(err)
      });
    }
  }

  openStatusModal(p: PaymentStatementResponse) {
    this.selectedPaymentForStatus = p;
    this.newIssueStatus = p.issueStatus;
    this.isStatusModalOpen = true;
    this.cdr.markForCheck();
  }

  closeStatusModal() {
    this.isStatusModalOpen = false;
    this.selectedPaymentForStatus = null;
    this.cdr.markForCheck();
  }

  updatePaymentStatus() {
    if (this.selectedPaymentForStatus) {
      this.service.updatePaymentStatus(this.selectedPaymentForStatus.id, this.newIssueStatus).subscribe({
        next: () => {
          alert("Payment status updated successfully.");
          this.closeStatusModal();
          this.onOrderSelect();
        },
        error: (err) => this.handleError(err)
      });
    }
  }

  reset() {
    this.payment = {
      customerOrderId: this.selectedOrderId || 0,
      paidAmount: 0,
      paymentMethod: 'CASH'
    };
    this.selectedFile = null;
    this.currentEditId = null;
    this.isEdit = false;
    this.errorMessage = null;
    if (this.fileInput && this.fileInput.nativeElement) {
      this.fileInput.nativeElement.value = "";
    }
  }

  handleError(err: any) {
    this.errorMessage = err.error?.message || err.message || "An error occurred.";
    this.cdr.markForCheck();
  }
}
