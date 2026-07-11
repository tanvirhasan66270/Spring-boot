import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WarehouseService } from '../../../../service/warehouse.service';
import { CountryService } from '../../../../service/country.service';
import { DivisionService } from '../../../../service/division.service';
import { DistrictService } from '../../../../service/district.service';
import { PoliceStationService } from '../../../../service/police-station.service';
import { WarehouseRequestModel, WarehouseResponseModel } from '../../../shared/model/warehouse';
import { environment } from '../../../../../environment/environment';

@Component({
  selector: 'app-warehouse',
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
    location: '', 
    address: '',  
    capacity: 0,
    managerId: 1, 
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

  loadWarehouses() {
    this.service.getAll().subscribe({
      next: (data) => {
        this.warehouses = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  loadCountries() {
    this.countryService.getAll().subscribe({
      next: (data) => {
        this.countries = data || [];
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
      this.divisions = res || [];
      this.cdr.markForCheck();
      console.log(this.divisions);
    });
  }

  onDivisionChange() {
    this.districts = [];
    this.policeStations = [];
    this.selectedDistrictId = null;
    this.warehouse.policeStationId = 0;

    const divisionId = Number(this.selectedDivisionId);
    if (!divisionId) return;

    this.districtService.getByDivisionId(divisionId).subscribe({
      next: (res) => {
        this.districts = res || [];
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load districts:', err);
        this.districts = [];
        this.cdr.markForCheck();
      }
    });
  }

  onDistrictChange() {
    this.policeStations = [];
    this.warehouse.policeStationId = 0;

    const districtId = Number(this.selectedDistrictId);
    if (!districtId) return;

    this.stationService.getByDistrictId(districtId).subscribe({
      next: (res) => {
        this.policeStations = res || [];
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load police stations:', err);
        this.policeStations = [];
        this.cdr.markForCheck();
      }
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
    if (!this.warehouse.policeStationId || this.warehouse.policeStationId === 0) {
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

    this.selectedCountryId = (w as any).countryId || null;
    this.selectedDivisionId = (w as any).divisionId || null;
    this.selectedDistrictId = (w as any).districtId || null;

    if (this.selectedCountryId) {
      this.divisionService.getByCountryId(this.selectedCountryId).subscribe(res => {
        this.divisions = res || [];
        if (this.selectedDivisionId) {
          this.districtService.getByDivisionId(this.selectedDivisionId).subscribe(res2 => {
            this.districts = res2 || [];
            if (this.selectedDistrictId) {
              this.stationService.getByDistrictId(this.selectedDistrictId).subscribe(res3 => {
                this.policeStations = res3 || [];
                this.cdr.markForCheck();
              });
            }
          });
        }
      });
    }

    this.isDrawerOpen = true;
    this.cdr.markForCheck();
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
      managerId: 1,
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
