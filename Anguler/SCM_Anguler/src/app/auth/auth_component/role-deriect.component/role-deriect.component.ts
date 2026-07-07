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
      ADMIN:    '/admin',
      MANAGER:    '/managers',
      DRIVER:    '/drivers',
      PROCUREMENT: '/procurements',
      QC_INSPECTOR:    '/qc-inspectors',
      LOGISTICS_OFFICER:    '/logistics-officers',
      COMMERCIAL_OFFICER:    '/commercial-officer',
      CUSTOMER:   '/customer/',
      SUPPLIER:   '/suppliers',
      SALES_OFFICER: '/sales-officers',
    };
    this.router.navigate([map[role ?? ''] ?? '/login']);
  }


}
