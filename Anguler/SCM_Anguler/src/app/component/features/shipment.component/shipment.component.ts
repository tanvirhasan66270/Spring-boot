import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ShipmentRequestModel, ShipmentResponseModel } from '../../shared/model/shipmentModel';
import { ShipmentService } from '../../../service/shipment.service';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { SupplierService } from '../../../service/supplier.service';
import { StorageService, KEYS } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-shipment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './shipment.component.html',
  styleUrl: './shipment.component.css',
})
export class ShipmentComponent implements OnInit {
  shipments: ShipmentResponseModel[] = [];
  filteredShipments: ShipmentResponseModel[] = [];
  purchaseOrders: any[] = [];
  suppliers: any[] = [];

  // 🔍 ডুয়াল সার্চ বাফার মডেলস
  searchNo: string = '';
  searchVehicle: string = '';

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentEditId: number | null = null;
  selectedFile: File | null = null;
  currentUserEmail: string = '';

  userRole: string = '';
  currentSupplierId: number | null = null;
  currentSupplierName: string = ''; 

  shipment: ShipmentRequestModel = {
    poId: 0,
    supplierId: 0,
    vehicleNumber: '',
    captainRegistrationNumber: '',
    assignedByEmail: '',
    origin: '',
    sendByAddress: '',
    estimatedDelivery: '',
    transportCost: 0,
    podFileUrl: '',
  };

  constructor(
    private service: ShipmentService,
    private poService: PurchaseOrderService,
    private supplierService: SupplierService,
    private storage: StorageService,
    private cdr: ChangeDetectorRef,
  ) {}
ngOnInit() {
  this.userRole = this.storage.getActiveRole()?.toUpperCase() || '';
  const user = this.storage.getUser();
  if (user) {
    this.currentUserEmail = user.email;
    this.shipment.assignedByEmail = user.email;
    if (!this.userRole && user.role) {
      this.userRole = user.role.toUpperCase();
    }
  }

  // 🔒 ক্যাশ মেমরি থেকে কারেন্ট সাপ্লায়ারের আইডি এবং নাম সঠিকভাবে রিড করা
  const cachedSupplier = this.storage.getData(KEYS.SUPPLIER) as any;
  if (cachedSupplier) {
    this.currentSupplierId = cachedSupplier.id;
    // ক্যাশ অবজেক্টে 'name' বা 'companyName' থাকতে পারে, তাই সেফটি চেক রাখা হলো
    this.currentSupplierName = cachedSupplier.name || cachedSupplier.companyName || user?.name || 'Your Supplier Account';
  } else {
    this.currentSupplierName = user?.name || 'Your Supplier Account';
  }

  this.loadShipments();
  this.loadPurchaseOrders();
  this.loadSuppliers();
}



  loadShipments() {
    this.service.findAll().subscribe({
      next: (data) => {
        const allShipments = data || [];

        // 🔒 CLIENT SIDE ISOLATION
        if (this.userRole === 'SUPPLIER' && this.currentSupplierId) {
          this.shipments = allShipments.filter((s: any) => {
            const sId = s.supplierId || (s.supplier ? s.supplier.id : null);
            return sId === this.currentSupplierId;
          });
        } else {
          this.shipments = allShipments;
        }

        this.filteredShipments = [...this.shipments];
        this.applyDoubleSearch();
        this.cdr.markForCheck();
      },
    });
  }

  // 🎯 ডুয়াল সার্চ ইঞ্জিন প্রসেসর
  applyDoubleSearch() {
    const noTerm = this.searchNo.toLowerCase().trim();
    const vehicleTerm = this.searchVehicle.toLowerCase().trim();

    this.filteredShipments = this.shipments.filter(s => {
      const shipNo = s.shipmentNumber || '';
      const vehicleNo = s.vehicleNumber || '';

      const matchesNo = shipNo.toLowerCase().includes(noTerm);
      const matchesVehicle = vehicleNo.toLowerCase().includes(vehicleTerm);

      return matchesNo && matchesVehicle;
    });
    this.cdr.markForCheck();
  }

  loadPurchaseOrders() {
    if (this.userRole === 'SUPPLIER') return;
    this.poService.findAll().subscribe({ next: (data) => (this.purchaseOrders = data || []) });
  }

  loadSuppliers() {
    if (this.userRole === 'SUPPLIER') return;
    this.supplierService.findAll().subscribe({ next: (data) => (this.suppliers = data || []) });
  }

  onFileChange(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

openDrawer() {
  this.reset();
  this.isEdit = false;
  
  if (this.userRole === 'SUPPLIER' && this.currentSupplierId) {
    this.shipment.supplierId = this.currentSupplierId;
  }
  
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

    if (this.shipment.poId === 0 || this.shipment.supplierId === 0) {
      this.errorMessage = 'Validation Error: Cluster reference fields (Purchase Order ID, Vendor Node) are required.';
      return;
    }

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.shipment, this.selectedFile).subscribe({
        next: () => {
          alert('Shipment logistics registry updated successfully.');
          this.closeDrawer();
          this.loadShipments();
        },
        error: (err) => (this.errorMessage = err.error?.message || 'Modification matrix deployment failure.'),
      });
    } else {
      this.service.save(this.shipment, this.selectedFile).subscribe({
        next: () => {
          alert('New Cargo Dispatch Node authorized and registered.');
          this.closeDrawer();
          this.loadShipments();
        },
        error: (err) => (this.errorMessage = err.error?.message || 'Logistics initialization exception.'),
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
      podFileUrl: o.podFileUrl || '',
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm('Definitively remove this cargo shipment track node from core databases?')) {
      this.service.delete(id).subscribe({
        next: () => {
          alert('Shipment record successfully pruned.');
          this.loadShipments();
        },
        error: (err) => alert(err.error?.message || err.message),
      });
    }
  }

  reset() {
    this.shipment = {
      poId: 0,
      supplierId: 0,
      vehicleNumber: '',
      captainRegistrationNumber: '',
      assignedByEmail: this.currentUserEmail,
      origin: '',
      sendByAddress: '',
      estimatedDelivery: '',
      transportCost: 0,
      podFileUrl: '',
    };
    this.selectedFile = null;
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}