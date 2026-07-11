import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { POLineItemRequestDTO, POLineItemResponseDTO } from '../../shared/model/pOLineItemModel';
import { PoLineItemService } from '../../../service/po-line-item.service';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { AddProductService } from '../../../service/add-product.service';


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
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadLineItems();
    this.loadPurchaseOrders();
    this.loadProducts();
  }

  loadLineItems() {
    this.service.findAll().subscribe({
      next: (data) => { this.lineItems = data || []; this.cdr.markForCheck(); }
    });
  }

  loadPurchaseOrders() {
    this.poService.findAll().subscribe({ next: (data) => this.purchaseOrders = data || [] });
  }

  loadProducts() {
    this.productService.findAll().subscribe({ next: (data) => this.products = data || [] });
  }

  onPoChange(event: any) {
    const poId = +event.target.value;
    const targetPo = this.purchaseOrders.find(po => po.id === poId);
    if (targetPo) {
      this.item.poNumber = targetPo.poNumber; // প্যারেন্ট PO নাম্বার অটো সিঙ্ক
    }
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
