import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { KEYS, StorageService } from '../../../auth/auth_service/storage.service';
import { SupplierService } from '../../../service/supplier.service';
import { SupplierResponseDTO } from '../../shared/model/supplierModel';
import { LoginResponse } from '../../../auth/Model/authModel';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { PurchaseOrderResponseModel } from '../../shared/model/purchaseOrderModel';
import { QuotationService } from '../../../service/quatation.service';
import { QuotationResponseModel } from '../../shared/model/quatationModel';
import { DashboardSettingsComponent } from '../dashboard-settings/dashboard-settings.component';
import { NotificationService } from '../../../system/service/notification.service';
import { NotificationModel } from '../../../system/NotificationModel';
import { InvoiceService } from '../../../service/invoice.service';
import { InvoiceResponseModel } from '../../shared/model/invoiceModel';
import { PurchaseRequisitionService } from '../../../service/purchase-requisition.service';
import { purchaseRequisitionResponseModel } from '../../shared/model/purchase-requisionModel';
import { ShipmentService } from '../../../service/shipment.service';
import { PoLineItemService } from '../../../service/po-line-item.service';

@Component({
  selector: 'app-supplier-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DashboardSettingsComponent, FormsModule],
  templateUrl: './supplier-dashboard.component.html',
  styleUrls:['./supplier-dashboard.component.css'],
})
export class SupplierDashboardComponent implements OnInit {
  
  // =========================================================================
  // 1. COMPONENT STATE & VARIABLES
  // =========================================================================

  userName = '';
  userId!: number;
  supplier: SupplierResponseDTO | null = null;
  user: LoginResponse | null = null;

  // KPI (Key Performance Indicator) Cards shown at the top of the dashboard
  kpis = [
    { label: 'Purchase Orders', value: '0 POs', trend: 0, icon: 'bi-inboxes', color: 'primary' },
    { label: 'Pending Delivery', value: '0 Consignments', trend: 0, icon: 'bi-truck-flatbed', color: 'warning' },
    { label: 'Outstanding Payments', value: '৳0', trend: 0, icon: 'bi-currency-exchange', color: 'success' },
    { label: 'Supply Accuracy', value: '0%', trend: 0, icon: 'bi-patch-check', color: 'success' },
    { label: 'Active RFQ Bids', value: '0 Bids', trend: 0, icon: 'bi-file-earmark-arrow-up', color: 'info' },
    { label: 'Assigned PRs', value: '0 PRs', trend: 0, icon: 'bi-file-earmark-text', color: 'teal' },
    { label: 'Active LCs', value: '0 Open', trend: 0, icon: 'bi-bank', color: 'warning' },
    { label: 'PO Archive', value: '0 Records', trend: 0, icon: 'bi-journal-check', color: 'secondary' },
  ];

  // Data arrays for main dashboard tables
  pos: any[] = [];          
  receivedPOs: any[] = [];  
  rfqs: any[] = [];
  notifications: NotificationModel[] = [];
  lettersOfCredit: any[] = [];
  allSupplierPOs: any[] = [];
  requisitions: any[] = []; 
  shipments: any[] = [];
  poLineItems: any[] = [];

  // Arrays containing ONLY data from the last 30 days (used for Shortcut Modals)
  recentPOs: any[] = [];
  recentRfqs: any[] = [];
  recentLCs: any[] = [];
  recentShipments: any[] = [];
  recentPoLineItems: any[] = [];

  // UI State Control Variables
  activeTableTab: string = 'ISSUED_POS'; // Tracks which table is active in the main view
  activeShortcutModal: string | null = null; // Tracks which shortcut modal is currently open
  modalSearchText: string = ''; // Bound to the search input inside the modal
  showSettings = false; // Controls the side drawer for settings
  loading = true; // Shows skeleton loader when true
  lcOpen = false;
  poRegistryOpen = false;

  // Calculated Metrics
  outstandingPayments = 0;
  supplyAccuracy = 0;
  pendingDeliveries = 0;

  constructor(
    private storage: StorageService,
    public router: Router, 
    public cdr: ChangeDetectorRef,
    private supplierService: SupplierService,
    private poService: PurchaseOrderService,
    private quotationService: QuotationService,
    private notificationService: NotificationService,
    private invoiceService: InvoiceService,
    private prService: PurchaseRequisitionService,
    private shipmentService: ShipmentService,
    private poLineItemService: PoLineItemService
  ) {}

  // =========================================================================
  // 2. INITIALIZATION & LIFECYCLE HOOKS
  // =========================================================================

  ngOnInit(): void {
    // 1. Get logged-in user details from local storage
    const loggedInUser = this.storage.getUser();
    if (!loggedInUser) {
      return; // Stop execution if no user is found
    }
    
    // 2. Set user details
    this.userName = loggedInUser.name || 'Supplier Node';
    this.userId = loggedInUser.userId;
    
    // 3. Load supplier profile and subsequent data
    this.loadSupplier();
    this.loadNotifications();
  }

  isChildRouteActive(): boolean {
    // Checks if the user is currently viewing the profile page route
    return this.router.url.includes('supplier_profile');
  }

  onEditProfileTriggered(): void {
    this.showSettings = false; 
    // Dynamically navigate to the profile route based on current URL
    this.router.navigate([this.router.url.split('/supplier_profile')[0] + '/supplier_profile']);
    this.cdr.markForCheck();
  }

  loadSupplier(): void {
    // Fetch the supplier's specific ID based on their User ID
    this.supplierService.getSupplierByUserId(this.userId).subscribe({
      next: (response) => {
        if (response) {
          this.supplier = response;
          this.storage.saveData(KEYS.SUPPLIER, response);
          
          // Once we have the supplier ID, load all their dashboard data!
          this.loadDashboardData(response.id);
        }
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Failed to load supplier profile:', err);
        this.loading = false; // Stop loading spinner on error
        this.cdr.markForCheck();
      },
    });
  }

  // =========================================================================
  // 3. UTILITY METHODS
  // =========================================================================

  /**
   * This function filters any array of data to only keep items from the last 30 days.
   * It is useful for showing "Recent" items on the dashboard.
   */
  filterLastMonth(dataArray: any[], dateField: string): any[] {
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30); // Subtract 30 days from today

    // Return a new array containing only the items that were created after 'thirtyDaysAgo'
    return dataArray.filter(item => {
      if (!item[dateField]) return false; // If there is no date, skip this item
      const itemDate = new Date(item[dateField]);
      return itemDate >= thirtyDaysAgo;
    });
  }

  // =========================================================================
  // 4. CORE DATA LOADING METHOD
  // =========================================================================

  /**
   * This is the main function that loads all the numbers and tables for the dashboard.
   * It calls multiple services (APIs) and filters the data so the supplier only sees their own information.
   */
  private loadDashboardData(supplierId: number): void {
    this.loading = true; // Show the loading skeleton animation

    // ---------------------------------------------------------
    // A. Load Purchase Orders (POs)
    // ---------------------------------------------------------
    this.poService.findAll().subscribe({ 
      next: (allOrdersFromDatabase) => {
        const allOrders = allOrdersFromDatabase || [];
        
        // Filter to only keep orders assigned to THIS specific supplier
        const supplierSpecificOrders = allOrders.filter((order: any) => {
          const currentOrderSupplierId = order.supplierId || (order.supplier ? order.supplier.id : null);
          return currentOrderSupplierId === supplierId;
        });

        // Separate orders by their status
        const issuedOrders = supplierSpecificOrders.filter((order: PurchaseOrderResponseModel) => order.status === 'ISSUED');
        const deliveredOrders = supplierSpecificOrders.filter((order: PurchaseOrderResponseModel) => order.status === 'RECEIVED');
        
        // Update KPI metrics
        this.pendingDeliveries = issuedOrders.length;
        if (supplierSpecificOrders.length > 0) {
          this.supplyAccuracy = Math.round((deliveredOrders.length / supplierSpecificOrders.length) * 100);
        }

        // Calculate Monthly Trends (Comparing this month vs last month)
        const now = new Date();
        const currentMonth = now.getMonth();
        const currentYear = now.getFullYear();
        let lastMonth = currentMonth - 1;
        let lastMonthYear = currentYear;
        
        // If current month is January (0), last month is December (11) of the previous year
        if (lastMonth < 0) { 
          lastMonth = 11; 
          lastMonthYear--; 
        }

        let ordersThisMonth = 0; 
        let ordersLastMonth = 0;
        let pendingOrdersThisMonth = 0; 
        let pendingOrdersLastMonth = 0;
        
        supplierSpecificOrders.forEach((order: any) => {
          const orderDate = new Date(order.createdAt || new Date());
          const isThisMonth = orderDate.getMonth() === currentMonth && orderDate.getFullYear() === currentYear;
          const isLastMonth = orderDate.getMonth() === lastMonth && orderDate.getFullYear() === lastMonthYear;

          if (isThisMonth) { 
            ordersThisMonth++; 
            if(order.status === 'ISSUED') pendingOrdersThisMonth++; 
          }
          if (isLastMonth) { 
            ordersLastMonth++; 
            if(order.status === 'ISSUED') pendingOrdersLastMonth++; 
          }
        });

        // Calculate percentage trends for the KPI cards
        const orderTrendPercentage = ordersLastMonth > 0 ? Math.round(((ordersThisMonth - ordersLastMonth) / ordersLastMonth) * 100) : (ordersThisMonth > 0 ? 100 : 0);
        const pendingTrendPercentage = pendingOrdersLastMonth > 0 ? Math.round(((pendingOrdersThisMonth - pendingOrdersLastMonth) / pendingOrdersLastMonth) * 100) : (pendingOrdersThisMonth > 0 ? 100 : 0);
        const accuracyTrendPercentage = this.supplyAccuracy > 80 ? 15 : (this.supplyAccuracy > 50 ? 5 : -10);

        // Update the KPI Cards Array
        this.kpis[0] = { ...this.kpis[0], value: `${supplierSpecificOrders.length} POs`, trend: orderTrendPercentage };
        this.kpis[1] = { ...this.kpis[1], value: `${this.pendingDeliveries} Consignments`, trend: pendingTrendPercentage };
        this.kpis[3] = { ...this.kpis[3], value: `${this.supplyAccuracy}%`, trend: accuracyTrendPercentage };
        this.kpis[7] = { ...this.kpis[7], value: `${deliveredOrders.length} Records`, trend: accuracyTrendPercentage };

        // Save data for the HTML tables
        this.allSupplierPOs = supplierSpecificOrders;
        this.recentPOs = this.filterLastMonth(this.allSupplierPOs, 'createdAt');

        // Prepare a small list (max 5) of pending issued orders for the table view
        this.pos = issuedOrders.slice(0, 5).map((order: PurchaseOrderResponseModel) => ({
          id: order.id,
          poNumber: order.poNumber || `PO-${order.id}`,
          date: order.createdAt || 'N/A',
          amount: order.totalAmount || 0,
          deliveryDue: order.expectedDeliveryDate || 'N/A',
          status: order.status || 'ISSUED',
        }));

        // Prepare a small list (max 5) of received orders for the table view
        this.receivedPOs = deliveredOrders.slice(0, 5).map((order: PurchaseOrderResponseModel) => ({
          id: order.id,
          poNumber: order.poNumber || `PO-${order.id}`,
          date: order.createdAt || 'N/A',
          amount: order.totalAmount || 0,
          deliveryDue: order.expectedDeliveryDate || 'N/A',
          status: order.status || 'RECEIVED',
        }));

        // Create Mock Letters of Credit (LCs) based on delivered orders
        this.lettersOfCredit = deliveredOrders.map((order: any) => ({
          lcNumber: `LC-${100000 + order.id}`,
          poReference: order.poNumber || `PO-${order.id}`,
          expiryDate: order.expectedDeliveryDate ? new Date(new Date(order.expectedDeliveryDate).getTime() + (30 * 24 * 60 * 60 * 1000)) : 'N/A',
          amount: order.totalAmount || 0,
          status: 'OPEN'
        }));
        
        // Ensure the shortcut modal has access to the recent LCs
        this.recentLCs = this.lettersOfCredit;

        // Check if the user has received any POs (used for hiding/showing specific UI elements)
        if (deliveredOrders.length > 0) {
          localStorage.setItem('hasReceivedPO', 'true');
        } else {
          localStorage.removeItem('hasReceivedPO');
        }

        // Update LC KPI
        this.kpis[6] = { ...this.kpis[6], value: `${this.lettersOfCredit.length} Open`, trend: orderTrendPercentage };

        this.cdr.markForCheck(); // Tell Angular to update the HTML view
      },
      error: (err: any) => console.error('Error loading Purchase Orders:', err),
    });

    // ---------------------------------------------------------
    // B. Load Quotations (RFQs)
    // ---------------------------------------------------------
    this.quotationService.findAll().subscribe({
      next: (data) => {
        const allQuotationsFromDatabase = data || [];
        
        // Filter quotations to only keep those for this supplier
        const supplierSpecificQuotations = allQuotationsFromDatabase.filter((quotation: any) => {
          const currentQuotationSupplierId = quotation.supplierId || (quotation.supplier && quotation.supplier.id ? quotation.supplier.id : null);
          return currentQuotationSupplierId === supplierId;
        });
        
        // Map the raw data into a simpler object for the HTML table
        this.rfqs = supplierSpecificQuotations.map((quotation: QuotationResponseModel) => ({
          id: quotation.quotationNumber || `RFQ-${quotation.id}`,
          item: quotation.productName || 'N/A',
          closingDate: quotation.validUntil || 'N/A',
          status: quotation.status || 'PENDING',
        }));
        
        // Get only the quotations from the last 30 days for the shortcut modal
        this.recentRfqs = this.filterLastMonth(supplierSpecificQuotations, 'createdAt');

        // Update the Quotation KPI
        const rfqTrendPercentage = supplierSpecificQuotations.length > 5 ? 20 : (supplierSpecificQuotations.length > 0 ? 5 : 0);
        this.kpis[4] = { ...this.kpis[4], value: `${supplierSpecificQuotations.length} Bids`, trend: rfqTrendPercentage };

        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Error loading Quotations:', err);
      }
    });

    // ---------------------------------------------------------
    // C. Load Purchase Requisitions (PRs)
    // ---------------------------------------------------------
    this.prService.findAll().subscribe({ 
      next: (data) => {
        const allRequisitionsFromDatabase = data || [];
        
        // Keep only APPROVED requisitions that are specifically assigned to this supplier's ID
        const approvedRequisitionsForSupplier = allRequisitionsFromDatabase.filter(
          (requisition: purchaseRequisitionResponseModel) => 
            requisition.approvalStatus === 'APPROVED' && 
            requisition.supplierIds && requisition.supplierIds.includes(supplierId)
        );

        // Take a maximum of 5 requisitions for the table
        this.requisitions = approvedRequisitionsForSupplier.slice(0, 5).map((requisition: purchaseRequisitionResponseModel) => ({
          id: requisition.id,
          productNames: requisition.productNames || [],
          supplierNames: requisition.supplierNames || [], 
          quantity: requisition.quantityRequired || 0,
          urgency: requisition.urgencyLevel || 'LOW',
          deadline: requisition.requiredByDate || 'N/A',
          status: requisition.approvalStatus || 'APPROVED'
        }));
        
        // Update the Requisition KPI
        const prTrendPercentage = approvedRequisitionsForSupplier.length > 5 ? 10 : (approvedRequisitionsForSupplier.length > 0 ? 2 : 0);
        this.kpis[5] = { ...this.kpis[5], value: `${approvedRequisitionsForSupplier.length} PRs`, trend: prTrendPercentage };

        this.cdr.markForCheck(); 
      },
      error: (err: any) => console.error('Error loading Requisitions:', err)
    });

    // ---------------------------------------------------------
    // D. Load Invoices (For Outstanding Payments KPI)
    // ---------------------------------------------------------
    this.invoiceService.findAll().subscribe({
      next: (allInvoicesFromDatabase: any[]) => {
        // Keep only invoices related to this specific supplier
        const supplierInvoices = allInvoicesFromDatabase.filter((invoice: any) => {
          return invoice.supplierId === supplierId;
        });
        
        // Calculate the total amount of money still owed to the supplier
        let totalUnpaidAmount = 0;
        supplierInvoices.forEach(invoice => {
          if (invoice.status === 'UNPAID' || invoice.status === 'PENDING') {
            totalUnpaidAmount += (invoice.amount || 0);
          }
        });
        this.outstandingPayments = totalUnpaidAmount;
        
        // Update the Payment KPI
        const invoiceTrendPercentage = totalUnpaidAmount > 10000 ? 5 : (totalUnpaidAmount > 0 ? 2 : 0);
        this.kpis[2] = { ...this.kpis[2], value: `৳${this.outstandingPayments.toLocaleString()}`, trend: invoiceTrendPercentage };

        this.loading = false; // Turn off loading skeleton once this is done
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Invoice Load Error:', err);
        this.loading = false;
        this.cdr.markForCheck();
      }
    });

    // ---------------------------------------------------------
    // E. Load Shipments (For Shortcut Modal)
    // ---------------------------------------------------------
    this.shipmentService.findAll().subscribe({
      next: (allShipmentsFromDatabase: any[]) => {
        // Keep only shipments for this supplier
        const supplierSpecificShipments = allShipmentsFromDatabase.filter((shipment: any) => shipment.supplierId === supplierId);
        this.shipments = supplierSpecificShipments;
        
        // Get only the shipments from the last 30 days
        this.recentShipments = this.filterLastMonth(supplierSpecificShipments, 'createdAt');
        
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Error loading Shipments:', err);
        this.loading = false;
        this.cdr.markForCheck();
      }
    });

    // ---------------------------------------------------------
    // F. Load PO Line Items (For Shortcut Modal)
    // ---------------------------------------------------------
    this.poLineItemService.findAll().subscribe({
      next: (allLineItemsFromDatabase: any[]) => {
        // Keep only line items for this supplier
        const supplierSpecificLineItems = allLineItemsFromDatabase.filter((lineItem: any) => lineItem.supplierId === supplierId);
        this.poLineItems = supplierSpecificLineItems;
        
        // Get only the line items from the last 30 days
        this.recentPoLineItems = this.filterLastMonth(supplierSpecificLineItems, 'createdAt');
        
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Error loading PO Line Items:', err);
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  // =========================================================================
  // 5. EVENT HANDLERS & HELPERS
  // =========================================================================

  togglePurchaseOrderLedger(event: Event): void {
    event.preventDefault();
    this.activeTableTab = 'SHORTCUT_PO';
    this.cdr.markForCheck();
  }

  toggleBillingLCSection(event: Event): void {
    event.preventDefault();
    this.recentLCs = this.filterLastMonth(this.lettersOfCredit, 'createdAt');
    this.activeTableTab = 'SHORTCUT_LC';
    this.cdr.markForCheck();
  }

  showShortcut(tab: string, event?: Event): void {
    if (event) {
      event.preventDefault();
    }
    this.activeShortcutModal = tab;
    this.modalSearchText = '';
    this.cdr.markForCheck();
  }

  closeShortcutModal(): void {
    this.activeShortcutModal = null;
    this.modalSearchText = '';
    this.cdr.markForCheck();
  }

  /**
   * Getter method used in the HTML template.
   * It dynamically returns the correct list of data based on which modal is currently open.
   * It also applies a simple text filter if the user typed something in the search bar.
   */
  get filteredShortcutData(): any[] {
    const searchText = this.modalSearchText.toLowerCase();
    let currentModalData: any[] = [];
    
    // Determine which array to use based on the active modal string
    switch (this.activeShortcutModal) {
      case 'SHORTCUT_PO': currentModalData = this.recentPOs; break;
      case 'SHORTCUT_RFQ': currentModalData = this.recentRfqs; break;
      case 'SHORTCUT_LC': currentModalData = this.recentLCs; break;
      case 'SHORTCUT_SHIPMENT': currentModalData = this.recentShipments; break;
      case 'SHORTCUT_POLINE': currentModalData = this.recentPoLineItems; break;
    }
    
    // If the search bar is empty, just return all the data
    if (!searchText) return currentModalData;
    
    // Filter the data by converting the whole object to a string and checking if it contains the search text
    return currentModalData.filter(item => JSON.stringify(item).toLowerCase().includes(searchText));
  }

  updatePoStatus(orderId: number, nextStatus: 'RECEIVED' | 'CANCELLED') {
    this.poService.changeStatus(orderId, nextStatus).subscribe({
      next: () => {
        alert(`Purchase Order successfully marked as ${nextStatus}!`);
        if (this.supplier) {
          this.loadDashboardData(this.supplier.id); // Reload the dashboard data to reflect the change
        }
      },
      error: (err: any) => {
        console.error('Failed to change PO status:', err);
        alert(err.error?.message || 'Transaction failed.');
      },
    });
  }

  togglePurchaseOrderRegistry(event: Event) {
    event.preventDefault();
    this.poRegistryOpen = !this.poRegistryOpen;

    if (this.poRegistryOpen && this.supplier) {
      this.loading = true; 
      this.poService.getOrdersBySupplierId(this.supplier.id).subscribe({
        next: (data) => {
          this.allSupplierPOs = (data || []).map((order: any) => ({
            poNumber: order.poNumber || `PO-${order.id}`,
            date: order.createdAt || 'N/A',
            amount: order.totalAmount || 0,
            deliveryDue: order.expectedDeliveryDate || 'N/A',
            status: order.status || 'DRAFT'
          }));
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: (err: any) => {
          console.error('Failed to load supplier PO history:', err);
          this.loading = false;
          this.cdr.markForCheck();
        }
      });
    }
  }

  loadNotifications(): void {
    this.notificationService.findAll().subscribe({
      next: (data) => {
        this.notifications = (data || []).slice(0, 5); // Take only the 5 most recent notifications
        this.cdr.markForCheck();
      },
      error: (err: any) => console.error('Error loading notifications:', err),
    });
  }

  // =========================================================================
  // 6. UI HELPER METHODS (Icons & Colors)
  // =========================================================================

  getActionIcon(action: string): string {
    const icons: Record<string, string> = {
      CREATE: 'bi-plus-circle text-success',
      UPDATE: 'bi-pencil-square text-primary',
      DELETE: 'bi-trash text-danger',
      LOGIN: 'bi-box-arrow-in-right text-info',
    };
    return icons[action] || 'bi-clock text-secondary';
  }

  getStatusClass(status: string): string {
    const colorMap: Record<string, string> = {
      DRAFT: 'bg-secondary text-white',
      ISSUED: 'bg-primary text-white',
      PARTIALLY_RECEIVED: 'bg-info text-white',
      RECEIVED: 'bg-success text-white',
      CANCELLED: 'bg-danger text-white',
    };
    return colorMap[status] || 'bg-secondary text-white';
  }

  getRfqStatusClass(status: string): string {
    const colorMap: Record<string, string> = {
      PENDING: 'bg-warning text-dark',
      UNDER_REVIEW: 'bg-info text-dark',
      APPROVED: 'bg-success text-white',
      REJECTED: 'bg-danger text-white',
      EXPIRED: 'bg-secondary text-white',
    };
    return colorMap[status] || 'bg-secondary text-white';
  }

  getPrStatusClass(status: string): string {
    const colorMap: Record<string, string> = {
      APPROVED: 'bg-success-subtle text-success border border-success',
      PENDING: 'bg-warning-subtle text-warning border border-warning',
      REJECTED: 'bg-danger-subtle text-danger border border-danger',
      CANCELLED: 'bg-dark-subtle text-dark border border-dark',
    };
    return colorMap[status] || 'bg-warning-subtle text-warning border border-warning';
  }

  logout(): void {
    localStorage.removeItem('hasReceivedPO');
    this.storage.clearSession(); // Clear all saved user data
    this.router.navigate(['']); // Send back to login page
  }
}