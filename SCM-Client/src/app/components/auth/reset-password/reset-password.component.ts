import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reset-password.component.html',
  styles: []
})
export class ResetPasswordComponent implements OnInit {
  token = '';
  newPassword = '';
  confirmPassword = '';
  loading = false;
  success = false;
  message = '';

  constructor(private route: ActivatedRoute, private authService: AuthService) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (!this.token) {
      this.message = 'No security token provided. Link is invalid.';
    }
  }

  onSubmit(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.message = 'Passwords do not match.';
      return;
    }

    if (this.newPassword.length < 4) {
      this.message = 'Password must be at least 4 characters long.';
      return;
    }

    this.loading = true;
    this.message = '';

    this.authService.resetPassword({ token: this.token, newPassword: this.newPassword }).subscribe({
      next: (res: string) => {
        this.loading = false;
        this.success = true;
        this.message = res || 'Password reset successfully!';
      },
      error: (err: any) => {
        this.loading = false;
        this.success = false;
        this.message = err.error?.message || 'Failed to reset password. Link may be expired.';
      }
    });
  }
}
