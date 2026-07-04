import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WarehouseService } from '../../../../service/warehouse.service';
import { CountryService } from '../../../../service/country.service';
import { DivisionService } from '../../../../service/division.service';
import { DistrictService } from '../../../../service/district.service';
import { PoliceStationService } from '../../../../service/police-station.service';
import { WarehouseRequestModel, WarehouseResponseModel } from '../../../shared/model/warehouse';

@Component({
  selector: 'app-warehouse.component',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './warehouse.component.html',
  styleUrl: './warehouse.component.css',
})
export class WarehouseComponent implements OnInit {
  
  warehouses: WarehouseResponseModel[] = [];

  countries: any[] = [];
  divisions: any[] = [];
  districts: any[] = [];
  policeStations: any[] = [];

  selectedCountryId: number | null = null;
  selectedDivisionId: number | null = null;
  selectedDistrictId: number | null = null;

  warehouse: WarehouseRequestModel = {
    name: '',
    email: '',
    location: '', // User text entry: e.g., "Plot-45, Road-12"
    address: '',  // Auto compiled address
    capacity: 0,
    managerId: 1, // Default Mock Manager
    isActive: true,
    policeStationId: 0
  };

  isEdit = false;
  currentEditId: number | null = null;
  isDrawerOpen = false; 

  constructor(
    private service: WarehouseService,
    private countryService: CountryService,
    private divisionService: DivisionService,
    private districtService: DistrictService,
    private stationService: PoliceStationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadWarehouses();
    this.loadCountries();
  }

  openDrawer() {
    this.isDrawerOpen = true;
  }

  closeDrawer() {
    this.isDrawerOpen = false;
    this.reset();
  }

  loadWarehouses() {
    this.service.getAll().subscribe({
      next: (data) => {
        this.warehouses = data;
        this.cdr.markForCheck();
      }
    });
  }

  loadCountries() {
    this.countryService.getAll().subscribe({
      next: (data) => {
        this.countries = data;
        this.cdr.markForCheck();
      }
    });
  }

  // =====================================================
  // CASCADING LOCATION LOGICS 
  // =====================================================

  onCountryChange() {
    this.divisions = [];
    this.districts = [];
    this.policeStations = [];
    this.selectedDivisionId = null;
    this.selectedDistrictId = null;
    this.warehouse.policeStationId = 0;

    if (!this.selectedCountryId) return;

    this.divisionService.getByCountryId(this.selectedCountryId).subscribe(res => {
      this.divisions = res;
      this.cdr.markForCheck();
    });
  }

  onDivisionChange() {
    this.districts = [];
    this.policeStations = [];
    this.selectedDistrictId = null;
    this.warehouse.policeStationId = 0;

    if (!this.selectedDivisionId) return;

    this.districtService.getByDivisionId(this.selectedDivisionId).subscribe(res => {
      this.districts = res;
      this.cdr.markForCheck();
    });
  }

  onDistrictChange() {
    this.policeStations = [];
    this.warehouse.policeStationId = 0;

    if (!this.selectedDistrictId) return;

    this.stationService.getByDistrictId(this.selectedDistrictId).subscribe(res => {
      this.policeStations = res;
      this.cdr.markForCheck();
    });
  }

  // =====================================================
  // AUTO ADDRESS COMPILER
  // =====================================================
  generateFullAddress() {
    const countryName = this.countries.find(x => x.id == this.selectedCountryId)?.name || '';
    const divisionName = this.divisions.find(x => x.id == this.selectedDivisionId)?.name || '';
    const districtName = this.districts.find(x => x.id == this.selectedDistrictId)?.name || '';
    const psName = this.policeStations.find(x => x.id == this.warehouse.policeStationId)?.name || '';

    this.warehouse.address = [
      this.warehouse.location,
      psName,
      districtName,
      divisionName,
      countryName
    ].filter(v => v).join(', ');
  }

  save() {
    if (this.warehouse.policeStationId === 0) {
      alert("Please complete the location hierarchy up to Police Station.");
      return;
    }

    this.generateFullAddress();

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.warehouse).subscribe({
        next: () => {
          alert("Warehouse updated successfully!");
          this.closeDrawer();
          this.loadWarehouses();
        }
      });
    } else {
      this.service.save(this.warehouse).subscribe({
        next: () => {
          alert("Warehouse deployed successfully!");
          this.closeDrawer();
          this.loadWarehouses();
        }
      });
    }
  }

  edit(w: WarehouseResponseModel) {
    this.currentEditId = w.id;
    this.isEdit = true;

    this.warehouse = {
      name: w.name,
      email: w.email,
      location: w.location,
      address: w.address,
      capacity: w.capacity,
      managerId: w.managerId,
      isActive: w.isActive,
      policeStationId: w.policeStationId
    };

    
    this.openDrawer();
  }

  delete(id: number) {
    if (confirm("Purge this warehouse node?")) {
      this.service.delete(id).subscribe({
        next: () => {
          alert("Warehouse deleted successfully");
          this.loadWarehouses();
        }
      });
    }
  }

  reset() {
    this.warehouse = {
      name: '',
      email: '',
      location: '',
      address: '',
      capacity: 0,
      managerId: 0,
      isActive: true,
      policeStationId: 0
    };
    this.selectedCountryId = null;
    this.selectedDivisionId = null;
    this.selectedDistrictId = null;
    this.divisions = [];
    this.districts = [];
    this.policeStations = [];
    this.isEdit = false;
    this.currentEditId = null;
  }
}