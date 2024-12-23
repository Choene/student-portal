import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl: string;

  constructor(private http: HttpClient) {
    this.apiUrl = environment.production ? 'https://hopeful-clarity-production.up.railway.app/api' : 'http://localhost:8081/api';
   }

   login(data: { email: string; password: string }): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/auth/login`, data, { headers: { 'Content-Type': 'application/json' } })
      .pipe(catchError(this.handleError));
  }

  register(data: any): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/auth/register`, data, { headers: { 'Content-Type': 'application/json' } })
      .pipe(catchError(this.handleError));
  }

  forgotPassword(email: string): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/auth/forgot-password`, { email }, { headers: { 'Content-Type': 'application/json' } })
      .pipe(catchError(this.handleError));
  }

  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    const message = error?.error?.message || 'Error: Something went wrong. Please try again.';
    return throwError(() => new Error(message));
  }

  decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch (error) {
      console.error('Failed to decode token', error);
      return null;
    }
  }

  verifyOtp(data: { email: string; otp: string }): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/auth/verify-otp`, data)
      .pipe(catchError(this.handleError));
  }

  // verifyOtp(data: { email: string; otp: string }): Observable<any> {
  //   return this.http.post(`${this.apiUrl}/auth/verify-otp`, data, { headers: { 'Content-Type': 'application/json' } })
  //     .pipe(catchError(this.handleError));
  // }

}
