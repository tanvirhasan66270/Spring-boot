import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DeliveryTripRequestModel, DeliveryTripResponseModel } from '../../shared/model/DeliveryTripModel';
import { DeliveryTripService } from '../../../service/delivery-trip.service';

@Component({
  selector: 'app-delivery-trip',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './delivery-trip.component.html',
  styleUrl: './delivery-trip.component.css'
})
export class DeliveryTripComponent implements OnInit {

  trips: DeliveryTripResponseModel[] = [];
  customers: any[] = []; // dropdown feeds
  drivers: any[] = [];   // dropdown feeds
  vehicles: any[] = [];  // dropdown feeds

  errorMessage: string | null = null;
  isDrawerOpen = false;
  isStatusModalOpen = false;
  isEdit = false;
  currentTripId: number | null = null;

  // ফাইল হ্যান্ডেলিং মেমোরি স্টেট
  selectedSignature: File | null = null;
  selectedPhoto: File | null = null;
  statusUpdateValue = 'PENDING';

  formModel: DeliveryTripRequestModel = {
    dispatcherId: 1, 
    customerId: 0,
    driverId: 0,
    vehicleId: 0,
    status: 'PENDING',
    customerAddress: '',
    remarks: ''
  };

  constructor(
    private service: DeliveryTripService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadTrips();
    this.mockDropdownFeeds(); 
  }

  loadTrips() {
    this.service.findAll().subscribe({
      next: (data) => { this.trips = data || []; this.cdr.markForCheck(); },
      error: (err) => this.handleError(err)
    });
  }

  submitTrip() {
    this.errorMessage = null;

    // ড্রপডাউন ভ্যালিডেশন গার্ড
    if (!this.formModel.customerId || +this.formModel.customerId === 0 ||
        !this.formModel.driverId || +this.formModel.driverId === 0 ||
        !this.formModel.vehicleId || +this.formModel.vehicleId === 0) {
      this.errorMessage = "Validation Fault: Customer mapping, Fleet allocation and Captain configuration are required.";
      this.cdr.markForCheck();
      return;
    }

    // 🎯 ফিক্স ২: status.toUpperCase() নিশ্চিত করা হলো এবং dispatcherId সেফ কাস্টিং করা হলো
    const payload: DeliveryTripRequestModel = {
      dispatcherId: this.formModel.dispatcherId ? +this.formModel.dispatcherId : 1,
      customerId: +this.formModel.customerId,
      driverId: +this.formModel.driverId,
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
    this.formModel = {
      dispatcherId: trip.dispatcherId,
      customerId: trip.customerId,
      driverId: trip.driverId,
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
    this.formModel = { 
      dispatcherId: 1, 
      customerId: 0, 
      driverId: 0, 
      vehicleId: 0, 
      status: 'PENDING', 
      customerAddress: '', 
      remarks: '' 
    };
    this.isEdit = false;
    this.currentTripId = null;
    this.errorMessage = null;
  }

  private handleError(err: any) {
    this.errorMessage = err.error?.message || err.message || "Runtime Communication Exception.";
    this.cdr.markForCheck();
  }

  private mockDropdownFeeds() {
    this.customers = [{ id: 1, name: "Tanvir Rahman" }];
    this.drivers = [{ id: 1, driverName: "Captain Rafiq" }];
    this.vehicles = [{ id: 1, plateNumber: "Dhaka Metro-GA-11-2026" }];
  }
}