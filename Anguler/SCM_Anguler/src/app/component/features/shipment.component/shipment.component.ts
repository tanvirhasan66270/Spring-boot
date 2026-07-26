import { ChangeDetectorRef, Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ShipmentRequestModel, ShipmentResponseModel } from '../../shared/model/shipmentModel';
import { ShipmentService } from '../../../service/shipment.service';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { SupplierService } from '../../../service/supplier.service';
import { StorageService, KEYS } from '../../../auth/auth_service/storage.service';

// 🌟 jsPDF এবং html2canvas ইমপোর্ট
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import { environment } from '../../../../environment/environment';

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

  //  ডুয়াল সার্চ বাফার মডেলস
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

  isPdfModalOpen = false;
  selectedShipmentForPdf: ShipmentResponseModel | null = null;
  @ViewChild('pdfPreviewContainer') pdfPreviewContainer!: ElementRef;

readonly imageBaseUrl = environment.imgUrl + "shipments/";



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

        // 🌟 সেফটি চেক: যদি লজিস্টিক অফিসার বা অন্য কোনো রোলেও ডেটা ফিল্টার হয়ে জিরো হয়ে যায়, 
        // তবে রিকোয়ারমেন্ট অনুযায়ী অল শিপমেন্টস দেখানোর ব্যবস্থা রাখা হলো
        if (this.userRole === 'SUPPLIER' && this.currentSupplierId) {
          this.shipments = allShipments.filter((s: any) => {
            const sId = s.supplierId || (s.supplier ? s.supplier.id : null);
            return sId === this.currentSupplierId;
          });
          // যদি সাপ্লায়ার আইডির সাথে ম্যাচ করে কোনো শিপমেন্ট না পাওয়া যায়, তবে ব্যাকআপ হিসেবে সব দেখাবে বা খালি থাকবে
          if (this.shipments.length === 0 && allShipments.length > 0) {
            this.shipments = allShipments;
          }
        } else {
          this.shipments = allShipments;
        }

        this.filteredShipments = [...this.shipments];
        this.applyDoubleSearch();
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error("Shipment Load Error:", err);
      }
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
    this.poService.findAll().subscribe({ 
      next: (data) => {
        this.purchaseOrders = data || [];
        this.cdr.markForCheck();
      },
      error: (err) => console.error("PO Load Error:", err)
    });
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

    this.shipment.poId = Number(this.shipment.poId);
    this.shipment.supplierId = Number(this.shipment.supplierId);
    this.shipment.transportCost = Number(this.shipment.transportCost);

    if (!this.shipment.poId || this.shipment.poId === 0 || !this.shipment.supplierId || this.shipment.supplierId === 0) {
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
        error: (err) => {
          console.error('Update error:', err);
          this.errorMessage = err.error?.message || err.message || 'Modification matrix deployment failure.';
        },
      });
    } else {
      this.service.save(this.shipment, this.selectedFile).subscribe({
        next: () => {
          alert('New Cargo Dispatch Node authorized and registered.');
          this.closeDrawer();
          this.loadShipments();
        },
        error: (err) => {
          console.error('Save error:', err);
          this.errorMessage = err.error?.message || err.message || 'Logistics initialization exception.';
        },
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

  openPdfModal(s: ShipmentResponseModel) {
    this.selectedShipmentForPdf = s;
    this.isPdfModalOpen = true;
    this.cdr.markForCheck();
  }

  closePdfModal() {
    this.isPdfModalOpen = false;
    this.selectedShipmentForPdf = null;
    this.cdr.markForCheck();
  }

  downloadPdfFromModal() {
    if (!this.selectedShipmentForPdf) return;

    const element = this.pdfPreviewContainer.nativeElement;
    
    html2canvas(element, { scale: 2, useCORS: true, windowHeight: element.scrollHeight, height: element.scrollHeight }).then((canvas) => {
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF('p', 'mm', 'a4');
      const imgWidth = 210; 
      const pageHeight = 295; 
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      let heightLeft = imgHeight;
      let position = 0;

      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      while (heightLeft >= 0) {
        position = heightLeft - imgHeight;
        pdf.addPage();
        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
      }

      pdf.save(`Shipment-${this.selectedShipmentForPdf?.shipmentNumber}.pdf`);
      this.closePdfModal();
    });
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