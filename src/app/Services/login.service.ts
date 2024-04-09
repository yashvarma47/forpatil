import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

const BASE_URL = 'http://localhost:1010/api/retailbanking';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  // private headers = new HttpHeaders({ 
  //   'Content-Type': 'application/json',
  //   'Access-Control-Allow-Origin': '*',
  //   'Access-Control-Allow-Methods': 'POST, GET, PUT, OPTIONS, DELETE',
  //   'Access-Control-Max-Age': '3600',
  //   'Access-Control-Allow-Headers': '*'
  // });

  constructor(private http: HttpClient) {}

  validateUserId(loginRequest: any): Observable<any> {
    return this.http.post(BASE_URL + '/login/userId', loginRequest, { headers: this.headers });
  }

  login(loginRequest: any): Observable<any> {
    return this.http.post(BASE_URL + '/login/loginUser', loginRequest, { headers: this.headers });
  }

  deleteCustomer(customerId: number): Observable<any> {
    return this.http.get(`${BASE_URL}/login/customer/delete-customer/${customerId}`);
  }

  changePassword(customerId: number, oldPassword: string, newPassword: string): Observable<any> {
    const params = new HttpParams()
      .set('oldPassword', oldPassword)
      .set('newPassword', newPassword);

    return this.http.post(`${BASE_URL}/login/change-password/${customerId}`, null, { params });
  }
}
