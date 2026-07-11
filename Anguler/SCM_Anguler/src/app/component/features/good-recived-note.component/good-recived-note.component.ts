import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GoodsReceivedNoteRequestModel, GoodsReceivedNoteResponseModel, GRNLineItemRequestModel } from '../../shared/model/goodRecivedNoteModel';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { GoodRecivedNoteService } from '../../../service/good-recived-note.service';
import { WarehouseService } from '../../../service/warehouse.service';
import { AddProductService } from '../../../service/add-product.service';


@Component({
  selector: 'app-good-recived-note',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './good-recived-note.component.html',
  styleUrl: './good-recived-note.component.css'
})
export class GoodRecivedNoteComponent implements OnInit {

  grns: GoodsReceivedNoteResponseModel[] = [];
  purchaseOrders: any[] = [];
  warehouses: any[] = [];
  products: any[] = [];
  users: any[] = [ 
    { id: 1, name: 'Tanvir Rahman', role: 'WAREHOUSE_MANAGER' },
    { id: 2, name: 'Israt Jahan', role: 'QUALITY_INSPECTOR' }
  ];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;

  grn: GoodsReceivedNoteRequestModel = {
    poId: 0,
    productId: 0,
    receivedQuantity: 0,
    receivedBy: 1, 
    warehouseId: 0,
    receivedAt: '',
    status: 'PENDING',
    remarks: '',
    inspectedBy: null,
    inspectionDate: null,
    lineItems: []
  };

  constructor(
    private service: GoodRecivedNoteService,
    private poService: PurchaseOrderService,
    private warehouseService: WarehouseService,
    private productService: AddProductService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadGRNs();
    this.loadPurchaseOrders();
    this.loadWarehouses();
    this.loadProducts();
  }

  loadGRNs() {
    this.service.findAll().subscribe({ next: (data) => { this.grns = data || []; this.cdr.markForCheck(); } });
  }

  loadPurchaseOrders() {
    this.poService.findAll().subscribe({ next: (data) => this.purchaseOrders = data || [] });
  }

  loadWarehouses() {
    this.warehouseService.getAll().subscribe({ next: (data) => this.warehouses = data || [] });
  }

  loadProducts() {
    this.productService.findAll().subscribe({ next: (data) => this.products = data || [] });
  }

  //  চাইল্ড লাইন আইটেম কন্ট্রোল লজিক ──
  addLineItem() {
    const newItem: GRNLineItemRequestModel = {
      productId: 0,
      quantityOrdered: 0,
      quantityReceived: 0
    };
    this.grn.lineItems.push(newItem);
    this.cdr.markForCheck();
  }

  removeLineItem(index: number) {
    this.grn.lineItems.splice(index, 1);
    this.cdr.markForCheck();
  }

  openDrawer() { this.reset(); this.isEdit = false; this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.reset(); this.cdr.markForCheck(); }

  save() {
    this.errorMessage = null;

    if (!this.grn.poId || !this.grn.warehouseId || !this.grn.receivedBy) {
      this.errorMessage = "Validation Fault: Linked PO Node, Destination Warehouse, and Receiver Context are mandatory.";
      return;
    }

    // ব্যাকএন্ড ডাবল ম্যাপিং সেফটি ট্র্যাকার কাস্টিং
    const payload: GoodsReceivedNoteRequestModel = {
      ...this.grn,
      poId: +this.grn.poId,
      warehouseId: +this.grn.warehouseId,
      productId: this.grn.productId ? +this.grn.productId : 0,
      receivedBy: +this.grn.receivedBy,
      inspectedBy: this.grn.inspectedBy ? +this.grn.inspectedBy : null,
      lineItems: this.grn.lineItems.map(item => ({
        ...item,
        productId: +item.productId
      }))
    };

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, payload).subscribe({
        next: () => { alert("Goods Received Note modified cleanly."); this.closeDrawer(); this.loadGRNs(); },
        error: (err) => this.errorMessage = err.error?.message || "Operational layout modification failure."
      });
    } else {
      this.service.save(payload).subscribe({
        next: () => { alert("New GRN Ledger generated and approved inside inventory matrix."); this.closeDrawer(); this.loadGRNs(); },
        error: (err) => this.errorMessage = err.error?.message || "Inventory integration exception."
      });
    }
  }

  edit(o: GoodsReceivedNoteResponseModel) {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;
    this.grn = {
      poId: o.poId,
      productId: o.productId || 0,
      receivedQuantity: o.receivedQuantity,
      receivedBy: o.receivedBy,
      warehouseId: o.warehouseId,
      receivedAt: o.receivedAt,
      status: o.status,
      remarks: o.remarks,
      inspectedBy: o.inspectedBy,
      inspectionDate: o.inspectionDate,
      lineItems: [] 
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Definitively remove this Goods Received Note record? Warning: Inventory balances will remain unaffected.")) {
      this.service.delete(id).subscribe({
        next: () => { alert("GRN ledger node cleanly removed."); this.loadGRNs(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  reset() {
    this.grn = {
      poId: 0,
      productId: 0,
      receivedQuantity: 0,
      receivedBy: 1,
      warehouseId: 0,
      receivedAt: '',
      status: 'PENDING',
      remarks: '',
      inspectedBy: null,
      inspectionDate: null,
      lineItems: []
    };
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}
