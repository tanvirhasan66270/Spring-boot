
import { CountryComponent } from './component/features/address/country.component/country.component';
import { Division } from './component/features/address/division.component/division.component';
import { DistrictComponent } from './component/features/address/district.component/district.component';
import { PoliceStationComponent } from './component/features/address/police-station.component/police-station.component';
import { LocationComponent } from './component/features/address/location.component/location.component';
import { WarehouseComponent } from './component/features/address/warehouse.component/warehouse.component';
import { CustomerComponent } from './component/features/customer.component/customer.component';
import { CommercialOfficerComponent } from './component/features/commercial-officer.component/commercial-officer.component';
import { DriverComponent } from './component/features/driver.component/driver.component';
import { LogisticsOfficerComponent } from './component/features/logistics-officer.component/logistics-officer.component';
import { ManagerComponent } from './component/features/manager.component/manager.component';
import { QcinspactorComponent } from './component/features/qcinspactor.component/qcinspactor.component';
import { SalesOfficerComponent } from './component/features/sales-officer.component/sales-officer.component';
import { SupplierComponent } from './component/features/supplier.component/supplier.component';
import { CategoryComponent } from './component/features/category.component/category.component';
import { ProcourmentComponent } from './component/features/procourment.component/procourment.component';
import { AddProductComponent } from './component/features/add-product.component/add-product.component';
import { CustomerOrderComponent } from './component/features/customer-order.component/customer-order.component';
import { LoginComponent } from './auth/auth_component/login.component/login.component';
import { ForgetPasswordComponent } from './auth/auth_component/forget-password.component/forget-password.component';
import { ResetPasswordComponent } from './auth/auth_component/reset-password.component/reset-password.component';
import { BlankLayoutComponent } from './component/shared/layout/blank-layout.component/blank-layout.component';
import { MainLayoutComponent } from './component/shared/layout/main-layout.component/main-layout.component';
import { VeryfyEmailComponent } from './auth/auth_component/veryfy-email.component/veryfy-email.component';
import { RoleDeriectComponent } from './auth/auth_component/role-deriect.component/role-deriect.component';
import { Routes } from '@angular/router';
import { CustomerDashboardComponent } from './component/dashboard/customer-dashboard.component/customer-dashboard.component';
import { PurchaseRequisitionComponent } from './component/features/purchase-requisition.component/purchase-requisition.component';
import { QuatationComponent } from './component/features/quatation.component/quatation.component';


export const routes: Routes = [

  {
    path: '',
    component: BlankLayoutComponent,
    children: [
      { path: '', redirectTo: 'country', pathMatch: 'full' },
      { path: 'customer', component: CustomerComponent },
      { path: 'commercial-officer', component: CommercialOfficerComponent },
      { path: 'driver', component: DriverComponent },
      { path: 'logistics-officer', component: LogisticsOfficerComponent },
      { path: 'manager', component: ManagerComponent },
      { path: 'qc-inspector', component: QcinspactorComponent },
      { path: 'sales-officer', component: SalesOfficerComponent },
      { path: 'supplier', component: SupplierComponent },
      { path: 'procurement', component: ProcourmentComponent },
      { path: 'category', component: CategoryComponent },
      { path: 'product', component: AddProductComponent },
    
      { path: 'login', component: LoginComponent },
      { path: 'forgot-password', component: ForgetPasswordComponent },
      { path: 'reset-password', component: ResetPasswordComponent },
      { path: 'verify-email', component: VeryfyEmailComponent }



    ]
  },

  {
    path: '',
    component: MainLayoutComponent,
    children: [
       { path: 'order', component: CustomerOrderComponent },
      { path: 'dashboard', component:RoleDeriectComponent  },
      { path: 'country', component: CountryComponent },
      { path: 'division', component: Division },
      { path: 'district', component: DistrictComponent },
      { path: 'police-station', component: PoliceStationComponent },
      { path: 'location', component: LocationComponent },
      { path: 'warehouse', component: WarehouseComponent },
      { path: 'order-dashboard', component: CustomerDashboardComponent },
      { path: 'purchase-requisition', component: PurchaseRequisitionComponent },
      { path: 'quotation', component: QuatationComponent }

    ]
  }













];