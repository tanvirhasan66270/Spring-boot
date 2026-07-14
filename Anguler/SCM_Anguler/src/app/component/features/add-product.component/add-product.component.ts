import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CategoryService } from '../../../service/category.service';
import { environment } from '../../../../environment/environment';
import { ProductRequestModel, ProductResponseModel } from '../../shared/model/addProduct';
import { AddProductService } from '../../../service/add-product.service';

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-product.component.html',
  styleUrl: './add-product.component.css',
})
export class AddProductComponent implements OnInit {

  products: ProductResponseModel[] = [];
  categories: any[] = []; 

  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;
  errorMessage: string | null = null;

  readonly imageBaseUrl = environment.imgUrl+"product/";

  product: ProductRequestModel = {
    id: 0,
    productCode: '',
    name: '',
    unit: '',
    reorderPoint: 0,
    unitCost: 0,
    quantity: 0,
    sellingPrice: 0,
    hasExpiryDate: 'NO',
    weight: 0,
    isActive: true,
    availability: 'AVAILABLE',
    image: '',
    categoryId: 0
  };

  isEdit = false;
  currentEditId: number | null = null;
  isDrawerOpen = false;

  constructor(
    private service: AddProductService,
    private categoryService: CategoryService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadProducts();
    this.loadCategories();
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

  loadProducts() {
    this.service.findAll().subscribe({
      next: (data) => {
        this.products = data || [];
        this.cdr.markForCheck();
      }
    });
  }

  loadCategories() {
    this.categoryService.getAll().subscribe({
      next: (data) => {
        this.categories = data || [];
        this.cdr.markForCheck();
      }
    });
  }

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

  getImageUrl(imageName: string | null | undefined): string {
    return imageName ? `${this.imageBaseUrl}${imageName}` : '';
  }

  onImageError(event: Event): void {
    const target = event.target as HTMLImageElement | null;
    if (target) {
      target.style.display = 'none';
    }
  }

  private handleBackendError(err: any) {
    this.errorMessage = null;
    const errorContext = err.error?.message || err.message || '';

    if (errorContext.includes('already exists') || errorContext.includes('already taken')) {
      this.errorMessage = `Deployment Failed: The product code '${this.product.productCode}' is already registered in inventory!`;
    } else if (errorContext.includes('upload failed') || errorContext.includes('Illegal char')) {
      this.errorMessage = 'I/O Exception: Check out filename configurations or colons inside product title.';
    } else {
      this.errorMessage = errorContext || 'An unexpected transaction drop occurred inside product repository.';
    }
    this.cdr.markForCheck();
  }

  save() {
    this.errorMessage = null;

    if (this.product.categoryId === 0) {
      this.errorMessage = 'Validation Fault: Mapping requires an active cluster Category allocation.';
      return;
    }

    if (this.isEdit && this.currentEditId !== null) {
      this.service.update(this.currentEditId, this.product, this.selectedFile).subscribe({
        next: () => {
          alert("Product metadata structural updates executed successfully!");
          this.closeDrawer();
          this.loadProducts();
        },
        error: (err) => this.handleBackendError(err)
      });
    } else {
      this.service.save(this.product, this.selectedFile).subscribe({
        next: () => {
          alert("New product unit successfully registered into ecosystem!");
          this.closeDrawer();
          this.loadProducts();
        },
        error: (err) => this.handleBackendError(err)
      });
    }
  }

  edit(p: ProductResponseModel) {
    this.errorMessage = null;
    this.currentEditId = p.id;
    this.isEdit = true;

    this.product = {
      id: p.id,
      productCode: p.productCode,
      name: p.name,
      unit: p.unit,
      reorderPoint: p.reorderPoint,
      unitCost: p.unitCost,
      quantity: p.quantity,
      sellingPrice: p.sellingPrice,
      hasExpiryDate: p.hasExpiryDate,
      weight: p.weight,
      isActive: p.isActive,
      availability: p.availability,
      image: p.image,
      categoryId: p.categoryId
    };

    this.imagePreview = p.image ? this.getImageUrl(p.image) : null;
    this.isDrawerOpen = true;
    this.cdr.markForCheck();
  }

  delete(id: number) {
    if (confirm("Definitively purge this product item and its tracking indexes from active SCM systems?")) {
      this.service.delete(id).subscribe({
        next: () => {
          alert("Product node metadata permanently deleted.");
          this.loadProducts();
        },
        error: (err) => this.handleBackendError(err)
      });
    }
  }

  reset() {
    this.product = {
      id: 0,
      productCode: '',
      name: '',
      unit: '',
      reorderPoint: 0,
      unitCost: 0,
      quantity: 0,
      sellingPrice: 0,
      hasExpiryDate: 'NO',
      weight: 0,
      isActive: true,
      availability: 'AVAILABLE',
      image: '',
      categoryId: 0
    };
    this.selectedFile = null;
    this.imagePreview = null;
    this.isEdit = false;
    this.currentEditId = null;
    this.errorMessage = null;
  }
}
