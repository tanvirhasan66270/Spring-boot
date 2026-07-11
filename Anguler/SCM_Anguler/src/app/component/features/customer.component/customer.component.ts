import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../../service/customer.service';
import { CountryService } from '../../../service/country.service';
import { DivisionService } from '../../../service/division.service';
import { DistrictService } from '../../../service/district.service';
import { PoliceStationService } from '../../../service/police-station.service';
import { environment } from '../../../../environment/environment';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css',
})
export class CustomerComponent implements OnInit {

  customers: any[] = [];

  countries: any[] = [];
  divisions: any[] = [];
  districts: any[] = [];
  policeStations: any[] = [];

  selectedCountryId: number | null = null;
  selectedDivisionId: number | null = null;
  selectedDistrictId: number | null = null;

  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;
  streetAddress: string = '';
  confirmPassword = '';

  readonly imageBaseUrl = environment.apiUrl+"customer/";




  customer: any = {
    name: '',
    email: '',
    phone: '',
    password: '',
    address: '',
    gender: '',
    dob: '',
    nidNumber: '',
    image: '',
    policeStationId: 0
  };

  isEdit = false;
  currentEditId: number | null = null;
  isDrawerOpen = false;

  constructor(
    private service: CustomerService,
    private countryService: CountryService,
    private divisionService: DivisionService,
    private districtService: DistrictService,
    private stationService: PoliceStationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadCustomers();
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

  loadCustomers() {
    this.service.getAll().subscribe({
      next: (data) => {
        this.customers = data || [];
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
  this.customer.policeStationId = 0;

  if (!this.selectedCountryId) {
    this.generateFullAddress();
    return;
  }

  this.divisionService.getByCountryId(this.selectedCountryId).subscribe(res => {
    this.divisions = res || [];
    this.generateFullAddress(); 
    this.cdr.markForCheck();
  });
}

onDivisionChange() {
  this.districts = [];
  this.policeStations = [];
  this.selectedDistrictId = null;
  this.customer.policeStationId = 0;

  if (!this.selectedDivisionId) {
    this.generateFullAddress();
    return;
  }

  this.districtService.getByDivisionId(this.selectedDivisionId).subscribe(res => {
    this.districts = res || [];
    this.generateFullAddress();
    this.cdr.markForCheck();
  });
}

onDistrictChange() {
  this.policeStations = [];
  this.customer.policeStationId = 0;

  if (!this.selectedDistrictId) {
    this.generateFullAddress();
    return;
  }

  this.stationService.getByDistrictId(this.selectedDistrictId).subscribe(res => {
    this.policeStations = res || [];
    this.generateFullAddress();
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
    const psName = this.policeStations.find(x => x.id == this.customer.policeStationId)?.name || '';

    this.customer.address = [
      this.streetAddress,
      psName,
      districtName,
      divisionName,
      countryName
    ].filter(v => v).join(', ');
  }

  // =====================================================
  // FILE CAPTURE HANDLER
  // =====================================================
  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (!file) return;

    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
      this.cdr.markForCheck();
    };
    reader.readAsDataURL(file);
  }

  removeSelectedFile(fileInput: HTMLInputElement) {
    this.selectedFile = null;
    this.imagePreview = null;
    fileInput.value = '';
    this.cdr.markForCheck();
  }

  isImageAvailable(imageName: string): boolean {
    return !!imageName && imageName.trim().length > 0;
  }

  getImageUrl(imageName: string | null | undefined): string {
    return imageName ? `${this.imageBaseUrl}${imageName}` : '';
  }

  onImageError(event: Event): void {
    const target = event.target as HTMLImageElement | null;
    if (target) {
      target.style.display = 'none';
    }
  }

  // =====================================================
  // CRUD CORE METHODS
  // =====================================================
  save() {
    if (!this.isEdit && this.customer.password !== this.confirmPassword) {
      alert('Password and confirm password do not match.');
      return;
    }

    if (this.customer.policeStationId === 0) {
      alert("Please complete the location hierarchy up to Police Station.");
      return;
    }

    this.generateFullAddress();

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.customer, this.selectedFile).subscribe({
        next: () => {
          alert("Customer profile updated successfully!");
          this.closeDrawer();
          this.loadCustomers();
        },
        error: (err) => {
          console.error('Customer update failed:', err);
          alert('Customer update failed. Please check the server response.');
        }
      });
    } else {
      this.service.save(this.customer, this.selectedFile).subscribe({
        next: () => {
          alert("Customer registered successfully!");
          this.closeDrawer();
          this.loadCustomers();
        },
        error: (err) => {
          console.error('Customer save failed:', err);
          alert('Customer save failed. Please check the server response.');
        }
      });
    }
  }

  edit(c: any) {
  this.currentEditId = c.id;
  this.isEdit = true;

  this.customer = {
    name: c.name,
    email: c.email,
    phone: c.phone,
    password: '', 
    address: c.address,
    gender: c.gender,
    dob: c.dob,
    nidNumber: c.nidNumber,
    image: c.image,
    policeStationId: c.policeStationId
  };

  // অ্যাড্রেস স্প্লিট করার সময় স্পেসসহ সেফ স্প্লিট
  const addressParts = c.address ? c.address.split(', ') : [];
  this.streetAddress = addressParts[0] || '';
  
  // হার্ডকোডেড URL ফিক্স
  this.imagePreview = c.image ? this.getImageUrl(c.image) : null; 

  // আইডি ম্যাপিং
  this.selectedCountryId = (c as any).countryId ? +(c as any).countryId : null;
  this.selectedDivisionId = (c as any).divisionId ? +(c as any).divisionId : null;
  this.selectedDistrictId = (c as any).districtId ? +(c as any).districtId : null;
  this.customer.policeStationId = c.policeStationId ? +c.policeStationId : 0;

  // এডিট মোডে ড্রপডাউনগুলো ক্রমান্বয়ে লোড করা
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
    if (confirm("Purge this customer record definitively?")) {
      this.service.delete(id).subscribe({
        next: () => {
          alert("Customer record successfully purged.");
          this.loadCustomers();
        }
      });
    }
  }

  reset() {
    this.customer = {
      name: '',
      email: '',
      phone: '',
      password: '',
      address: '',
      gender: '',
      dob: '',
      nidNumber: '',
      image: '',
      policeStationId: 0
    };
    this.selectedCountryId = null;
    this.selectedDivisionId = null;
    this.selectedDistrictId = null;
    this.divisions = [];
    this.districts = [];
    this.policeStations = [];
    this.selectedFile = null;
    this.imagePreview = null;
    this.streetAddress = '';
    this.confirmPassword = '';
    this.isEdit = false;
    this.currentEditId = null;
  }
}
