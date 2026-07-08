import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LetterOfCreditRequestModel, LetterOfCreditResponseModel } from '../../shared/model/letterOfCraditModel';
import { LetterOfCreditService } from '../../../service/letterofcradit.service';
import { SupplierService } from '../../../service/supplier.service';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';

@Component({
  selector: 'app-letter-of-credit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './letterofcradit.component.html',
  styleUrl: './letterofcradit.component.css'
})
export class LetterOfCreditComponent implements OnInit {

  lcs: LetterOfCreditResponseModel[] = [];
  purchaseOrders: any[] = [];
  suppliers: any[] = [];
  banks: any[] = []; // এসাইন করা ব্যাংক ডিরেক্টরি লিস্ট (ধরে নেওয়া হয়েছে ব্যাংক সার্ভিস আলাদা আছে)

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  isAmendMode = false; // Amendment পপআপ ট্র্যাকার
  currentEditId: number | null = null;
  selectedFile: File | null = null;

  lc: LetterOfCreditRequestModel = {
    purchaseOrderId: 0,
    issuingBankId: 0,
    shipmentIncoTerms: 'FOB',
    latestShipmentDate: '',
    portOfLoading: '',
    portOfDischarge: '',
    amount: 0,
    supplierId: 0,
    currency: 'USD',
    expiryDate: '',
    lcStatus: 'DRAFT',
    documentVaultUrl: ''
  };

  constructor(
    private service: LetterOfCreditService,
    private poService: PurchaseOrderService,
    private supplierService: SupplierService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadLCs();
    this.loadPurchaseOrders();
    this.loadSuppliers();
    this.mockBanks(); 
  }

  loadLCs() {
    this.service.findAll().subscribe({ next: (res) => { this.lcs = res || []; this.cdr.markForCheck(); } });
  }

  loadPurchaseOrders() {
    this.poService.findAll().subscribe({ next: (res) => this.purchaseOrders = res || [] });
  }

  loadSuppliers() {
    this.supplierService.findAll().subscribe({ next: (res) => this.suppliers = res || [] });
  }

  mockBanks() {
    this.banks = [
      { id: 1, name: 'Standard Chartered Bank', swiftCode: 'SCBLBDDHXXX', branchName: 'Gulban' },
      { id: 2, name: 'HSBC Bangladesh', swiftCode: 'HSBCBDDHXXX', branchName: 'Motijheel' },
      { id: 3, name: 'Brac Bank PLC', swiftCode: 'BRACBDDHXXX', branchName: 'Dhaka Head Office' }
    ];
  }

  onFileChange(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  openDrawer() { this.reset(); this.isEdit = false; this.isAmendMode = false; this.isDrawerOpen = true; this.cdr.markForCheck(); }
  closeDrawer() { this.isDrawerOpen = false; this.reset(); this.cdr.markForCheck(); }

 save() {
    this.errorMessage = null;

    if (!this.lc.purchaseOrderId || this.lc.purchaseOrderId === 0 ||
        !this.lc.supplierId || this.lc.supplierId === 0 ||
        !this.lc.issuingBankId || this.lc.issuingBankId === 0) {
      this.errorMessage = "Validation Fault: Please select valid Purchase Order, Supplier, and Issuing Bank.";
      this.cdr.markForCheck();
      return;
    }

    if (!this.lc.latestShipmentDate || !this.lc.expiryDate) {
      this.errorMessage = "Validation Fault: Latest Shipment Date and Expiry Date are mandatory parameters.";
      this.cdr.markForCheck();
      return;
    }

    const cleanedPayload: LetterOfCreditRequestModel = {
      ...this.lc,
      lcStatus: this.lc.lcStatus ? this.lc.lcStatus.toUpperCase() : 'DRAFT',
      // যদি আইডি স্ট্রিং হিসেবে থাকে তবে সেটিকে নাম্বারে কাস্ট করা
      purchaseOrderId: +this.lc.purchaseOrderId,
      supplierId: +this.lc.supplierId,
      issuingBankId: +this.lc.issuingBankId
    };

    if (this.isAmendMode && this.currentEditId !== null) {
      const patchData = {
        amount: cleanedPayload.amount,
        expiryDate: cleanedPayload.expiryDate,
        latestShipmentDate: cleanedPayload.latestShipmentDate
      };
      this.service.amendLC(this.currentEditId, patchData).subscribe({
        next: () => { alert("Commercial LC Amendment applied."); this.closeDrawer(); this.loadLCs(); },
        error: (err) => this.handleErrorLog(err)
      });
    } else if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, cleanedPayload, this.selectedFile).subscribe({
        next: () => { alert("Letter of Credit updated successfully."); this.closeDrawer(); this.loadLCs(); },
        error: (err) => this.handleErrorLog(err)
      });
    } else {
      this.service.save(cleanedPayload, this.selectedFile).subscribe({
        next: () => { alert("New Letter of Credit registered successfully."); this.closeDrawer(); this.loadLCs(); },
        error: (err) => this.handleErrorLog(err)
      });
    }
  }

  // কাস্টম এরর হ্যান্ডলার মেথড
  private handleErrorLog(err: any) {
    console.error("Backend Error Object:", err);
    this.errorMessage = err.error?.message || err.message || "400 Bad Request: Structural payload mismatch.";
    this.cdr.markForCheck();
  }
  edit(o: LetterOfCreditResponseModel) {
    this.errorMessage = null;
    this.currentEditId = o.id;
    this.isEdit = true;
    this.isAmendMode = false;
    this.lc = {
      purchaseOrderId: o.purchaseOrderId,
      issuingBankId: o.issuingBankId,
      shipmentIncoTerms: o.shipmentIncoTerms || 'FOB',
      latestShipmentDate: o.latestShipmentDate,
      portOfLoading: o.portOfLoading,
      portOfDischarge: o.portOfDischarge,
      amount: o.amount,
      supplierId: o.supplierId,
      currency: o.currency || 'USD',
      expiryDate: o.expiryDate,
      lcStatus: o.lcStatus,
      documentVaultUrl: o.documentVaultUrl || ''
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  openAmendWindow(o: LetterOfCreditResponseModel) {
    this.edit(o);
    this.isAmendMode = true; // শুধু অ্যামাউন্ট এবং ডেট ইনপুট ভিজিবল ফিল্ড করার ট্র্যাকার
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Definitively purge this Letter of Credit mapping from the enterprise logistics grid?")) {
      this.service.delete(id).subscribe({
        next: () => { alert("LC entry cleanly wiped."); this.loadLCs(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  reset() {
    this.lc = {
      purchaseOrderId: 0,
      issuingBankId: 0,
      shipmentIncoTerms: 'FOB',
      latestShipmentDate: '',
      portOfLoading: '',
      portOfDischarge: '',
      amount: 0,
      supplierId: 0,
      currency: 'USD',
      expiryDate: '',
      lcStatus: 'DRAFT',
      documentVaultUrl: ''
    };
    this.selectedFile = null;
    this.isEdit = false;
    this.isAmendMode = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}