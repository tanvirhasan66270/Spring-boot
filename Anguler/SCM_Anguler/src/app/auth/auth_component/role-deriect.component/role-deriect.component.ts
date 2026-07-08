import { Component, OnInit } from '@angular/core';
import { StorageService } from '../../auth_service/storage.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-role-deriect.component',
  imports: [],
  templateUrl: './role-deriect.component.html',
  styleUrl: './role-deriect.component.css',
})
export class RoleDeriectComponent implements OnInit {

constructor(private storage: StorageService, private router: Router) { }

  ngOnInit(): void {
    const role = this.storage.getRole();
    const map: Record<string, string> = {
      ADMIN:  '/admin',
      MANAGER:  '/manager',
      DRIVER:  '/driver',
      PROCUREMENT: '/procurement',
      QC_INSPECTOR: '/qc-inspector',
      LOGISTICS_OFFICER:  '/logistics-officer',
      COMMERCIAL_OFFICER:  '/commercial-officer',
      CUSTOMER:   '/customer',
      SUPPLIER:   '/supplier',
      SALES_OFFICER: '/sales-officer',
    };
    this.router.navigate([map[role ?? ''] ?? '/login']);
  }


}
