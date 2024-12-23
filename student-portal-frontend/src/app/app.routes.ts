import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';
import { ProfileComponent } from './student/profile/profile.component';
import { authGuard } from './guards/auth.guard';
import { OtpLoginComponent } from './auth/otp-login/otp-login.component';

export const routes: Routes = [
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/otp', component: OtpLoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'auth/forgot-password', component: ForgotPasswordComponent },
  { path: 'student/profile', component: ProfileComponent, canActivate: [authGuard] },
];
