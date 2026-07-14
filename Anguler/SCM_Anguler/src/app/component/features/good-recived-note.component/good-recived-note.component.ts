import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  GoodsReceivedNoteRequestModel,
  GoodsReceivedNoteResponseModel,
  GRNLineItemRequestModel,
} from '../../shared/model/goodRecivedNoteModel';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { GoodRecivedNoteService } from '../../../service/good-recived-note.service';
import { WarehouseService } from '../../../service/warehouse.service';
import { AddProductService } from '../../../service/add-product.service';
import { ManagerService } from '../../../service/manager.service';
import { QcInspectorService } from '../../../service/qc-inspactor.service';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-good-recived-note',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './good-recived-note.component.html',
  styleUrl: './good-recived-note.component.css',
})
export class GoodRecivedNoteComponent implements OnInit {
  grns: GoodsReceivedNoteResponseModel[] = [];
  purchaseOrders: any[] = [];
  warehouses: any[] = [];
  products: any[] = [];
  users: any[] = [];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;
  currentUserId: number = 0;

  grn: GoodsReceivedNoteRequestModel = {
    poId: 0,
    productId: 0,
    receivedQuantity: 0,
    receivedBy: 0,
    warehouseId: 0,
    receivedAt: '',
    status: 'PENDING',
    remarks: '',
    inspectedBy: null,
    inspectionDate: null,
    lineItems: [],
  };

  constructor(
    private service: GoodRecivedNoteService,
    private poService: PurchaseOrderService,
    private warehouseService: WarehouseService,
    private productService: AddProductService,
    private managerService: ManagerService,
    private qcInspectorService: QcInspectorService,
    private storage: StorageService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    const user = this.storage.getUser();
    if (user) {
      this.currentUserId = user.userId;
      this.grn.receivedBy = user.userId;
    }
    this.loadGRNs();
    this.loadPurchaseOrders();
    this.loadWarehouses();
    this.loadProducts();
    this.loadUsers();
  }

  loadGRNs() {
    this.service.findAll().subscribe({
      next: (data) => {
        this.grns = data || [];
        this.cdr.markForCheck();
      },
    });
  }

  loadPurchaseOrders() {
    this.poService.findAll().subscribe({ next: (data) => (this.purchaseOrders = data || []) });
  }

  loadWarehouses() {
    this.warehouseService.getAll().subscribe({ next: (data) => (this.warehouses = data || []) });
  }

  loadProducts() {
    this.productService.findAll().subscribe({ next: (data) => (this.products = data || []) });
  }

  loadUsers() {
    const managers$ = this.managerService.findAll();
    const inspectors$ = this.qcInspectorService.findAll();

    managers$.subscribe({
      next: (managers) => {
        const managerUsers = (managers || []).map((m: any) => ({
          id: m.userId || m.id,
          name: m.name || m.managerName,
          role: 'WAREHOUSE_MANAGER',
        }));
        inspectors$.subscribe({
          next: (inspectors) => {
            const inspectorUsers = (inspectors || []).map((i: any) => ({
              id: i.userId || i.id,
              name: i.name || i.inspectorName,
              role: 'QUALITY_INSPECTOR',
            }));
            this.users = [...managerUsers, ...inspectorUsers];
            this.cdr.markForCheck();
          },
          error: () => {
            this.users = managerUsers;
            this.cdr.markForCheck();
          },
        });
      },
      error: () => {
        inspectors$.subscribe({
          next: (inspectors) => {
            this.users = (inspectors || []).map((i: any) => ({
              id: i.userId || i.id,
              name: i.name || i.inspectorName,
              role: 'QUALITY_INSPECTOR',
            }));
            this.cdr.markForCheck();
          },
          error: () => {
            this.users = [];
          },
        });
      },
    });
  }

  addLineItem() {
    const newItem: GRNLineItemRequestModel = {
      productId: 0,
      quantityOrdered: 0,
      quantityReceived: 0,
    };
    this.grn.lineItems.push(newItem);
    this.cdr.markForCheck();
  }

  removeLineItem(index: number) {
    this.grn.lineItems.splice(index, 1);
    this.cdr.markForCheck();
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

  save() {
    this.errorMessage = null;

    if (!this.grn.poId || !this.grn.warehouseId || !this.grn.receivedBy) {
      this.errorMessage =
        'Validation Fault: Linked PO Node, Destination Warehouse, and Receiver Context are mandatory.';
      return;
    }

    const payload: GoodsReceivedNoteRequestModel = {
      ...this.grn,
      poId: +this.grn.poId,
      warehouseId: +this.grn.warehouseId,
      productId: this.grn.productId ? +this.grn.productId : 0,
      receivedBy: +this.grn.receivedBy,
      inspectedBy: this.grn.inspectedBy ? +this.grn.inspectedBy : null,
      lineItems: this.grn.lineItems.map((item) => ({
        ...item,
        productId: +item.productId,
      })),
    };

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, payload).subscribe({
        next: () => {
          alert('Goods Received Note modified cleanly.');
          this.closeDrawer();
          this.loadGRNs();
        },
        error: (err) =>
          (this.errorMessage = err.error?.message || 'Operational layout modification failure.'),
      });
    } else {
      this.service.save(payload).subscribe({
        next: () => {
          alert('New GRN Ledger generated and approved inside inventory matrix.');
          this.closeDrawer();
          this.loadGRNs();
        },
        error: (err) =>
          (this.errorMessage = err.error?.message || 'Inventory integration exception.'),
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
      lineItems: [],
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (
      confirm(
        'Definitively remove this Goods Received Note record? Warning: Inventory balances will remain unaffected.',
      )
    ) {
      this.service.delete(id).subscribe({
        next: () => {
          alert('GRN ledger node cleanly removed.');
          this.loadGRNs();
        },
        error: (err) => alert(err.error?.message || err.message),
      });
    }
  }

  reset() {
    this.grn = {
      poId: 0,
      productId: 0,
      receivedQuantity: 0,
      receivedBy: this.currentUserId,
      warehouseId: 0,
      receivedAt: '',
      status: 'PENDING',
      remarks: '',
      inspectedBy: null,
      inspectionDate: null,
      lineItems: [],
    };
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}
