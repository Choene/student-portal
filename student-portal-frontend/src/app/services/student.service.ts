import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private apiUrl: string;

  constructor(private http: HttpClient) {
    this.apiUrl = environment.production ? 'https://api.production.com/api' : 'http://localhost:8081/api';
  }

  getProfile(email: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/profile`, { params: { email } });
  }

  toggleMfa(email: string, otp?: string): Observable<{ twoFactorEnabled: boolean }> {
    const params: any = { email };
    if (otp) {
      params.otp = otp;
    }
    return this.http.patch<{ twoFactorEnabled: boolean }>(
      `${this.apiUrl}/profile/mfa`,
      {},
      { params }
    );
  }

  verifyOtp(email: string, otp: string): Observable<{ twoFactorEnabled: boolean }> {
    return this.http.post<{ twoFactorEnabled: boolean }>(
      `${this.apiUrl}/profile/validate-otp`,
      {},
      { params: { email, otp } }
    );
  }

  sendOtp(email: string): Observable<any> {
    return this.http.post('/profile/send-otp', { email });
  }
}
