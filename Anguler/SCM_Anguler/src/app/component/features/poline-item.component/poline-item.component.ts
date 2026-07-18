import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { POLineItemRequestDTO, POLineItemResponseDTO } from '../../shared/model/pOLineItemModel';
import { PoLineItemService } from '../../../service/po-line-item.service';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { AddProductService } from '../../../service/add-product.service';
import { StorageService } from '../../../auth/auth_service/storage.service';
import { SupplierService } from '../../../service/supplier.service';

@Component({
  selector: 'app-poline-item',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './poline-item.component.html',
  styleUrl: './poline-item.component.css',
})
export class POLineItemComponent implements OnInit {

  lineItems: POLineItemResponseDTO[] = [];
  purchaseOrders: any[] = [];
  products: any[] = [];

  currentSupplierId: number | null = null;
  activeRole: string = 'CUSTOMER';
  searchSupplier: string = '';
searchProduct: string = '';
searchStatus: string = ''; 
filteredLineItems: POLineItemResponseDTO[] = [];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;
  trackingSearchQuery = '';

  item: POLineItemRequestDTO = {
    poId: 0,
    productId: 0,
    quantity: 1,
    unitPrice: 0,
    quotationRef: '',
    poNumber: '',
    deliveryDate: '',
    shipmentMethod: '',
    notes: '',
    status: 'PENDING'
  };

  constructor(
    private service: PoLineItemService,
    private poService: PurchaseOrderService,
    private productService: AddProductService,
    private supplierService: SupplierService,
    private storage: StorageService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.activeRole = this.storage.getActiveRole();
    const user = this.storage.getUser();
    
    if (user && this.activeRole === 'SUPPLIER') {
      this.supplierService.getSupplierByUserId(user.userId).subscribe({
        next: (res) => {
          if (res) {
            this.currentSupplierId = res.id;
            this.loadSecurePipelineData();
          }
        },
        error: (err) => console.error('Failed to resolve supplier identity:', err)
      });
    } else {
      // অন্যান্য রোলের (ADMIN/MANAGER) জন্য গ্লোবাল ডেটা লোড
      this.loadSecurePipelineData();
    }
  }

  loadSecurePipelineData() {
    this.loadLineItems();
    this.loadPurchaseOrders();
  }
  applyFilters() {
  const sName = this.searchSupplier.toLowerCase().trim();
  const pName = this.searchProduct.toLowerCase().trim();
  const status = this.searchStatus.toLowerCase().trim();

  this.filteredLineItems = this.lineItems.filter(i => {
    return (i.supplierName?.toLowerCase().includes(sName) || '') &&
           (i.productName?.toLowerCase().includes(pName) || '') &&
           (i.status?.toLowerCase().includes(status) || '');
  });
  this.cdr.markForCheck();
}

  loadLineItems() {
    this.service.findAll().subscribe({
      next: (data) => {
        const allItems = data || [];
        if (this.activeRole === 'SUPPLIER' && this.currentSupplierId) {
          this.lineItems = allItems.filter((i: any) => {
            const sId = i.supplierId || (i.supplier ? i.supplier.id : null);
            return sId === this.currentSupplierId;
          });
        } else {
          this.lineItems = allItems;
        }
        this.cdr.markForCheck();
      }
    });
  }

  loadPurchaseOrders() {
    this.poService.findAll().subscribe({
      next: (data) => {
        const allPOs = data || [];
        if (this.activeRole === 'SUPPLIER' && this.currentSupplierId) {
          this.purchaseOrders = allPOs.filter((po: any) => {
            const sId = po.supplierId || (po.supplier ? po.supplier.id : null);
            return sId === this.currentSupplierId;
          });
          
          this.extractProductsFromSupplierPOs();
        } else {
          this.purchaseOrders = allPOs;
          this.loadAllGlobalProducts();
        }
        this.cdr.markForCheck();
      }
    });
  }

  
  onPoChange(event: any) {
    const poId = +event.target.value;
    
    // ১. সিলেক্ট করা PO অবজেক্ট খুঁজে বের করা
    const targetPo = this.purchaseOrders.find(po => po.id === poId);
    
    if (targetPo && targetPo.status === 'RECEIVED') {
      this.item.poNumber = targetPo.poNumber; // প্যারেন্ট PO নাম্বার অটো সিঙ্ক
      const productMap = new Map();
      
      // ক) যদি PO অবজেক্টের ভেতরে line items বা items অ্যারে থাকে:
      if (targetPo.items && Array.isArray(targetPo.items)) {
        targetPo.items.forEach((item: any) => { 
          if (item.product) productMap.set(item.product.id, item.product); 
        });
      } 
      else if (targetPo.poLineItems && Array.isArray(targetPo.poLineItems)) {
        targetPo.poLineItems.forEach((item: any) => { 
          if (item.product) productMap.set(item.product.id, item.product); 
        });
      }
      // খ) যদি PO অবজেক্টের সাথে সরাসরি সিঙ্গেল প্রোডাক্ট ম্যাপ করা থাকে:
      else if (targetPo.product) {
        productMap.set(targetPo.product.id, targetPo.product);
      }
      
      // ড্রপডাউন অ্যারে আপডেট
      this.products = Array.from(productMap.values());
      
    } else {
      // যদি কোনো PO সিলেক্ট করা না থাকে বা সিলেক্টেড PO-এর স্ট্যাটাস RECEIVED না হয়, তবে প্রোডাক্ট ড্রপডাউন খালি থাকবে
      this.products = [];
      this.item.poNumber = '';
    }

    this.cdr.markForCheck(); 
  }

 
  extractProductsFromSupplierPOs() {
    const productMap = new Map();
    
    const receivedOrders = this.purchaseOrders.filter(order => order.status === 'RECEIVED');
    
    receivedOrders.forEach((order: any) => {
      if (order.items && Array.isArray(order.items)) {
        order.items.forEach((item: any) => {
          if (item.product) {
            productMap.set(item.product.id, item.product);
          }
        });
      } 
      else if (order.poLineItems && Array.isArray(order.poLineItems)) {
        order.poLineItems.forEach((item: any) => {
          if (item.product) {
            productMap.set(item.product.id, item.product);
          }
        });
      }
      else if (order.product) {
        productMap.set(order.product.id, order.product);
      }
    });

    this.products = Array.from(productMap.values());
    this.cdr.markForCheck();
  }

  loadAllGlobalProducts() {
    this.productService.findAll().subscribe({
      next: (data) => {
        this.products = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  openDrawer() { this.reset(); this.isEdit = false; this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.reset(); this.cdr.markForCheck(); }

  trackShipment() {
    if (!this.trackingSearchQuery.trim()) return;
    this.service.trackByNumber(this.trackingSearchQuery.trim()).subscribe({
      next: (res) => {
        alert(`Shipment Node Found!\nTracking: ${res.trackingNumber}\nStatus: ${res.status}\nMethod: ${res.shipmentMethod}`);
      },
      error: (err) => alert(err.error?.message || "Invalid Tracking Vector Number.")
    });
  }

  save() {
    this.errorMessage = null;

    if (this.item.poId === 0 || this.item.productId === 0) {
      this.errorMessage = "Validation Exception: Parent Purchase Order and Target Product mappings are mandatory.";
      return;
    }

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.item).subscribe({
        next: () => { alert("PO Line Item properties updated successfully."); this.closeDrawer(); this.loadLineItems(); },
        error: (err) => this.errorMessage = err.error?.message || "Warehouse stock or state machine validation fault."
      });
    } else {
      this.service.save(this.item).subscribe({
        next: () => { alert("New PO Line Item logged. Inventory reserved successfully."); this.closeDrawer(); this.loadLineItems(); },
        error: (err) => this.errorMessage = err.error?.message || "Insufficient warehouse stock vector."
      });
    }
  }

  edit(o: POLineItemResponseDTO) {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;
    this.item = {
      poId: o.poId,
      productId: o.productId,
      quantity: o.quantity,
      unitPrice: o.unitPrice,
      quotationRef: o.quotationRef || '',
      poNumber: o.poNumber || '',
      deliveryDate: o.deliveryDate,
      shipmentMethod: o.shipmentMethod || '',
      notes: o.notes || '',
      status: o.status
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Wipe this item and release reserved inventory quantity allocations?")) {
      this.service.delete(id).subscribe({
        next: () => { alert("Item pruned. Parent aggregates re-calculated."); this.loadLineItems(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  reset() {
    this.item = {
      poId: 0,
      productId: 0,
      quantity: 1,
      unitPrice: 0,
      quotationRef: '',
      poNumber: '',
      deliveryDate: '',
      shipmentMethod: '',
      notes: '',
      status: 'PENDING'
    };
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}