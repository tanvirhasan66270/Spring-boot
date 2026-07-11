import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DeliveryTripRequestModel, DeliveryTripResponseModel } from '../../shared/model/DeliveryTripModel';
import { DeliveryTripService } from '../../../service/delivery-trip.service';
import { VehicleService } from '../../../service/vehicle.service'; // ভেহিকল ডাটা ফেচ করার জন্য

@Component({
  selector: 'app-delivery-trip',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './delivery-trip.component.html',
  styleUrl: './delivery-trip.component.css'
})
export class DeliveryTripComponent implements OnInit {

  trips: DeliveryTripResponseModel[] = [];
  customers: any[] = [];
  
  // ── ফ্লিট ডাটা মেমোরি ──
  allVehicles: any[] = [];      
  filteredVehicles: any[] = [];  
  
  selectedVehicleType: string = ''; 

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isStatusModalOpen = false;
  isEdit = false;
  currentTripId: number | null = null;

  selectedSignature: File | null = null;
  selectedPhoto: File | null = null;
  statusUpdateValue = 'PENDING';

  formModel: DeliveryTripRequestModel = {
    dispatcherId: 1, 
    customerId: 0,
    vehicleId: 0,
    status: 'PENDING',
    customerAddress: '',
    remarks: ''
  };

  constructor(
    private service: DeliveryTripService,
    private vehicleService: VehicleService, // ইনজেক্টেড ওল্ড সার্ভিস
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadTrips();
    this.loadActiveFleetData();
  }

  loadTrips() {
    this.service.findAll().subscribe({
      next: (data) => { this.trips = data || []; this.cdr.markForCheck(); },
      error: (err) => this.handleError(err)
    });
  }

  loadActiveFleetData() {
    // রিয়েল ভেহিকল সার্ভিস থেকে ডাটা ফেচ করা (যার ভেতর ড্রাইভার ফ্ল্যাটেনড মেটাডাটা আছে)
    this.vehicleService.findAll().subscribe({
      next: (data) => {
        this.allVehicles = data || [];
        this.cdr.markForCheck();
      },
      error: () => this.mockDropdownFeeds() // ব্যাকএন্ড অফ থাকলে মক ডাটা ফলব্যাক হবে
    });
    
    // কাস্টমার ফিড লোড (আপনার এক্সিস্টিং কাস্টমার সার্ভিস কল এখানে হবে)
    this.customers = [{ id: 1, name: "Tanvir Rahman" }];
  }

  onVehicleTypeChange() {
    this.formModel.vehicleId = 0; // ওল্ড সিলেকশন রিসেট
    
    if (!this.selectedVehicleType) {
      this.filteredVehicles = [];
    } else {
      // ব্যাকএন্ড থেকে আসা লিস্ট থেকে শুধুমাত্র সিলেক্টেড টাইপ (যেমন: TRUCK) এবং অ্যাক্টিভ ড্রাইভার আছে এমন গাড়ি ফিল্টার হবে
      this.filteredVehicles = this.allVehicles.filter(v => 
        v.type.toUpperCase() === this.selectedVehicleType.toUpperCase() && v.driverId != null
      );
    }
    this.cdr.markForCheck();
  }

  submitTrip() {
    this.errorMessage = null;

    if (!this.formModel.customerId || +this.formModel.customerId === 0 ||
        !this.formModel.vehicleId || +this.formModel.vehicleId === 0) {
      this.errorMessage = "Validation Fault: Customer mapping and Fleet allocation with assigned captain are required.";
      this.cdr.markForCheck();
      return;
    }

    const payload: DeliveryTripRequestModel = {
      dispatcherId: this.formModel.dispatcherId ? +this.formModel.dispatcherId : 1,
      customerId: +this.formModel.customerId,
      vehicleId: +this.formModel.vehicleId,
      status: this.formModel.status.toUpperCase(), 
      customerAddress: this.formModel.customerAddress.trim(),
      remarks: this.formModel.remarks?.trim() || null
    };

    if (this.isEdit && this.currentTripId) {
      this.service.update(this.currentTripId, payload).subscribe({
        next: () => { alert("Trip manifest routing modified."); this.closeDrawer(); this.loadTrips(); },
        error: (err) => this.handleError(err)
      });
    } else {
      this.service.create(payload).subscribe({
        next: () => { alert("New delivery trip blueprint deployed successfully."); this.closeDrawer(); this.loadTrips(); },
        error: (err) => this.handleError(err)
      });
    }
  }

  openStatusModal(trip: DeliveryTripResponseModel) {
    this.currentTripId = trip.id;
    this.statusUpdateValue = trip.status;
    this.selectedSignature = null;
    this.selectedPhoto = null;
    this.isStatusModalOpen = true;
    this.cdr.markForCheck();
  }

  closeStatusModal() { this.isStatusModalOpen = false; this.currentTripId = null; }

  onFileChange(event: any, type: 'sig' | 'photo') {
    const file = event.target.files[0];
    if (file) {
      if (type === 'sig') this.selectedSignature = file;
      if (type === 'photo') this.selectedPhoto = file;
    }
  }

  executeStatusPatch() {
    if (!this.currentTripId) return;

    this.service.changeStatus(this.currentTripId, this.statusUpdateValue.toUpperCase(), this.selectedSignature, this.selectedPhoto).subscribe({
      next: () => {
        alert("Trip execution console state patched successfully.");
        this.closeStatusModal();
        this.loadTrips();
      },
      error: (err) => this.handleError(err)
    });
  }

  edit(trip: DeliveryTripResponseModel) {
    this.isEdit = true;
    this.currentTripId = trip.id;
    
    // এডিট করার সময় ওল্ড ভেহিকল টাইপ ডিটেক্ট করা
    const matchedVehicle = this.allVehicles.find(v => v.id === trip.vehicleId);
    if (matchedVehicle) {
      this.selectedVehicleType = matchedVehicle.type;
      this.onVehicleTypeChange();
    }

    this.formModel = {
      dispatcherId: trip.dispatcherId,
      customerId: trip.customerId,
      vehicleId: trip.vehicleId,
      status: trip.status,
      customerAddress: trip.customerAddress,
      remarks: trip.remarks
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  deleteTrip(id: number) {
    if (confirm("Are you sure you want to terminate this delivery manifest pointer from matrix?")) {
      this.service.delete(id).subscribe({
        next: (msg) => { alert(msg); this.loadTrips(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  openDrawer() { this.resetForm(); this.isDrawerOpen = true; }
  closeDrawer() { this.isDrawerOpen = false; this.resetForm(); }

  resetForm() {
    this.formModel = { dispatcherId: 1, customerId: 0, vehicleId: 0, status: 'PENDING', customerAddress: '', remarks: '' };
    this.selectedVehicleType = '';
    this.filteredVehicles = [];
    this.isEdit = false;
    this.currentTripId = null;
    this.errorMessage = null;
  }

  private handleError(err: any) {
    this.errorMessage = err.error?.message || err.message || "Runtime Communication Exception.";
    this.cdr.markForCheck();
  }

  private mockDropdownFeeds() {
    // ব্যাকএন্ড সার্ভিস ফেসিং এরর হলে রিলেশন প্রটেক্টেড ডামি ডাটা আর্কিটেকচার
    this.allVehicles = [
      { id: 1, plateNumber: "Dhaka Metro-GA-11-2026", type: "TRUCK", driverId: 10, driverName: "Captain Rafiq" },
      { id: 2, plateNumber: "Dhaka Metro-HA-22-9981", type: "TRUCK", driverId: 11, driverName: "Captain Asif" },
      { id: 3, plateNumber: "Dhaka Metro-THA-55-1024", type: "VAN", driverId: 12, driverName: "Captain Karim" },
      { id: 4, plateNumber: "Dhaka Metro-MOTO-88-1122", type: "BIKE", driverId: 13, driverName: "Rider Sohel" }
    ];
    this.cdr.markForCheck();
  }
}
