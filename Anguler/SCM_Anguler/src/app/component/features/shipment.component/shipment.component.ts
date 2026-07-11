import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ShipmentRequestModel, ShipmentResponseModel } from '../../shared/model/shipmentModel';
import { ShipmentService } from '../../../service/shipment.service';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { SupplierService } from '../../../service/supplier.service';


@Component({
  selector: 'app-shipment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './shipment.component.html',
  styleUrl: './shipment.component.css'
})
export class ShipmentComponent implements OnInit {

  shipments: ShipmentResponseModel[] = [];
  purchaseOrders: any[] = [];
  suppliers: any[] = [];

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;
  selectedFile: File | null = null;

  shipment: ShipmentRequestModel = {
    poId: 0,
    supplierId: 0,
    vehicleNumber: '',
    captainRegistrationNumber: '',
    assignedByEmail: 'logistics@enterprise.com',
    origin: '',
    sendByAddress: '',
    estimatedDelivery: '',
    transportCost: 0,
    podFileUrl: ''
  };

  constructor(
    private service: ShipmentService,
    private poService: PurchaseOrderService,
    private supplierService: SupplierService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadShipments();
    this.loadPurchaseOrders();
    this.loadSuppliers();
  }

  loadShipments() {
    this.service.findAll().subscribe({
      next: (data) => { this.shipments = data || []; this.cdr.markForCheck(); }
    });
  }

  loadPurchaseOrders() {
    this.poService.findAll().subscribe({ next: (data) => this.purchaseOrders = data || [] });
  }

  loadSuppliers() {
    this.supplierService.findAll().subscribe({ next: (data) => this.suppliers = data || [] });
  }

  onFileChange(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  openDrawer() { this.reset(); this.isEdit = false; this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.reset(); this.cdr.markForCheck(); }

  save() {
    this.errorMessage = null;

    if (this.shipment.poId === 0 || this.shipment.supplierId === 0) {
      this.errorMessage = "Validation Error: Cluster reference fields (Purchase Order ID, Vendor Node) are required.";
      return;
    }

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.shipment, this.selectedFile).subscribe({
        next: () => { alert("Shipment logistics registry updated successfully."); this.closeDrawer(); this.loadShipments(); },
        error: (err) => this.errorMessage = err.error?.message || "Modification matrix deployment failure."
      });
    } else {
      this.service.save(this.shipment, this.selectedFile).subscribe({
        next: () => { alert("New Cargo Dispatch Node authorized and registered."); this.closeDrawer(); this.loadShipments(); },
        error: (err) => this.errorMessage = err.error?.message || "Logistics initialization exception."
      });
    }
  }

  edit(o: ShipmentResponseModel) {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;
    this.shipment = {
      poId: o.poId,
      supplierId: o.supplierId,
      vehicleNumber: o.vehicleNumber,
      captainRegistrationNumber: o.captainRegistrationNumber,
      assignedByEmail: o.assignedByEmail,
      origin: o.origin,
      sendByAddress: o.sendByAddress,
      estimatedDelivery: o.estimatedDelivery,
      transportCost: o.transportCost,
      podFileUrl: o.podFileUrl || ''
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Definitively remove this cargo shipment track node from core databases?")) {
      this.service.delete(id).subscribe({
        next: () => { alert("Shipment record successfully pruned."); this.loadShipments(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  reset() {
    this.shipment = {
      poId: 0,
      supplierId: 0,
      vehicleNumber: '',
      captainRegistrationNumber: '',
      assignedByEmail: 'logistics@enterprise.com',
      origin: '',
      sendByAddress: '',
      estimatedDelivery: '',
      transportCost: 0,
      podFileUrl: ''
    };
    this.selectedFile = null;
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}
