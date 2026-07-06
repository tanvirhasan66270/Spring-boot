import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CustomerService } from '../../../service/customer.service';
import { CustomerOrderRequestModel, CustomerOrderResponseModel, OrderLineItemRequestModel } from '../../shared/model/customerOrder';
import { CustomerOrderService } from '../../../service/customer-order.service';
import { AddProductService } from '../../../service/add-product.service';


@Component({
  selector: 'app-customer-order',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customer-order.component.html',
  styleUrl: './customer-order.component.css',
})
export class CustomerOrderComponent implements OnInit {

  orders: CustomerOrderResponseModel[] = [];
  customers: any[] = [];
  products: any[] = [];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;

  currentProduct: any = null;
  currentQuantity: number = 1;
  currentRemarks: string = '';

  order: CustomerOrderRequestModel = {
    customerId: 0,
    deliveryAddress: '',
    estimatedDelivery: '',
    serviceType: 'STANDARD',
    codAmount: 0,
    status: 'PENDING',
    items: []
  };

  constructor(
    private service: CustomerOrderService,
    private customerService: CustomerService,
    private productService: AddProductService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadOrders();
    this.loadCustomers();
    this.loadProducts();
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

  loadOrders() {
    this.service.findAll().subscribe({
      next: (data) => {
        this.orders = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  loadCustomers() {
    this.customerService.getAll().subscribe({
      next: (data) => {
        this.customers = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  loadProducts() {
    this.productService.findAll().subscribe({
      next: (data) => {
        this.products = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  addItem() {
    if (!this.currentProduct) {
      alert("Please select an operational product template node!");
      return;
    }
    if (this.currentQuantity <= 0) {
      alert("Quantity configuration metrics must be positive!");
      return;
    }

    const existingItem = this.order.items.find(x => x.productId == this.currentProduct.id);
    if (existingItem) {
      existingItem.quantity += this.currentQuantity;
    } else {
      const newItem: OrderLineItemRequestModel = {
        id: 0, 
        productId: this.currentProduct.id,
        quantity: this.currentQuantity,
        unitPrice: this.currentProduct.sellingPrice,
        remarks: this.currentRemarks
      };
      this.order.items.push(newItem);
    }

    this.currentProduct = null;
    this.currentQuantity = 1;
    this.currentRemarks = '';
    this.cdr.markForCheck();
  }

  removeItem(index: number) {
    this.order.items.splice(index, 1);
    this.cdr.markForCheck();
  }

  getProductName(productId: number): string {
    return this.products.find(p => p.id == productId)?.name || 'Unknown Item';
  }

  private handleBackendError(err: any) {
    this.errorMessage = err.error?.message || err.message || 'An unmapped transactional mutation dropped.';
    this.cdr.markForCheck();
  }

  save() {
    this.errorMessage = null;

    if (this.order.customerId === 0) {
      this.errorMessage = 'Validation Fault: Associate a target customer profile token context.';
      return;
    }

    if (this.order.items.length === 0) {
      this.errorMessage = 'Validation Fault: Target allocation package requires at least one product row.';
      return;
    }

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.order).subscribe({
        next: () => {
          alert("Customer purchase ledger instance mutated successfully!");
          this.closeDrawer();
          this.loadOrders();
        },
        error: (err) => this.handleBackendError(err)
      });
    } else {
      this.service.save(this.order).subscribe({
        next: () => {
          alert("New customer purchase order dispatched and authorized!");
          this.closeDrawer();
          this.loadOrders();
        },
        error: (err) => this.handleBackendError(err)
      });
    }
  }

  edit(o: CustomerOrderResponseModel) {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;

    this.order = {
      customerId: o.customerId,
      deliveryAddress: o.deliveryAddress,
      estimatedDelivery: o.estimatedDelivery,
      serviceType: o.serviceType,
      codAmount: o.codAmount,
      status: o.status,
      items: o.lineItems.map(item => ({
        id: item.id,
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        remarks: item.remarks || ''
      }))
    };

    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Definitively remove this purchase order sequence from active cluster tables?")) {
      this.service.delete(id).subscribe({
        next: () => {
          alert("Order instance completely wiped.");
          this.loadOrders();
        },
        error: (err) => this.handleBackendError(err)
      });
    }
  }

  reset() {
    this.order = {
      customerId: 0,
      deliveryAddress: '',
      estimatedDelivery: '',
      serviceType: 'STANDARD',
      codAmount: 0,
      status: 'PENDING',
      items: []
    };
    this.currentProduct = null;
    this.currentQuantity = 1;
    this.currentRemarks = '';
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}