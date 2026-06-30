import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './guards/auth-guard';

// Layouts
import { BlankLayoutComponent } from './components/shared/layout/blank-layout/blank-layout.component';
import { MainLayoutComponent } from './components/shared/layout/main-layout/main-layout.component';

// Auth Components
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';
import { ForgotPasswordComponent } from './components/auth/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/auth/reset-password/reset-password.component';
import { VeryfyEmailComponent } from './components/auth/veryfy-email/veryfy-email.component';
import { RoleRedirectComponent } from './components/auth/role-redirect/role-redirect.component';

// Feature Components
import { HomeComponent } from './components/shared/layout/home/home.component';
import { AgentsComponent } from './components/feature/agents/agents.component';
import { CustomerComponent } from './components/feature/customer/customer.component';
import { RidersComponent } from './components/feature/riders/riders.component';
import { AddressComponent } from './components/feature/address/address.component';
import { BookingReceiptComponent } from './components/feature/print/booking-receipt.component';

export const routes: Routes = [
  // Public/Auth routes wrapped in blank layout
  {
    path: '',
    component: BlankLayoutComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'forgot-password', component: ForgotPasswordComponent },
      { path: 'reset-password', component: ResetPasswordComponent },
      { path: 'api/auth/verify-email', component: VeryfyEmailComponent }, // Match verification links from API
      { path: 'verify-email', component: VeryfyEmailComponent },
      { path: 'role-redirect', component: RoleRedirectComponent, canActivate: [authGuard] }
    ]
  },
  
  // Guarded feature routes wrapped in main layout
  {
    path: 'feature',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { 
        path: 'agents', 
        component: AgentsComponent, 
        canActivate: [roleGuard(['ADMIN', 'MANAGER'])] 
      },
      { 
        path: 'customer', 
        component: CustomerComponent, 
        canActivate: [roleGuard(['CUSTOMER', 'MANAGER', 'ADMIN'])] 
      },
      { 
        path: 'riders', 
        component: RidersComponent, 
        canActivate: [roleGuard(['DRIVER', 'MANAGER', 'ADMIN'])] 
      },
      { 
        path: 'address', 
        component: AddressComponent 
      },
      { 
        path: 'print/booking-receipt', 
        component: BookingReceiptComponent 
      }
    ]
  },

  // Fallback
  { path: '**', redirectTo: 'home' }
];
