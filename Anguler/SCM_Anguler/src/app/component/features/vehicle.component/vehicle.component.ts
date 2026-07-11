import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VehicleRequestModel, VehicleResponseModel } from '../../shared/model/vehicleModel';
import { VehicleService } from '../../../service/vehicle.service';
import { DriverService } from '../../../service/driver.service';

@Component({
  selector: 'app-vehicle',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './vehicle.component.html',
  styleUrl: './vehicle.component.css'
})
export class VehicleComponent implements OnInit {

  vehicles: VehicleResponseModel[] = [];
  drivers: any[] = []; // ড্রপডাউন ফিডের জন্য
  
  errorMessage: string | null = null;
  isDrawerOpen = false;
  isEdit = false;
  currentVehicleId: number | null = null;

  // ── 🎯 নতুন স্ট্যাটাস চেঞ্জার মোডাল স্টেট ──
  isStatusModalOpen = false;
  statusUpdateValue = 'AVAILABLE';

  formModel: VehicleRequestModel = {
    plateNumber: '',
    type: 'VAN',
    capacity: 0,
    status: 'AVAILABLE', // ফর্মে এটি ফিক্সড থাকবে
    lastServiceDate: '',
    fuelLevel: 100,
    driverId: null
  };

  constructor(
    private service: VehicleService,
    private driverService: DriverService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadVehicles();
    this.loadDrivers();
  }

  loadVehicles() {
    this.service.findAll().subscribe({
      next: (data) => { this.vehicles = data || []; this.cdr.markForCheck(); },
      error: (err) => this.handleError(err)
    });
  }

  loadDrivers() {
    this.driverService.findAll().subscribe({ next: (data) => this.drivers = data || [] });
  }

  submitVehicle() {
    this.errorMessage = null;

    if (!this.formModel.plateNumber || this.formModel.plateNumber.trim() === '') {
      this.errorMessage = "Validation Error: Vehicle Plate Number is mandatory.";
      this.cdr.markForCheck();
      return;
    }

    const payload: VehicleRequestModel = {
      plateNumber: this.formModel.plateNumber.trim(),
      type: this.formModel.type,
      capacity: +this.formModel.capacity,
      status: this.formModel.status, // ফর্মের কারেন্ট ফিক্সড স্টেট নিয়ে সাবমিট হবে
      lastServiceDate: this.formModel.lastServiceDate || null,
      fuelLevel: +this.formModel.fuelLevel,
      driverId: this.formModel.driverId ? +this.formModel.driverId : null
    };

    if (this.isEdit && this.currentVehicleId) {
      this.service.update(this.currentVehicleId, payload).subscribe({
        next: () => { alert("Fleet vehicle parameters updated successfully."); this.closeDrawer(); this.loadVehicles(); },
        error: (err) => this.handleError(err)
      });
    } else {
      this.service.create(payload).subscribe({
        next: () => { alert("New vehicle asset logged into logistics database."); this.closeDrawer(); this.loadVehicles(); },
        error: (err) => this.handleError(err)
      });
    }
  }

  // ── 🎯 টেবিল রো থেকে স্ট্যাটাস মোডাল ট্রিগার করার মাস্টার মেথড ──
  openStatusModal(vehicle: VehicleResponseModel) {
    this.currentVehicleId = vehicle.id;
    this.statusUpdateValue = vehicle.status;
    this.isStatusModalOpen = true;
    this.cdr.markForCheck();
  }

  closeStatusModal() {
    this.isStatusModalOpen = false;
    this.currentVehicleId = null;
  }

  // ── 🎯 ডাটাবেজ কুইক প্যাচ এক্সিকিউশন লজিক ──
  executeStatusUpdate() {
    if (!this.currentVehicleId) return;

    // গাড়ির এক্সিস্টিং ডাটা অবজেক্ট খুঁজে বের করা
    const targetVehicle = this.vehicles.find(v => v.id === this.currentVehicleId);
    if (!targetVehicle) return;

    // ভেহিকল রিকোয়েস্ট মডেলের সাথে ১০০% সিঙ্কড পেলোড জেনারেশন
    const patchPayload: VehicleRequestModel = {
      plateNumber: targetVehicle.plateNumber,
      type: targetVehicle.type,
      capacity: targetVehicle.capacity,
      status: this.statusUpdateValue.toUpperCase(), // 💡 মোডাল থেকে সিলেক্টেড নতুন স্ট্যাটাস
      lastServiceDate: targetVehicle.lastServiceDate,
      fuelLevel: targetVehicle.fuelLevel,
      driverId: targetVehicle.driverId
    };

    this.service.update(this.currentVehicleId, patchPayload).subscribe({
      next: () => {
        alert("Vehicle workshop maintenance status patched successfully.");
        this.closeStatusModal();
        this.loadVehicles();
      },
      error: (err) => this.handleError(err)
    });
  }

  edit(vehicle: VehicleResponseModel) {
    this.isEdit = true;
    this.currentVehicleId = vehicle.id;
    this.formModel = {
      plateNumber: vehicle.plateNumber,
      type: vehicle.type,
      capacity: vehicle.capacity,
      status: vehicle.status, // ওল্ড ডাটা লোড হবে কিন্তু ইউআই-তে রিডঅনলি থাকবে
      lastServiceDate: vehicle.lastServiceDate,
      fuelLevel: vehicle.fuelLevel,
      driverId: vehicle.driverId
    };
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  deleteVehicle(id: number) {
    if (confirm("Are you sure you want to decommission this vehicle asset?")) {
      this.service.delete(id).subscribe({
        next: (msg) => { alert(msg); this.loadVehicles(); },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  openDrawer() { this.resetForm(); this.isDrawerOpen = true; }
  closeDrawer() { this.isDrawerOpen = false; this.resetForm(); }

  resetForm() {
    this.formModel = { plateNumber: '', type: 'VAN', capacity: 0, status: 'AVAILABLE', lastServiceDate: '', fuelLevel: 100, driverId: null };
    this.isEdit = false;
    this.currentVehicleId = null;
    this.errorMessage = null;
  }

  private handleError(err: any) {
    this.errorMessage = err.error?.message || err.message || "Runtime Fleet Communication Interruption.";
    this.cdr.markForCheck();
  }
}
