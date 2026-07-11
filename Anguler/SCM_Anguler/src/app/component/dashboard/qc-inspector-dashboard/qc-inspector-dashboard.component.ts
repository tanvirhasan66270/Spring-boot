import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { StorageService } from '../../../auth/auth_service/storage.service';

@Component({
  selector: 'app-qc-inspector-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './qc-inspector-dashboard.component.html',
  styleUrls: ['./qc-inspector-dashboard.component.css']
})
export class QCInspectorDashboardComponent implements OnInit {
  userName = '';

  kpis = [
    { label: 'Pending Inspection', value: '26 Batches', trend: 30, icon: 'bi-clipboard-pulse', color: 'purple' },
    { label: 'Passed Today', value: '142 Items', trend: 15, icon: 'bi-check-circle', color: 'success' },
    { label: 'Defects Rate', value: '2.8%', trend: -18, icon: 'bi-x-circle', color: 'danger' },
    { label: 'Today\'s Audit', value: '8 Logs', trend: 0, icon: 'bi-journal-check', color: 'info' }
  ];

  queue = [
    { batchId: 'B-771', product: 'Universal Bearing Grease Tub', sampleSize: 10, defectsFound: 3, status: 'FAIL' },
    { batchId: 'B-772', product: 'Premium Anti-Rust Coating Liquid', sampleSize: 5, defectsFound: 0, status: 'PASS' },
    { batchId: 'B-773', product: 'Textile Fabric Stain Remover', sampleSize: 20, defectsFound: 1, status: 'PENDING' }
  ];

  defects = [
    { code: 'DF-01', description: 'Viscosity lower than threshold spec', count: 4, severity: 'HIGH' },
    { code: 'DF-04', description: 'Seal structural rupture during stress test', count: 2, severity: 'CRITICAL' }
  ];

  constructor(
    private storage: StorageService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const user = this.storage.getUser();
    if (user) {
      this.userName = user.name || 'QC Inspector';
    }
  }

  pass(batchId: string): void {
    const batch = this.queue.find(b => b.batchId === batchId);
    if (batch) {
      batch.status = 'PASS';
      batch.defectsFound = 0;
      alert(`Batch ${batchId} marked as passed.`);
      this.cdr.markForCheck();
    }
  }

  fail(batchId: string): void {
    const batch = this.queue.find(b => b.batchId === batchId);
    if (batch) {
      batch.status = 'FAIL';
      batch.defectsFound = 4;
      alert(`Batch ${batchId} logged as failure.`);
      this.cdr.markForCheck();
    }
  }

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }
}
