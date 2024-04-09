import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

const BASE_URL = 'http://localhost:1013/api/retailbanking';



@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  constructor(private http: HttpClient) { }

  createTransaction(transactionRequest: any): Observable<any> {
    return this.http.post(BASE_URL + '/transactions/createTransaction', transactionRequest, { headers: this.headers })
  }

  withinSelfAccount(transactionRequestForSelf: any): Observable<any>{
    return this.http.post(BASE_URL + '/transactions/transferWithinSelfAccounts', transactionRequestForSelf, { headers: this.headers })
  }

  withinMyBank(transactionRequestForSelf: any): Observable<any>{
    return this.http.post(BASE_URL + '/transactions/transferWithinAccounts', transactionRequestForSelf, { headers: this.headers })
  }

  getBeneficiary(userId: number): Observable<any> {
    return this.http.get(`${BASE_URL}/transactions/benificiary/${userId}`);
  }

  addBeneficiary(benResponse: any, userId: number): Observable<any> {
    // Constructing the URL with userId as part of the path
    const url = `${BASE_URL}/transactions/benificiary/add/${userId}`;

    // Extracting necessary data from benResponse
    const { accountNumber, name, email } = benResponse;

    // Constructing the query parameters
    let params = new HttpParams()
      .set('accountNumber', accountNumber)
      .set('name', name)
      .set('email', email);

    // Making the HTTP POST request with query parameters
    return this.http.post(url, {}, { headers: this.headers, params: params });
  }

  getYearlyExpenses(accountNumber: number,accountType: string, year: number): Observable<any> {
    const url = `${BASE_URL}/transactions/expense/${accountNumber}/${accountType}/yearly/${year}`;
    return this.http.get<any>(url, { headers: this.headers });
  }

  getMonthlyExpenses(accountNumber: number,accountType: string, month : number): Observable<any> {
    const url = `${BASE_URL}/transactions/expense/${accountNumber}/${accountType}/monthly/${month}`;
    return this.http.get<any>(url, { headers: this.headers });
  }

  getWeeklyExpenses(accountNumber: number,accountType: string): Observable<any> {
    const url = `${BASE_URL}/transactions/expense/${accountNumber}/${accountType}/weekly`;
    return this.http.get<any>(url, { headers: this.headers });
  }

  getTodaysOrYesterdaysExpenses(accountNumber: number,accountType: string,todayOrYesterday: string ): Observable<any> {
    const url = `${BASE_URL}/transactions/expense/${accountNumber}/${accountType}/yesterdayOrToday/${todayOrYesterday}`;
    return this.http.get<any>(url, { headers: this.headers });
  }

  // Method to get the latest transactions
  getLatestTransactions(accountNumber: number, accountType: string): Observable<any> {
    return this.http.get(`${BASE_URL}/transactions/getLatestTransactions/${accountNumber}/${accountType}`);
  }

  // Method to get monthly transactions
  getMonthlyTransactions(accountNumber: number, accountType: string, year: number, month: number): Observable<any> {
    return this.http.get(`${BASE_URL}/transactions/${accountNumber}/${accountType}/monthly/${year}/${month}`);
  }

  // Method to get quarterly transactions
  getQuarterlyTransactions(accountNumber: number, accountType: string, year: number, quarter: number): Observable<any> {
    return this.http.get(`${BASE_URL}/transactions/${accountNumber}/${accountType}/quarterly/${year}/${quarter}`);
  }

  // Method to get yearly transactions
  getYearlyTransactions(accountNumber: number, accountType: string, year: number): Observable<any> {
    return this.http.get(`${BASE_URL}/transactions/${accountNumber}/${accountType}/yearly/${year}`);
  }

  getTransactionBetweenTwoDates(accountNumber: number, accountType: string, startDate :string, endDate :string): Observable<any> {
    return this.http.get(`${BASE_URL}/transactions/between/${accountNumber}/${accountType}/${startDate}/${endDate}`);
  }

  getExpensesByCategory(accountNumber: number, accountType: string, expenseCategory: string): Observable<any>{
    return this.http.get(`${BASE_URL}/transactions/expenses/${accountNumber}/${accountType}/${expenseCategory}`);
  }

  // Get transaction budget
  getBudgetLimit(accountNumber: number): Observable<any>{
    return this.http.get(`${BASE_URL}/transactions/fetchBudgets/accountNumber/${accountNumber}`);
  }

  // Update transaction budget
  updateBudgetLimit(accountNumber: number, budgetSetupDTO : any): Observable<any>{
    return this.http.post(`${BASE_URL}/transactions/setupBudget/accountNumber/${accountNumber}`,budgetSetupDTO);
  }
}