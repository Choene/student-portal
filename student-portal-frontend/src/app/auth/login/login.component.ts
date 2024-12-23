import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (response) => {
        if (response.requiresOtp) {
          // Redirect to OTP login if 2FA is enabled
          this.router.navigate(['/auth/otp'], { state: { email: this.email } });
        } else if (response.token) {
          localStorage.setItem('token', response.token);
          this.router.navigate(['student/profile']);
        } else {
          this.error = 'Unexpected error. Please try again.';
        }
      },
      error: () => {
        this.error = 'Invalid login credentials.';
      }
    });
  }

}
