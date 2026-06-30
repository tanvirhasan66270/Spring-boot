import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { LocationService } from '../../../services/location.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styles: []
})
export class RegisterComponent implements OnInit {
  // Core user info
  form = {
    name: '',
    email: '',
    phone: '',
    password: '',
    role: 'CUSTOMER', // CUSTOMER, SUPPLIER, DRIVER
    address: '',
    gender: 'MALE',
    dob: '',
    nidNumber: '',
    
    // Cascading address selections
    countryId: null as number | null,
    divisionId: null as number | null,
    districtId: null as number | null,
    policeStationId: null as number | null,

    // Supplier specific
    contactPerson: '',
    
    // Driver specific
    vehicleType: 'TRUCK',
    vehicleNumber: ''
  };

  countries: any[] = [];
  divisions: any[] = [];
  districts: any[] = [];
  policeStations: any[] = [];

  selectedPhoneCode = '';
  selectedFile: File | null = null;
  loading = false;
  success = false;
  message = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private locationService: LocationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCountries();
  }

  loadCountries(): void {
    this.locationService.getCountries().subscribe({
      next: (data) => {
        this.countries = data;
        // Mock fallback if empty
        if (this.countries.length === 0) {
          this.countries = [
            { id: 1, name: 'Bangladesh', code: 'BD', phoneCode: '+880' },
            { id: 2, name: 'United States', code: 'US', phoneCode: '+1' },
            { id: 3, name: 'United Kingdom', code: 'GB', phoneCode: '+44' }
          ];
        }
      },
      error: () => {
        this.countries = [
          { id: 1, name: 'Bangladesh', code: 'BD', phoneCode: '+880' },
          { id: 2, name: 'United States', code: 'US', phoneCode: '+1' },
          { id: 3, name: 'United Kingdom', code: 'GB', phoneCode: '+44' }
        ];
      }
    });
  }

  onCountryChange(): void {
    this.divisions = [];
    this.districts = [];
    this.policeStations = [];
    this.form.divisionId = null;
    this.form.districtId = null;
    this.form.policeStationId = null;
    this.selectedPhoneCode = '';

    if (this.form.countryId) {
      // Extract phone code dynamically based on country selection
      const selectedCountry = this.countries.find(c => Number(c.id) === Number(this.form.countryId));
      if (selectedCountry) {
        this.selectedPhoneCode = selectedCountry.phoneCode || '';
      }

      this.locationService.getDivisions(this.form.countryId).subscribe({
        next: (data) => {
          this.divisions = data;
        },
        error: () => {
          this.divisions = [
            { id: 1, name: 'Dhaka', nameBn: 'ঢাকা' },
            { id: 2, name: 'Chittagong', nameBn: 'চট্টগ্রাম' }
          ];
        }
      });
    }
  }

  onDivisionChange(): void {
    this.districts = [];
    this.policeStations = [];
    this.form.districtId = null;
    this.form.policeStationId = null;

    if (this.form.divisionId) {
      this.locationService.getDistricts(this.form.divisionId).subscribe({
        next: (data) => {
          this.districts = data;
        },
        error: () => {
          this.districts = [
            { id: 1, name: 'Dhaka', nameBn: 'ঢাকা' },
            { id: 2, name: 'Gazipur', nameBn: 'গাজীপুর' }
          ];
        }
      });
    }
  }

  onDistrictChange(): void {
    this.policeStations = [];
    this.form.policeStationId = null;

    if (this.form.districtId) {
      this.locationService.getPoliceStations(this.form.districtId).subscribe({
        next: (data) => {
          this.policeStations = data;
        },
        error: () => {
          this.policeStations = [
            { id: 1, name: 'Mirpur PS', nameBn: 'মিরপুর থানা', postalCode: '1216' },
            { id: 2, name: 'Uttara PS', nameBn: 'উত্তরা থানা', postalCode: '1230' }
          ];
        }
      });
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';
    this.message = '';

    const formData = new FormData();
    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    }

    const fullPhoneNumber = this.selectedPhoneCode + this.form.phone;

    if (this.form.role === 'CUSTOMER') {
      const customerDto = {
        name: this.form.name,
        email: this.form.email,
        phone: fullPhoneNumber,
        password: this.form.password,
        address: this.form.address,
        gender: this.form.gender,
        dob: this.form.dob,
        nidNumber: this.form.nidNumber,
        policeStationId: this.form.policeStationId
      };
      
      const customerBlob = new Blob([JSON.stringify(customerDto)], { type: 'application/json' });
      formData.append('customer', customerBlob);

      this.authService.registerCustomer(formData).subscribe({
        next: () => this.handleSuccess(),
        error: (err) => this.handleError(err)
      });

    } else if (this.form.role === 'SUPPLIER') {
      const supplierDto = {
        name: this.form.name,
        email: this.form.email,
        phone: fullPhoneNumber,
        password: this.form.password,
        address: this.form.address,
        gender: this.form.gender,
        dob: this.form.dob,
        nidNumber: this.form.nidNumber,
        policeStationId: this.form.policeStationId,
        contactPerson: this.form.contactPerson,
        rating: 5,
        averageLeadTimeDays: 7
      };

      formData.append('suppliers', JSON.stringify(supplierDto));

      this.authService.registerSupplier(formData).subscribe({
        next: () => this.handleSuccess(),
        error: (err) => this.handleError(err)
      });

    } else if (this.form.role === 'DRIVER') {
      const driverDto = {
        driverName: this.form.name,
        email: this.form.email,
        phone: fullPhoneNumber,
        password: this.form.password,
        address: this.form.address,
        gender: this.form.gender,
        dob: this.form.dob,
        nidNumber: this.form.nidNumber,
        policeStationId: this.form.policeStationId,
        vehicleType: this.form.vehicleType,
        vehicleNumber: this.form.vehicleNumber
      };

      formData.append('driver', JSON.stringify(driverDto));

      this.authService.registerDriver(formData).subscribe({
        next: () => this.handleSuccess(),
        error: (err) => this.handleError(err)
      });
    }
  }

  private handleSuccess(): void {
    this.loading = false;
    this.success = true;
    this.message = 'Registration initialized! Verification link dispatched to your email.';
    setTimeout(() => {
      this.router.navigate(['/login']);
    }, 4000);
  }

  private handleError(err: any): void {
    this.loading = false;
    this.errorMessage = err.error?.message || 'Registration failed. Please audit input params.';
  }
}
