import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

const BASE_URL = 'http://localhost:1011/api/retailbanking';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  constructor(private http: HttpClient) { }

  registerCustomer(customerRequest: any): Observable<any> {
    return this.http.post(BASE_URL + '/customer/customer/register', customerRequest, { headers: this.headers });
  }

  getCustomerDetails(userId: number): Observable<any> {
    return this.http.get(`${BASE_URL}/customer/customer/${userId}`);
  }

  getAllCustomer(): Observable<any> {
    return this.http.get(`${BASE_URL}/customer/allCustomer`);
  }

  getCustomerProfileUpdate(userId: number): Observable<any> {
    return this.http.get(`${BASE_URL}/customer/customers/${userId}`);
  }

  updateCustomerDetails(customerId: number, updatedData: any): Observable<any> {
    const url = `${BASE_URL}/customer/customer/update/${customerId}`;
    return this.http.put<any>(url, updatedData);
  }

  forgotUserId(forgotIdDTO: any): Observable<any> {
    return this.http.post(BASE_URL + '/customer/forgot-userid', forgotIdDTO, { headers: this.headers });
  }

  updateNotifications(customerId:number,typeofnotification:string,isChecked: boolean):Observable<any>{
    // /customer/updateNotification/Email/Security_alerts/12345678
    let notificationType = isChecked ? 'Email' : 'NA';
    const url= `${BASE_URL}/customer/updateNotification/${notificationType}/${typeofnotification}/${customerId}`;
    return this.http.get(url);
  }
 
  getNotifications(customerId:number):Observable<any>{
    const url= `${BASE_URL}/customer/getNotificationData/${customerId}/0`;
    return this.http.get(url);
  }

  forgotPassword(forgotPasswordDTO: any): Observable<any> {
    const url = `${BASE_URL}/customer/forgot-password`;
    return this.http.post(url, forgotPasswordDTO, { headers: this.headers });
  }
 
  resetPassword(passwordToken: string, newPassword: string): Observable<any> {
    const url = `${BASE_URL}/customer/reset-password/${passwordToken}`;
    return this.http.post(url, newPassword);
  }
 
  validateToken(token: string): Observable<any> {
    const url = `${BASE_URL}/customer/validate/${token}`;
    return this.http.get(url);
  }

}