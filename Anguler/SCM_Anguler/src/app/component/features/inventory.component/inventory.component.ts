import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryRequestModel, InventoryResponseModel } from '../../shared/model/inventoryModel';
import { InventoryService } from '../../../service/inventory.service';
import { AddProductService } from '../../../service/add-product.service';
import { WarehouseService } from '../../../service/warehouse.service';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory.component.html',
  styleUrl: './inventory.component.css'
})
export class InventoryComponent implements OnInit {

  inventories: InventoryResponseModel[] = [];
  products: any[] = [];
  warehouses: any[] = [];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;

  // ইনিশিয়াল ব্ল্যাঙ্ক রিকোয়েস্ট অবজেক্ট স্টেটম্যাপ
  stock: InventoryRequestModel = {
    productId: 0,
    warehouseId: 0,
    quantityOnHand: 0,
    quantityReserved: 0,
    locationStatus: '',
    expiryDate: '',
    stockStatus:'IN_STOCK'
  };

  constructor(
    private service: InventoryService,
    private productService: AddProductService,
    private warehouseService: WarehouseService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadInventoryMatrix();
    this.loadProducts();
    this.loadWarehouses();
  }

  loadInventoryMatrix() {
    this.service.findAll().subscribe({ 
      next: (data) => { this.inventories = data || []; this.cdr.markForCheck(); },
      error: (err) => this.handleErrorLog(err)
    });
  }

  loadProducts() {
    this.productService.findAll().subscribe({ next: (data) => this.products = data || [] });
  }

  loadWarehouses() {
    this.warehouseService.getAll().subscribe({ next: (data) => this.warehouses = data || [] });
  }

  openDrawer() { this.reset(); this.isEdit = false; this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.reset(); this.cdr.markForCheck(); }

  save() {
    this.errorMessage = null;

    if (!this.stock.productId || +this.stock.productId === 0 || 
        !this.stock.warehouseId || +this.stock.warehouseId === 0) {
      this.errorMessage = "Validation Fault: Targeted Product and Storage Warehouse Node must be specified.";
      this.cdr.markForCheck();
      return;
    }

    // পিওর ডাটা টাইপ ইন্টিগ্রিটি এনফোর্সমেন্ট
    const payload: InventoryRequestModel = {
      productId: +this.stock.productId,
      warehouseId: +this.stock.warehouseId,
      quantityOnHand: +this.stock.quantityOnHand,
      quantityReserved: +this.stock.quantityReserved,
      locationStatus: this.stock.locationStatus || '',
      expiryDate: this.stock.expiryDate || undefined,
      stockStatus: this.stock.stockStatus
    };

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, payload).subscribe({
        next: () => { alert("Inventory Stock metrics updated successfully."); this.closeDrawer(); this.loadInventoryMatrix(); },
        error: (err) => this.handleErrorLog(err)
      });
    } else {
      this.service.save(payload).subscribe({
        next: () => { alert("New Stock record localized inside the repository."); this.closeDrawer(); this.loadInventoryMatrix(); },
        error: (err) => this.handleErrorLog(err)
      });
    }
  }

  edit(i: InventoryResponseModel) {
    this.errorMessage = null;
    this.currentEditId = i.id;
    this.isEdit = true;
    this.stock = {
      productId: i.productId,
      warehouseId: i.warehouseId,
      quantityOnHand: i.quantityOnHand,
      quantityReserved: i.quantityReserved,
      locationStatus: i.locationStatus || '',
      expiryDate: i.expiryDate || '',
      stockStatus: i.stockStatus
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Are you sure you want to completely purge this inventory record from the SCM storage maps?")) {
      this.service.delete(id).subscribe({
        next: () => { alert("Stock node successfully destroyed."); this.loadInventoryMatrix(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  private handleErrorLog(err: any) {
    console.error("Inventory Core Error Exception:", err);
    this.errorMessage = err.error?.message || err.message || "400 Bad Request: Mapping parameter fault.";
    this.cdr.markForCheck();
  }

  reset() {
    this.stock = {
      productId: 0,
      warehouseId: 0,
      quantityOnHand: 0,
      quantityReserved: 0,
      locationStatus: '',
      expiryDate: '',
      stockStatus: 'IN_STOCK'
    };
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}
