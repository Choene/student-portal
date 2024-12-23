import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
  ],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss',
})
export class ForgotPasswordComponent {
  email = '';
  success = '';
  error = '';

  constructor(private authService: AuthService) {}

  onSubmit() {
    this.authService.forgotPassword(this.email).subscribe({
      next: () => {
        this.success = 'Password reset email sent successfully.';
        this.error = '';
      },
      error: () => {
        this.error = 'Failed to send password reset email. Please try again.';
        this.success = '';
      },
    });
  }
}
