import { Routes } from '@angular/router';
import { CountryComponent } from './component/features/address/country.component/country.component';
import { Division } from './component/features/address/division.component/division.component';
import { DistrictComponent } from './component/features/address/district.component/district.component';
import { PoliceStationComponent } from './component/features/address/police-station.component/police-station.component';
import { LocationComponent } from './component/features/address/location.component/location.component';
import { WarehouseComponent } from './component/features/address/warehouse.component/warehouse.component';
import { CustomerComponent } from './component/features/customer.component/customer.component';


export const routes: Routes = [//

  { path: '', redirectTo: 'country', pathMatch: 'full' },
  { path: 'country', component: CountryComponent },
   { path: 'division', component: Division },
   { path: 'district', component: DistrictComponent },
    { path: 'police-station', component: PoliceStationComponent },
     { path: 'location', component: LocationComponent },
       { path: 'warehouse', component: WarehouseComponent },
          { path: 'customer', component: CustomerComponent }


];