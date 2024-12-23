import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-otp-login',
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './otp-login.component.html',
  styleUrl: './otp-login.component.scss'
})
export class OtpLoginComponent {

  email = '';
  otp = '';
  error = '';

  constructor(private authService: AuthService, private router: Router) {}

  onVerifyOtp() {
    const email = localStorage.getItem('email');
    if (!email) {
      this.error = 'Email not found. Please log in again.';
      this.router.navigate(['/auth/login']);
      return;
    }

    this.authService.verifyOtp({ email, otp: this.otp }).subscribe({
      next: (response) => {
        if (response.token) {
          localStorage.setItem('token', response.token);
          this.router.navigate(['/student/profile']);
        }
      },
      error: (err) => {
        this.error = err.error || 'Invalid or expired OTP.';
      },
    });
  }
}
