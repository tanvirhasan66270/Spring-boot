import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CountryService } from '../../../../service/country.service';
import { CountryRequestModel, CountryResponseModel } from '../../../shared/model/country/countryModel';

@Component({
  selector: 'app-country.component',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './country.component.html',
  styleUrl: './country.component.css',
})
export class CountryComponent implements OnInit {

  countries: CountryResponseModel[] = [];
  
  country: CountryRequestModel = {
    name: '',
    code: '',
    phoneCode: '',
    active: true
  };

  isEdit = false;
  currentEditId: number | null = null; 
  isDrawerOpen = false; // ডান পাশের ড্রয়ার স্টেট ট্র্যাকিং

  constructor(
    private service: CountryService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadCountries();
  }

  openDrawer() {
    this.isDrawerOpen = true;
  }

  closeDrawer() {
    this.isDrawerOpen = false;
    this.reset();
  }

  loadCountries() {
    this.service.getAll().subscribe({
      next: (data) => {
        this.countries = data;
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error loading countries:', err)
    });
  }

  save() {
    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.country).subscribe({
        next: () => {
          alert("Updated Successfully");
          this.closeDrawer();
          this.loadCountries();
        },
        error: (err) => console.error('Error updating country:', err)
      });
    } else {
      this.service.save(this.country).subscribe({
        next: () => {
          alert("Saved Successfully");
          this.closeDrawer();
          this.loadCountries();
        },
        error: (err) => console.error('Error saving country:', err)
      });
    }
  }

  edit(c: CountryResponseModel) {
    this.currentEditId = c.id;
    this.country = {
      name: c.name,
      code: c.code,
      phoneCode: c.phoneCode,
      active: c.active
    };
    this.isEdit = true;
    this.openDrawer(); // এডিটে ক্লিক করলে ড্রয়ার ওপেন হবে
  }

  delete(id: number) {
    if (confirm("Delete this country?")) {
      this.service.delete(id).subscribe({
        next: () => {
          alert("Deleted successfully");
          this.loadCountries();
        },
        error: (err) => console.error('Error deleting country:', err)
      });
    }
  }

  reset() {
    this.country = {
      name: '',
      code: '',
      phoneCode: '',
      active: true
    };
    this.isEdit = false;
    this.currentEditId = null;
  }
}