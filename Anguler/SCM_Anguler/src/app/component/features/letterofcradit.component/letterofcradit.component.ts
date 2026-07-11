import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LetterOfCreditRequestModel, LetterOfCreditResponseModel } from '../../shared/model/letterOfCraditModel';
import { LetterOfCreditService } from '../../../service/letterofcradit.service';
import { SupplierService } from '../../../service/supplier.service';
import { PurchaseOrderService } from '../../../service/purchase-orde.service';
import { LcbankService } from '../../../service/lcbank.service'; // 🎯 রিয়েল ব্যাংক সার্ভিস সিঙ্ক

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
  banks: any[] = []; 

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  isAmendMode = false;
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
    private bankService: LcbankService, // 🎯 ইনজেক্ট করা হলো
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadLCs();
    this.loadPurchaseOrders();
    this.loadSuppliers();
    this.loadRealBanks(); // 🎯 মকিং বাদ দিয়ে রিয়েল ডাটা লোড
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

  loadRealBanks() {
    // 🏦 আপনার তৈরি করা /api/banks এপিআই থেকে ডাটা সরাসরি লোড হবে
    this.bankService.findAll().subscribe({
      next: (res) => { this.banks = res || []; this.cdr.markForCheck(); },
      error: () => { this.banks = []; }
    });
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

    // 🎯 ১. ড্রপডাউন আইডি ভ্যালিডেশন চেক (Strict Type Cast)
    if (!this.lc.purchaseOrderId || +this.lc.purchaseOrderId === 0 ||
        !this.lc.supplierId || +this.lc.supplierId === 0 ||
        !this.lc.issuingBankId || +this.lc.issuingBankId === 0) {
      this.errorMessage = "Validation Fault: Please select valid Purchase Order, Supplier, and Issuing Bank from options.";
      this.cdr.markForCheck();
      return;
    }

    // 🎯 ২. ক্রনিকাল ডেট চেক
    if (!this.lc.latestShipmentDate || !this.lc.expiryDate) {
      this.errorMessage = "Validation Fault: Latest Shipment Date and Expiry Date are mandatory parameters.";
      this.cdr.markForCheck();
      return;
    }

    // 🎯 ৩. ডাটা স্যানিটাইজেশন এবং পেলোড প্রিপারেশন
    const cleanedPayload: LetterOfCreditRequestModel = {
      ...this.lc,
      lcStatus: this.lc.lcStatus ? this.lc.lcStatus.toUpperCase() : 'DRAFT',
      purchaseOrderId: +this.lc.purchaseOrderId,
      supplierId: +this.lc.supplierId,
      issuingBankId: +this.lc.issuingBankId
    };

    if (this.isAmendMode && this.currentEditId !== null) {
      // 🚀 PATCH: অ্যামেন্ডমেন্ট ট্রানজেকশন
      const patchData = {
        amount: cleanedPayload.amount,
        expiryDate: cleanedPayload.expiryDate,
        latestShipmentDate: cleanedPayload.latestShipmentDate
      };
      this.service.amendLC(this.currentEditId, patchData).subscribe({
        next: () => { alert("Commercial LC Amendment applied successfully."); this.closeDrawer(); this.loadLCs(); },
        error: (err) => this.handleErrorLog(err)
      });
    } else if (this.isEdit && this.currentEditId !== null) {
      // 📝 PUT: মেটাডাটা আপডেট
      this.service.update(this.currentEditId, cleanedPayload, this.selectedFile).subscribe({
        next: () => { alert("Letter of Credit updated successfully."); this.closeDrawer(); this.loadLCs(); },
        error: (err) => this.handleErrorLog(err)
      });
    } else {
      // ➕ POST: নতুন এলসি রেজিস্টার
      this.service.save(cleanedPayload, this.selectedFile).subscribe({
        next: () => { alert("New Letter of Credit registered successfully into cluster registry."); this.closeDrawer(); this.loadLCs(); },
        error: (err) => this.handleErrorLog(err)
      });
    }
  }

  private handleErrorLog(err: any) {
    console.error("Core Backend Tracked Error:", err);
    this.errorMessage = err.error?.message || err.message || "400 Bad Request: Structural mapping mismatch.";
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
    this.isAmendMode = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Definitively purge this Letter of Credit mapping from the enterprise logistics grid?")) {
      this.service.delete(id).subscribe({
        next: () => { alert("LC entry cleanly wiped from server."); this.loadLCs(); },
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
