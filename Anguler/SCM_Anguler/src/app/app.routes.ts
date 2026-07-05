import { Routes } from '@angular/router';
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
import { ProcourmentComponent } from './component/features/procourment.component/procourment.component';


export const routes: Routes = [//

  { path: '', redirectTo: 'country', pathMatch: 'full' },
  { path: 'country', component: CountryComponent },
   { path: 'division', component: Division },
   { path: 'district', component: DistrictComponent },
    { path: 'police-station', component: PoliceStationComponent },
     { path: 'location', component: LocationComponent },
       { path: 'warehouse', component: WarehouseComponent },
          { path: 'customer', component: CustomerComponent },
           { path: 'commercial-officer', component: CommercialOfficerComponent },
             { path: 'driver', component: DriverComponent },
             { path: 'logistics-officer', component: LogisticsOfficerComponent },
              { path: 'manager', component: ManagerComponent },
                    { path: 'qc-inspector', component: QcinspactorComponent },
                    { path: 'sales-officer', component: SalesOfficerComponent },
                      { path: 'supplier', component: SupplierComponent},
                      { path: 'procurement', component: ProcourmentComponent}
                       



];