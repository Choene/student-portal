import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  profile: any;
  error = '';
  twoFactorEnabled = false;
  requiresOtp = false;
  otp = '';

  constructor(private studentService: StudentService, private router: Router) { }

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.error = 'No token found. Redirecting to login...';
      this.router.navigate(['/auth/login']);
      return;
    }

    // Decode token to extract user email
    const decoded = (() => {
      try {
        return JSON.parse(atob(token.split('.')[1]));
      } catch (e) {
        console.error("Failed to decode token:", e);
        return null;
      }
    })();
    const email = decoded?.email;

    if (email) {
      this.studentService.getProfile(email).subscribe({
        next: (profile: any) => {
          this.profile = profile;
          this.twoFactorEnabled = profile.twoFactorEnabled;
        },
        error: () => {
          this.error = 'Failed to fetch profile. Please try again.';
        },
      });
    } else {
      this.error = 'Invalid token. Please Log in again.';
      this.router.navigate(['/auth/login']);
    }
  }

  toggleTwoFactor(): void {
    this.error = ''; // Clear any existing errors

    // Send OTP and open OTP modal
    this.studentService.sendOtp(this.profile.email).subscribe({
      next: (response: any) => {
        if (response.message.includes('OTP sent')) {
          this.requiresOtp = true; // Show OTP modal
        } else {
          this.error = 'Failed to send OTP. Please try again.';
        }
      },
      error: (err: any) => {
        this.error = err.error?.message || 'Error: Failed to send OTP. Please try again.';
      }
    });
  }

  verifyOtp(): void {
    if (!this.otp) {
      this.error = 'Please enter the OTP.';
      return;
    }

    this.studentService.verifyOtp(this.profile.email, this.otp).subscribe({
      next: (response: any) => {
        this.twoFactorEnabled = response.twoFactorEnabled; // Update 2FA status
        this.requiresOtp = false; // Close OTP modal
        this.error = ''; // Clear errors
      },
      error: (err: any) => {
        this.error = err.error?.message || 'Invalid or expired OTP. Please try again.';
      }
    });
  }

  cancelOtp(): void {
    this.requiresOtp = false; // Close OTP modal
    this.otp = ''; // Clear OTP input
  }

  refreshProfile(): void {
    this.studentService.getProfile(this.profile.email).subscribe({
      next: (profile: any) => {
        this.profile = profile;
        this.twoFactorEnabled = profile.twoFactorEnabled;
      },
      error: () => {
        this.error = 'Failed to refresh profile. Please try again.';
      }
    });
  }

  logout(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/auth/login']);
  }
}
