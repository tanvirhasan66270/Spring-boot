import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styles: []
})
export class ForgotPasswordComponent {
  email = '';
  loading = false;
  success = false;
  message = '';

  constructor(private authService: AuthService) {}

  onSubmit(): void {
    if (!this.email) return;

    this.loading = true;
    this.message = '';

    this.authService.forgotPassword({ email: this.email }).subscribe({
      next: (res: string) => {
        this.loading = false;
        this.success = true;
        this.message = res || 'Password reset link sent to your email!';
      },
      error: (err: any) => {
        this.loading = false;
        this.success = false;
        this.message = err.error?.message || 'Failed to request password reset. Try again.';
      }
    });
  }
}
