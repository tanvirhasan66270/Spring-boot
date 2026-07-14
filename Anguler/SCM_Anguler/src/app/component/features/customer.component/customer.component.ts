import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../../service/customer.service';
import { CountryService } from '../../../service/country.service';
import { DivisionService } from '../../../service/division.service';
import { DistrictService } from '../../../service/district.service';
import { PoliceStationService } from '../../../service/police-station.service';
import { environment } from '../../../../environment/environment';
import { CustomerResponseModel, CustomerRequestModel } from '../../shared/model/customerModel';
import { LoginResponse } from '../../../auth/Model/authModel';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css',
})
export class CustomerComponent implements OnInit {
  customers: CustomerResponseModel[] = [];

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
  errorMessage: string | null = null;

  // ফিক্সড: টাইপ সেফটি নিশ্চিত করার জন্য 'any' পরিবর্তন করে রিকোয়েস্ট ইন্টারফেস অ্যাসাইন করা হয়েছে
  customer: CustomerRequestModel = {
    name: '',
    email: '',
    phone: '',
    password: '',
    address: '',
    gender: '',
    dob: '',
    nidNumber: '',
    policeStationId: 0,
  };

  isEdit = false;
  currentEditId: number | null = null;
  isDrawerOpen = false;

  userId!: number;
  singleCustomerProfile: CustomerResponseModel | null = null;
  user: LoginResponse | null = null;

  readonly imageBaseUrl = environment.imgUrl + 'customer/';

  constructor(
    private service: CustomerService,
    private countryService: CountryService,
    private divisionService: DivisionService,
    private districtService: DistrictService,
    private stationService: PoliceStationService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.loadCustomers();
    this.loadCountries();

    if (this.userId) {
      this.loadCustomerByUserId(this.userId);
    }
  }

  loadCustomerByUserId(id: number): void {
    this.service.getCustomerByUserId(id).subscribe({
      next: (profile) => {
        this.singleCustomerProfile = profile;
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Failed to resolve profile matrix:', err),
    });
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
      },
    });
  }

  loadCountries() {
    this.countryService.getAll().subscribe({
      next: (data) => {
        this.countries = data || [];
        this.cdr.markForCheck();
      },
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

    this.divisionService.getByCountryId(this.selectedCountryId).subscribe((res) => {
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

    this.districtService.getByDivisionId(this.selectedDivisionId).subscribe((res) => {
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

    this.stationService.getByDistrictId(this.selectedDistrictId).subscribe((res) => {
      this.policeStations = res || [];
      this.generateFullAddress();
      this.cdr.markForCheck();
    });
  }

  // ফিক্সড: এডিট মোডে চেইন রেন্ডারিং পাইপলাইন যেন ব্রেক না করে
  onDivisionOrDistrictEditPipeline(countryId: number, divisionId: number, districtId: number) {
    this.divisionService.getByCountryId(countryId).subscribe((res) => {
      this.divisions = res || [];
      this.districtService.getByDivisionId(divisionId).subscribe((res2) => {
        this.districts = res2 || [];
        this.stationService.getByDistrictId(districtId).subscribe((res3) => {
          this.policeStations = res3 || [];
          this.cdr.markForCheck();
        });
      });
    });
  }

  // =====================================================
  // AUTO ADDRESS COMPILER
  // =====================================================
  generateFullAddress() {
    const countryName = this.countries.find((x) => x.id == this.selectedCountryId)?.name || '';
    const divisionName = this.divisions.find((x) => x.id == this.selectedDivisionId)?.name || '';
    const districtName = this.districts.find((x) => x.id == this.selectedDistrictId)?.name || '';
    const psName =
      this.policeStations.find((x) => x.id == this.customer.policeStationId)?.name || '';

    this.customer.address = [
      this.streetAddress.trim(),
      psName,
      districtName,
      divisionName,
      countryName,
    ]
      .filter((v) => v && v.trim() !== '')
      .join(', ');
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

  // ফিক্সড: ব্যাকএন্ড থেকে আসা ডুপ্লিকেট ভ্যালিডেশন এরর পার্সিং ইঞ্জিন যুক্ত করা হয়েছে
  private handleBackendError(err: any) {
    this.errorMessage = null;
    const errorContext = err.error?.message || err.message || '';

    if (errorContext.includes('Duplicate entry')) {
      if (errorContext.includes('@')) {
        this.errorMessage = 'Deployment Failed: This Email Address is already registered!';
      } else if (errorContext.includes('phone_number') || errorContext.includes('phone')) {
        this.errorMessage = 'Deployment Failed: This Phone Number is already in use!';
      } else {
        this.errorMessage =
          'Deployment Failed: This NID number identity constraint is already assigned!';
      }
    } else {
      this.errorMessage = errorContext || 'An unexpected database transactional error occurred.';
    }
    this.cdr.markForCheck();
  }

  // =====================================================
  // CRUD CORE METHODS (FIXED FOR MULTIPART @REQUESTPART)
  // =====================================================
  save() {
    this.errorMessage = null;

    if (!this.isEdit && this.customer.password !== this.confirmPassword) {
      this.errorMessage = 'Validation Fault: Password and confirm password inputs do not match.';
      return;
    }

    if (this.customer.policeStationId === 0) {
      this.errorMessage =
        'Validation Fault: Please complete the region distribution up to Police Station.';
      return;
    }

    this.generateFullAddress();

    const formData = new FormData();

    const requestDto: CustomerRequestModel = {
      name: this.customer.name,
      email: this.customer.email,
      phone: this.customer.phone,
      address: this.customer.address,
      gender: this.customer.gender,
      dob: this.customer.dob,
      nidNumber: this.customer.nidNumber,
      policeStationId: Number(this.customer.policeStationId),
    };

    if (!this.isEdit) {
      requestDto.password = this.customer.password;
    } else if (this.customer.password && this.customer.password.trim() !== '') {
      requestDto.password = this.customer.password;
    }

    // JSON কনভার্ট পার্ট ফিক্সিং
    formData.append(
      'customer',
      new Blob([JSON.stringify(requestDto)], { type: 'application/json' }),
    );

    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    }

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, formData).subscribe({
        next: () => {
          alert('Customer profile updated successfully!');
          this.closeDrawer();
          this.loadCustomers();
        },
        error: (err) => this.handleBackendError(err),
      });
    } else {
      this.service.save(formData).subscribe({
        next: () => {
          alert('Customer registered successfully!');
          this.closeDrawer();
          this.loadCustomers();
        },
        error: (err) => this.handleBackendError(err),
      });
    }
  }

  edit(c: CustomerResponseModel) {
    this.currentEditId = c.id;
    this.isEdit = true;
    this.errorMessage = null;

    this.customer = {
      name: c.name,
      email: c.email,
      phone: c.phone,
      password: '',
      address: c.address,
      gender: c.gender,
      dob: c.dob,
      nidNumber: c.nidNumber,
      policeStationId: c.policeStationId,
    };

    const addressParts = c.address ? c.address.split(', ') : [];
    this.streetAddress = addressParts[0] || '';

    this.imagePreview = c.image ? this.getImageUrl(c.image) : null;

    this.selectedCountryId = c.countryId ? +c.countryId : null;
    this.selectedDivisionId = c.divisionId ? +c.divisionId : null;
    this.selectedDistrictId = c.districtId ? +c.districtId : null;
    this.customer.policeStationId = c.policeStationId ? +c.policeStationId : 0;

    // ফিক্সড: এডিট মোডে মাল্টি-টিয়ার ড্রপডাউন লোডের জন্য সেফ পাইপলাইন মেথড ট্রিগার
    if (this.selectedCountryId && this.selectedDivisionId && this.selectedDistrictId) {
      this.onDivisionOrDistrictEditPipeline(
        this.selectedCountryId,
        this.selectedDivisionId,
        this.selectedDistrictId,
      );
    }

    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm('Purge this customer record definitively?')) {
      this.service.delete(id).subscribe({
        next: () => {
          alert('Customer record successfully purged.');
          this.loadCustomers();
        },
        error: (err) => alert('Delete operation encountered a system error.'),
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
      policeStationId: 0,
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
    this.errorMessage = null;
  }
}
