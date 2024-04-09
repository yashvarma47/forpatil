
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

const BASE_URL = 'http://localhost:1012/api/retailbanking';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  constructor(private http: HttpClient) { }

  getCustomerDetails(userId: number): Observable<any> {
    return this.http.get(`${BASE_URL}/accounts/details/${userId}`);
  }

  createAccount(userId: number, accountType: string): Observable<any> {
    const queryParams = `?accountType=${accountType}`;
    return this.http.post(`${BASE_URL}/accounts/create/${userId}${queryParams}`, null, { headers: this.headers });
  }

  blockAccount(accountNo: number, status: String, reason: String): Observable<any> {
    return this.http.get(`${BASE_URL}/accounts/updateAccountStatus/${accountNo}/${status}/${reason}`);
  }

  getAllAccounts(): Observable<any> {
    return this.http.get(`${BASE_URL}/accounts/allAccounts`);
  }

  getAccountDetails(accuntnumber: number): Observable<any> {
    return this.http.get(`${BASE_URL}/accounts/fetchAccountDetails/${accuntnumber}`);
  }

  createTicket(ticketDTO: any): Observable<any> {
    return this.http.post(`${BASE_URL}/accounts/ticket/create`, ticketDTO);
  }

  fetchTicketIds(): Observable<any> {
    return this.http.get(`${BASE_URL}/accounts/ticketIds`);
  }

  updateTicket(ticketDTO: any, ticketId: number): Observable<any> {
    return this.http.put(`${BASE_URL}/accounts/ticket/update/${ticketId}`, ticketDTO);
  }

  getTicketDetails(ticketId: number): Observable<any> {
    return this.http.get(`${BASE_URL}/accounts/getTicket/${ticketId}`);
  }

  closeAccount(accountNumber: number): Observable<any> {
    return this.http.post(`${BASE_URL}/accounts/close/${accountNumber}`, null);
  }

  getAllBlockedAccounts(): Observable<any> {
    return this.http.get(`${BASE_URL}/accounts/getAllBlockedAccounts`);
  }

  unblockAccount(accountNumber: number, accountStatus: string, reason: string): Observable<any> {
    const url = `${BASE_URL}/accounts/unblockAccount/${accountNumber}/${accountStatus.toUpperCase()}/${reason}`;
    
    return this.http.get(url);
  }
  
  getBlockedAccountsByCustomerId(customerId: number): Observable<any> {
    return this.http.get(`${BASE_URL}/accounts/getBlockedAccountsByCustomerId/${customerId}`);
  }

  getBlockedAccountsByAccountNumber(accountNumber: number): Observable<any> {
    return this.http.get(`${BASE_URL}/accounts/getBlockedAccountsByAccountNumber/${accountNumber}`);
  }

}