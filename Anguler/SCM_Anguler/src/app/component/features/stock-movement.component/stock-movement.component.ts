import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StockMovementRequestModel, StockMovementResponseModel } from '../../shared/model/stock-movement';
import { StockMovementService } from '../../../service/stock-movement.service';
import { AddProductService } from '../../../service/add-product.service';
import { WarehouseService } from '../../../service/warehouse.service';

@Component({
  selector: 'app-stock-movement',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stock-movement.component.html',
  styleUrl: './stock-movement.component.css'
})
export class StockMovementComponent implements OnInit {

  movements: StockMovementResponseModel[] = [];
  products: any[] = [];
  warehouses: any[] = [];
  
  errorMessage: string | null = null;
  isDrawerOpen = false;

  // ইনিশিয়াল রিকোয়েস্ট ডিটিও অবজেক্ট
  formModel: StockMovementRequestModel = {
    productId: 0,
    warehouseId: 0,
    sourceWarehouseId: null,
    movementType: 'INWARD',
    quantity: 0,
    referenceId: '',
    performedBy: 0, 
    remarks: ''
  };

  constructor(
    private service: StockMovementService,
    private productService: AddProductService,
    private warehouseService: WarehouseService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadMovements();
    this.loadProducts();
    this.loadWarehouses();
  }

  loadMovements() {
    this.service.findAll().subscribe({
      next: (data) => { this.movements = data || []; this.cdr.markForCheck(); },
      error: (err) => this.handleErrorLog(err)
    });
  }

  loadProducts() {
    this.productService.findAll().subscribe({ next: (data) => this.products = data || [] });
  }

  loadWarehouses() {
    this.warehouseService.getAll().subscribe({ next: (data) => this.warehouses = data || [] });
  }

  onMovementTypeChange() {
    if (this.formModel.movementType !== 'TRANSFER') {
      this.formModel.sourceWarehouseId = null;
    }
  }

  openDrawer() { this.resetForm(); this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.resetForm(); this.cdr.markForCheck(); }

  submitMovement() {
    this.errorMessage = null;

    if (!this.formModel.productId || +this.formModel.productId === 0 ||
        !this.formModel.warehouseId || +this.formModel.warehouseId === 0) {
      this.errorMessage = "Validation Fault: Product specs and Destination Warehouse node must be assigned.";
      this.cdr.markForCheck();
      return;
    }

    if (this.formModel.movementType === 'TRANSFER') {
      if (!this.formModel.sourceWarehouseId || +this.formModel.sourceWarehouseId === 0) {
        this.errorMessage = "Validation Fault: Origin Source Warehouse context is required for TRANSFER type.";
        this.cdr.markForCheck();
        return;
      }
      if (+this.formModel.warehouseId === +this.formModel.sourceWarehouseId!) {
        this.errorMessage = "Business Conflict: Source warehouse and Target destination warehouse cannot be identical.";
        this.cdr.markForCheck();
        return;
      }
    }

    const payload: StockMovementRequestModel = {
      productId: +this.formModel.productId,
      warehouseId: +this.formModel.warehouseId,
      sourceWarehouseId: this.formModel.sourceWarehouseId ? +this.formModel.sourceWarehouseId : null,
      movementType: this.formModel.movementType,
      quantity: +this.formModel.quantity,
      referenceId: this.formModel.referenceId.trim(),
      performedBy: +this.formModel.performedBy,
      remarks: this.formModel.remarks?.trim() || ''
    };

    this.service.logMovement(payload).subscribe({
      next: () => {
        alert("Stock Transaction Logged successfully into ledger maps.");
        this.closeDrawer();
        this.loadMovements();
      },
      error: (err) => this.handleErrorLog(err)
    });
  }

  deleteLog(id: number) {
    if (confirm("Are you sure you want to delete this log transaction? (Inventory reconciliation needs to be handled manually)")) {
      this.service.delete(id).subscribe({
        next: () => { alert("Ledger item removed."); this.loadMovements(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  private handleErrorLog(err: any) {
    console.error("SCM Ledger Exception Stack:", err);
    this.errorMessage = err.error?.message || err.message || "400 Bad Request: Parameter serialization constraint.";
    this.cdr.markForCheck();
  }

  resetForm() {
    this.formModel = {
      productId: 0,
      warehouseId: 0,
      sourceWarehouseId: null,
      movementType: 'INWARD',
      quantity: 0,
      referenceId: '',
      performedBy: 1,
      remarks: ''
    };
    this.errorMessage = null;
  }
}
