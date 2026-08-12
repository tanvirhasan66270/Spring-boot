import { ChangeDetectorRef, Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { CustomerOrderService } from '../../../service/customer-order.service';
import { CustomerOrderResponseModel, CustomerOrderRequestModel, OrderLineItemRequestModel } from '../../shared/model/customerOrder';
import { LoginResponse } from '../../../auth/Model/authModel';
import { CustomerService } from '../../../service/customer.service';
import { CustomerResponseModel } from '../../shared/model/customerModel';
import { AddProductService } from '../../../service/add-product.service';
import { ProductResponseModel } from '../../shared/model/addProduct';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';
import { NotificationService } from '../../../system/service/notification.service';
import { NotificationModel } from '../../../system/NotificationModel';
import { InvoiceService } from '../../../service/invoice.service';
import { InvoiceResponseModel } from '../../shared/model/invoiceModel';
import { PaymentStatementService } from '../../../service/payment-statement.service';
import { PaymentStatementResponse } from '../../shared/model/PaymentStatementModel';
import { environment } from '../../../../environment/environment';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent, FormsModule],
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.css'],
})
export class CustomerDashboardComponent implements OnInit, OnDestroy {
  user: LoginResponse | null = null;
  userId!: number;
  customer: CustomerResponseModel | null = null;
  customerOrders: CustomerOrderResponseModel[] = [];
  userName = '';

  stats = { total: 0, active: 0, completed: 0, pending: 0, cancelled: 0, duePayments: 0 };
  recentOrders: CustomerOrderResponseModel[] = [];
  walletBalance = 0;
  dueAmountTotal = 0;
  monthlyExpenses: number[] = [];
  monthLabels: string[] = [];
  recommendations: any[] = [];
  readonly imageBaseUrl = environment.imgUrl + "product/";
  readonly imgUrl = environment.imgUrl;

  notifications: NotificationModel[] = [];

  showSettings = false;
  loading = true;

  // Track Product Modal States
  isTrackModalOpen = false;
  searchTrackingCode = '';
  trackedResult: CustomerOrderResponseModel | null = null;
  trackSearched = false;

  // Invoice History & Statement States
  isStatementCardOpen: boolean = false;
  searchStatementOrderId: string = ''; 
  statementData: any = null;
  statementPayments: PaymentStatementResponse[] = [];
  statementError: string | null = null;

  // Add Payment Modal States
  isPaymentModalOpen = false;
  paymentSearchOrderNumber = '';
  paymentOrderDetails: CustomerOrderResponseModel | null = null;
  paymentAmount = 0;
  paymentMethod = 'CASH';
  paymentFile: File | null = null;
  paymentErrorMessage: string | null = null;

  // Place New Order Inline Form States
  showOrderFormOnly = false;
  products: any[] = [];
  currentProduct: any = null;
  currentQuantity: number = 1;
  currentRemarks: string = '';
  orderImageFile: File | null = null;
  orderImagePreview: string | null = null;

  order: CustomerOrderRequestModel = {
    customerId: 0,
    deliveryAddress: '',
    deliveryPhone: '',      
    estimatedDelivery: '',
    serviceType: 'STANDARD',
    priority: 'NORMAL',      
    currency: 'BDT',            
    codAmount: 0,
    paymentMethod: 'CASH',    
    status: 'PENDING',
    remarks: '',                
    items: []
  };

  deliveredPercent = 0;
  processingPercent = 0;
  cancelledPercent = 0;

  donutDelivered = '0 282';
  donutProcessing = '0 282';
  donutCancelled = '0 282';
  donutOffsetProcessing = 0;
  donutOffsetCancelled = 0;

  chartPath = '';
  chartDots: { x: number; y: number }[] = [];
  chartMaxY = 1;

  statusHierarchy: string[] = [
    'PENDING', 
    'CONFIRMED', 
    'PROCESSING', 
    'SHIPPED', 
    'OUT_FOR_DELIVERY', 
    'DELIVERED'
  ];

  constructor(
    private storage: StorageService,
    private orderService: CustomerOrderService,
    private cutomerService: CustomerService,
    private productService: AddProductService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private route: ActivatedRoute,
    private invoiceService: InvoiceService,
    private paymentService: PaymentStatementService
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (!user) {
      return;
    }
    this.userName = user.name;
    this.userId = user.userId;
    this.loadCustomer();
    this.loadDashboardData();
    this.loadRecommendations();
    this.loadNotifications();

    this.route.queryParams.subscribe(params => {
      if (params['track'] === 'true') {
        setTimeout(() => {
          const btn = document.getElementById('track-product-btn');
          if (btn) {
            btn.click();
          } else {
            this.openTrackModal();
          }
        }, 150);
      }
      if (params['billing'] === 'true') {
        setTimeout(() => {
          const btn = document.getElementById('billing-ledger-btn');
          if (btn) {
            btn.click();
          } else {
            this.openBillingModal();
          }
        }, 150);
      }
    });
  }

  // Track Product Modal Methods
  openTrackModal(): void {
    this.isTrackModalOpen = true;
    this.searchTrackingCode = '';
    this.trackedResult = null;
    this.trackSearched = false;
    this.cdr.markForCheck();
  }

  closeTrackModal(): void {
    this.isTrackModalOpen = false;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { track: null },
      queryParamsHandling: 'merge'
    });
    this.cdr.markForCheck();
  }

  isBillingModalOpen = false;
  searchBillingCode = '';
  billingSearchResult: any = null;
  billingSearched = false;

  // Billing Ledger Modal Methods
  openBillingModal(): void {
    this.isBillingModalOpen = true;
    this.searchBillingCode = '';
    this.billingSearchResult = null;
    this.billingSearched = false;
    this.cdr.markForCheck();
  }

  closeBillingModal(): void {
    this.isBillingModalOpen = false;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { billing: null },
      queryParamsHandling: 'merge'
    });
    this.cdr.markForCheck();
  }

  toggleStatementCard(): void {
    this.isStatementCardOpen = !this.isStatementCardOpen;
    this.statementData = null;
    this.statementPayments = [];
    this.searchStatementOrderId = '';
    this.statementError = null;
    this.cdr.markForCheck();
  }

  // নতুন স্টেটমেন্ট রেকর্ড সার্চ মেথড যা এইচটিএমএল ফাইলের সাথে যুক্ত করা হয়েছে
  searchStatementRecord(): void {
    if (!this.searchStatementOrderId || this.searchStatementOrderId.trim() === '') {
      this.statementError = 'Please enter a valid Customer Order Number.';
      return;
    }
    
    this.statementError = null;
    const orderNumber = this.searchStatementOrderId.trim();

    this.paymentService.getPaymentsByOrderNumber(orderNumber).subscribe({
      next: (res) => {
        this.statementPayments = res || [];
        if (this.statementPayments.length === 0) {
          this.statementError = 'No payment statements found for Order Number #' + orderNumber;
        }
        this.cdr.markForCheck();
      },
      error: () => {
        this.statementPayments = [];
        this.statementError = 'Error fetching payment statements for Order Number #' + orderNumber;
        this.cdr.markForCheck();
      }
    });
  }

  searchBilling(): void {
    this.billingSearched = true;
    if (!this.searchBillingCode || this.searchBillingCode.trim() === '') {
      this.billingSearchResult = null;
      return;
    }

    this.invoiceService.findAll().subscribe({
      next: (invoices) => {
        this.orderService.findAll().subscribe({
          next: (orders) => {
            const order = (orders || []).find(
              o => o.orderNumber?.toLowerCase() === this.searchBillingCode.trim().toLowerCase()
            );
            const foundInvoice = (invoices || []).find(inv => 
              inv.invoiceNumber?.toLowerCase() === this.searchBillingCode.trim().toLowerCase() ||
              (order && inv.customerOrderId === order.id)
            );
            this.billingSearchResult = foundInvoice || null;
            this.cdr.markForCheck();
          },
          error: () => {
            const foundInvoice = (invoices || []).find(inv => 
              inv.invoiceNumber?.toLowerCase() === this.searchBillingCode.trim().toLowerCase()
            );
            this.billingSearchResult = foundInvoice || null;
            this.cdr.markForCheck();
          }
        });
      },
      error: () => {
        this.billingSearchResult = null;
        this.cdr.markForCheck();
      }
    });
  }

  trackOrder(): void {
    this.trackSearched = true;
    if (!this.searchTrackingCode || this.searchTrackingCode.trim() === '') {
      this.trackedResult = null;
      return;
    }

    this.orderService.findAll().subscribe({
      next: (orders) => {
        const found = (orders || []).find(
          (o) => o.orderNumber?.toLowerCase() === this.searchTrackingCode.trim().toLowerCase()
        );
        this.trackedResult = found || null;
        this.cdr.markForCheck();
      },
      error: () => {
        this.trackedResult = null;
        this.cdr.markForCheck();
      },
    });
  }

  openOrderFormView(): void {
    this.resetOrderForm();
    if (this.customer && this.customer.id) {
      this.order.customerId = this.customer.id;
    } else {
      this.loadCustomerForOrder();
    }
    this.loadProductsForForm();
    this.showOrderFormOnly = true;
    this.cdr.markForCheck();
  }

  closeOrderFormView(): void {
    this.showOrderFormOnly = false;
    this.resetOrderForm();
    this.cdr.markForCheck();
  }

  resetOrderForm(): void {
    this.order = {
      customerId: this.customer?.id || 0,
      deliveryAddress: '',
      deliveryPhone: '',
      estimatedDelivery: '',
      serviceType: 'STANDARD',
      priority: 'NORMAL',
      currency: 'BDT',
      codAmount: 0,
      paymentMethod: 'CASH',
      status: 'PENDING',
      remarks: '',
      items: []
    };
    this.currentProduct = null;
    this.currentQuantity = 1;
    this.currentRemarks = '';
    this.orderImageFile = null;
    this.orderImagePreview = null;
  }

  loadCustomerForOrder(): void {
    this.cutomerService.getCustomerByUserId(this.userId).subscribe({
      next: (cust) => {
        if (cust && cust.id) {
          this.customer = cust;
          this.order.customerId = cust.id;
          this.cdr.markForCheck();
        }
      }
    });
  }

  loadProductsForForm(): void {
    this.productService.findAll().subscribe({
      next: (data) => {
        this.products = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  addItemToForm(): void {
    if (!this.currentProduct) {
      alert("Please select a product!");
      return;
    }
    if (this.currentQuantity <= 0) {
      alert("Quantity must be positive!");
      return;
    }

    let selectedProd = this.currentProduct;
    if (typeof this.currentProduct === 'string') {
      selectedProd = this.products.find(p => `${p.name} (৳${p.sellingPrice})` === this.currentProduct);
    }

    if (!selectedProd || !selectedProd.id) {
      alert("Please select a valid product from the list!");
      return;
    }

    const existingItem = this.order.items.find(x => x.productId == selectedProd.id);
    if (existingItem) {
      existingItem.quantity += +this.currentQuantity;
    } else {
      const newItem: OrderLineItemRequestModel = {
        productId: selectedProd.id,
        quantity: +this.currentQuantity,
        remarks: this.currentRemarks
      };
      this.order.items.push(newItem);
    }

    this.currentProduct = null;
    this.currentQuantity = 1;
    this.currentRemarks = '';
    this.cdr.markForCheck();
  }

  removeItemFromForm(index: number): void {
    this.order.items.splice(index, 1);
    this.cdr.markForCheck();
  }

  getProductName(productId: number): string {
    return this.products.find(p => p.id == productId)?.name || 'Unknown Item';
  }

  calculateItemSubtotal(): number {
    let subtotal = 0;
    for (let item of this.order.items) {
      const p = this.products.find(prod => prod.id == item.productId);
      if (p && p.sellingPrice) subtotal += p.sellingPrice * item.quantity;
    }
    return subtotal;
  }

  calculateTotalWeight(): number {
    let weight = 0;
    for (let item of this.order.items) {
      const p = this.products.find(prod => prod.id == item.productId);
      if (p && p.weight) weight += p.weight * item.quantity;
    }
    return weight;
  }

  calculateDeliveryCharge(): number {
    const w = this.calculateTotalWeight();
    let charge = 60 + (w * 15);
    if (this.order.serviceType === 'EXPRESS') charge *= 1.5;
    if (this.order.serviceType === 'OVERNIGHT') charge *= 2.0;
    if (this.order.serviceType === 'SAME_DAY') charge *= 2.5;
    return Math.round(charge);
  }

  calculateTotalAmount(): number {
    return this.calculateItemSubtotal() + this.calculateDeliveryCharge();
  }

  calculateDueAmount(): number {
    const total = this.calculateTotalAmount();
    const paid = Number(this.order.codAmount) || 0;
    const due = total - paid;
    return due < 0 ? 0 : due;
  }

  onOrderImageChange(event: any): void {
    const fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      this.orderImageFile = fileList[0];
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.orderImagePreview = e.target.result;
      };
      reader.readAsDataURL(this.orderImageFile);
    } else {
      this.orderImagePreview = null;
      this.orderImageFile = null;
    }
  }

  saveFormOrder(): void {
    if (!this.order.deliveryPhone || this.order.deliveryPhone.trim() === '') {
      alert("Delivery phone number is required!");
      return;
    }
    if (this.order.items.length === 0) {
      alert("Please add at least one product!");
      return;
    }

    this.orderService.save(this.order, this.orderImageFile || undefined).subscribe({
      next: () => {
        alert("🚀 New customer purchase order dispatched and authorized!");
        this.closeOrderFormView();
        this.loadDashboardData();
      },
      error: (err: any) => {
        alert(err.error?.message || "Failed to dispatch order.");
      }
    });
  }

  isStepCompleted(step: string): boolean {
    if (!this.trackedResult || !this.trackedResult.status) return false;
    
    if (this.trackedResult.status === 'CANCELLED' || this.trackedResult.status === 'RETURNED') {
      return step === 'PENDING';
    }

    const currentIndex = this.statusHierarchy.indexOf(this.trackedResult.status.toUpperCase());
    const stepIndex = this.statusHierarchy.indexOf(step.toUpperCase());

    return currentIndex !== -1 && stepIndex !== -1 && stepIndex <= currentIndex;
  }

  loadCustomer(): void {
    this.cutomerService.getCustomerByUserId(this.userId).subscribe({
      next: (res) => {
        this.customer = res;
        this.storage.saveData(KEYS.CUSTOMER, res);
        this.cdr.markForCheck();
      },
      error: (err: any) => console.log(err),
    });
  }

  ngOnDestroy(): void {}

  loadDashboardData(): void {
    this.loading = true;
    this.orderService.findAll().subscribe({
      next: (orders) => {
        this.customerOrders = orders ? orders.filter((o) => o.customerId === this.userId) : [];

        this.stats.total = this.customerOrders.length;
        this.stats.pending = this.customerOrders.filter((o) => o.status === 'PENDING').length;
        this.stats.active = this.customerOrders.filter((o) =>
          ['CONFIRMED', 'PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY'].includes(o.status),
        ).length;
        this.stats.completed = this.customerOrders.filter((o) => o.status === 'DELIVERED').length;
        this.stats.cancelled = this.customerOrders.filter((o) => o.status === 'CANCELLED').length;
        this.stats.duePayments = this.customerOrders.filter(
          (o) => o.paymentStatus !== 'PAID',
        ).length;

        this.walletBalance = this.customerOrders
          .filter((o) => o.paymentStatus === 'PAID')
          .reduce((sum, o) => sum + (Number(o.totalAmount) || 0), 0);

        this.dueAmountTotal = this.customerOrders
          .filter((o) => o.paymentStatus !== 'PAID')
          .reduce((sum, o) => sum + (parseFloat(o.dueAmount as any) || 0), 0);

        const totalOrders = this.customerOrders.length;
        const deliveredCount = this.customerOrders.filter((o) => o.status === 'DELIVERED').length;
        const processingCount = this.customerOrders.filter((o) =>
          ['CONFIRMED', 'PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY', 'PENDING'].includes(o.status),
        ).length;
        const cancelledCount = this.customerOrders.filter((o) => o.status === 'CANCELLED').length;

        if (totalOrders > 0) {
          this.deliveredPercent = Math.round((deliveredCount / totalOrders) * 100);
          this.processingPercent = Math.round((processingCount / totalOrders) * 100);
          this.cancelledPercent = Math.round((cancelledCount / totalOrders) * 100);
        }

        this.recentOrders = [...this.customerOrders]
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
          .slice(0, 5);

        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Customer metrics fetching failed:', err);
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  loadRecommendations(): void {
    this.productService.findAll().subscribe({ next: (data) => {
        this.recommendations = (data || []).slice(0, 5).map((p: ProductResponseModel) => ({
          name: p.name || 'Product',
          price: p.sellingPrice || 0,
          rating:
            Math.round(((p.sellingPrice || 0) / Math.max(p.unitCost || 1, 1)) * 10) / 10 > 5
              ? 5
              : Math.round(((p.sellingPrice || 0) / Math.max(p.unitCost || 1, 1)) * 10) / 10 || 4.0,
          image: p.image || null }));
        this.cdr.markForCheck();
      },
    });
  }

  getImageUrl(imageName: string | null | undefined): string {
    return imageName ? `${this.imageBaseUrl}${imageName}` : '';
  }

  onImageError(prod: any): void {
    prod.image = null;
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

  getActionIcon(action: string): string {
    const icons: Record<string, string> = {
      CREATE: 'bi-plus-circle text-success',
      UPDATE: 'bi-pencil-square text-primary',
      DELETE: 'bi-trash text-danger',
      LOGIN: 'bi-box-arrow-in-right text-info',
    };
    return icons[action] || 'bi-clock text-secondary';
  }

  isChildRouteActive(): boolean {
    return this.router.url.includes('customer_profile');
  }

  onEditProfileTriggered(): void {
    this.showSettings = false;
    this.router.navigate(['customer_profile'], { relativeTo: this.route });
  }

  downloadStatementPdf(): void {
    window.print(); 
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['']);
  }

  // Add Payment Logic
  paymentImagePreview: string | null = null;
  paymentCustomerAccountNumber: string = '';
  
  openPaymentModal() {
    this.isPaymentModalOpen = true;
    this.resetPaymentState();
    this.cdr.markForCheck();
  }

  closePaymentModal() {
    this.isPaymentModalOpen = false;
    this.resetPaymentState();
    this.cdr.markForCheck();
  }

  resetPaymentState() {
    this.paymentSearchOrderNumber = '';
    this.paymentOrderDetails = null;
    this.paymentAmount = 0;
    this.paymentMethod = 'CASH';
    this.paymentFile = null;
    this.paymentImagePreview = null;
    this.paymentCustomerAccountNumber = '';
    this.paymentErrorMessage = null;
  }

  searchOrderForPayment() {
    this.paymentErrorMessage = null;
    if (!this.paymentSearchOrderNumber) return;

    this.orderService.trackOrder(this.paymentSearchOrderNumber).subscribe({
      next: (order) => {
        // Only allow if order belongs to the customer
        if (order.customerId !== this.userId) {
          this.paymentOrderDetails = null;
          this.paymentErrorMessage = "Order not found or invalid order number.";
          this.cdr.markForCheck();
          return;
        }

        this.paymentOrderDetails = order;
        if (order.dueAmount) {
          this.paymentAmount = Number(order.dueAmount);
        }
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.paymentOrderDetails = null;
        this.paymentErrorMessage = "Order not found or invalid order number.";
        this.cdr.markForCheck();
      }
    });
  }

  onPaymentFileChange(event: any) {
    const fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      this.paymentFile = fileList[0];
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.paymentImagePreview = e.target.result;
      };
      reader.readAsDataURL(this.paymentFile);
    } else {
      this.paymentImagePreview = null;
      this.paymentFile = null;
    }
  }

  submitPayment() {
    this.paymentErrorMessage = null;
    if (!this.paymentOrderDetails) return;
    if (this.paymentAmount <= 0) {
      this.paymentErrorMessage = "Amount must be greater than zero.";
      return;
    }

    const payload = {
      customerOrderId: this.paymentOrderDetails.id,
      paidAmount: this.paymentAmount,
      paymentMethod: this.paymentMethod,
      customerAccountNumber: this.paymentCustomerAccountNumber
    };

    const formData = new FormData();
    formData.append('payment', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
    
    if (this.paymentFile) {
      formData.append('image', this.paymentFile);
    } else {
      formData.append('image', new Blob([], { type: 'application/octet-stream' }), 'empty.png');
    }

    this.paymentService.addPayment(formData).subscribe({
      next: () => {
        alert("Payment submitted successfully. It will be verified by an officer soon.");
        this.closePaymentModal();
        this.loadDashboardData();
      },
      error: (err) => {
        this.paymentErrorMessage = err.error?.message || err.message || "Failed to add payment.";
        this.cdr.markForCheck();
      }
    });
  }
}