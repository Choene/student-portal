import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  name = '';
  email = '';
  password = '';
  confirmPassword = '';
  success = '';
  error = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onRegister() {
    if (this.password !== this.confirmPassword) {
      this.error = 'Passwords do not match';
      this.success = '';
      return;
    }

    this.loading = true;

    this.authService.register({ name: this.name, email: this.email, password: this.password, confirmPassword: this.confirmPassword }).subscribe({
      next: () => {
        this.success = 'Registration successful. Redirecting to login...';
        this.error = '';
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/auth/login']);
        }, 3000);
      },
      error: (error) => {
        this.error = 'Registration failed. Try again.';
        this.success = '';
        this.loading = false;
      },
    });
  }
}
