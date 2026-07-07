import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginRequest } from '../../Model/authModel';
import { AuthService } from '../../auth_service/auth-service';
import { Router } from '@angular/router';
import { Header } from '../../../component/shared/layout/header/header';

@Component({
  selector: 'app-login.component',
  imports: [CommonModule, FormsModule ,Header],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {



  dto: LoginRequest = { email: '', password: '' };

  showPassword = false;
  loading = false;
  errorMessage: string | null = null;

  constructor(private auth: AuthService, private router: Router) { }


  login(): void {
    this.loading = true;
    this.errorMessage = null;

    this.auth.login(this.dto).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err.status === 401
            ? 'Invalid email or password.'
            : err.status === 403
              ? 'Your account is not verified or has been disabled.'
              : 'Something went wrong. Please try again.';
      }
    });
  }



}
