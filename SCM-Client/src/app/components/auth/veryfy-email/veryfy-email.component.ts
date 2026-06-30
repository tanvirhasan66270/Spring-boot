import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-veryfy-email',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './veryfy-email.component.html',
  styles: []
})
export class VeryfyEmailComponent implements OnInit {
  token: string | null = null;
  loading = true;
  success = false;
  message = '';

  constructor(private route: ActivatedRoute, private authService: AuthService) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
    if (!this.token) {
      this.loading = false;
      this.success = false;
      this.message = 'No verification token provided in URL.';
      return;
    }

    this.authService.verifyEmail(this.token).subscribe({
      next: (res: string) => {
        this.loading = false;
        this.success = true;
        this.message = res || 'Email verified successfully!';
      },
      error: (err: any) => {
        this.loading = false;
        this.success = false;
        this.message = err.error?.message || 'Verification link is invalid or has expired.';
      }
    });
  }
}
