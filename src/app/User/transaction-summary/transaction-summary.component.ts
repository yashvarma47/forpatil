import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import { TransactionService } from 'src/app/Services/transaction.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';

import autoTable from 'jspdf-autotable';
import { jsPDF } from 'jspdf';
import 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import Swal from 'sweetalert2';

export interface Transaction {
  id: number;
  customerId: number;
  accountNumber: number;
  transactionId: string;
  transactionAmount: number;
  transactionType: string;
  remainingBalance: number;
  transactionTime: any;
  recipient: number;
  transactionCategory: string
  narratives: string;
  
}
@Component({
  selector: 'app-transaction-summary',
  templateUrl: './transaction-summary.component.html',
  styleUrls: ['./transaction-summary.component.css'],
  providers: [DatePipe]
})
export class TransactionSummaryComponent implements OnInit {


  displayedColumns: string[] = ['transactionTime', 'narratives', 'transactionId', 'withdrawal', 'deposit', 'remainingBalance'];

  accountOptions = [
    { value: 'savings', display: 'Savings Account' },
    { value: 'current', display: 'Current Account' },
  ];
  transactionList: any = [];
  // data: Transaction[] = [];
  dataSource = this.transactionList;
  formattedAmount: string = '';
  balanceAmount: number = 0;
  dataAfterDecimal: string = '';
  chooseAcc: string = '';
  chooseAccNo: string = '';
  noData: boolean = false;
  dropdownOptionschooseAcc: any[] = [];
  dropdownOptionschooseAccNo: any[] = [];
  customerAccounts: any[] = [];
  customerDetails: any = {};
  selectedAccountType: string = "";
  selectedAccount: string = "";
  showBalance: boolean = false;
  transactionFilter: string = 'Recent';
  transactionFilterOptions: string[] = ['Recent', 'Monthly', 'Quarterly', 'Yearly'];
  todayDate: Date = new Date();
  year: number = this.todayDate.getFullYear();
  month: number = this.todayDate.getMonth() + 1;
  quarter: number = Math.ceil(this.month / 3);
  showOptions: boolean = false;
  statementData: any[] = [];
  startDate: string = ""; // Property to store the start date
  endDate: string = ""; // Property to store the end date

  constructor(private cdr: ChangeDetectorRef, private formBuilder: FormBuilder, private transactionService: TransactionService, private datePipe: DatePipe, private accountService: AccountService, private userService: UserService) { 
    // const currentDate = new Date();
    
    // // Set start date to the 1st of the current month
    // currentDate.setDate(1);

    // this.startDate = this.datePipe.transform(currentDate, 'yyyy-MM-dd') as string;
    // this.endDate = this.datePipe.transform(new Date(), 'yyyy-MM-dd') as string;
  }

  getCurrentDate(): string {
    const currentDate = new Date();
    return currentDate.toISOString().split('T')[0];
  }

  getMinDate(): string {
    const currentDate = new Date();
    currentDate.setMonth(currentDate.getMonth() - 6);
    return currentDate.toISOString().split('T')[0];
  }

  getMaxDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }

  onEndDateSelected() {
    // 
    this.filterTransactions();
  }

  formatDate(date: number[] | null): string {
    if (date) {
      const formattedDate = new Date(date[0], date[1] - 1, date[2]);

      return this.datePipe.transform(formattedDate, 'dd/MM/yyyy') || '';
    } else {
      return '';
    }
  }

  getDepositAmount(transaction: Transaction): string {
    return transaction.transactionType.toLowerCase() === 'credit' ? transaction.transactionAmount.toFixed(2) : '-';
  }

  getWithdrawalAmount(transaction: Transaction): string {
    return transaction.transactionType.toLowerCase() === 'debit' ? transaction.transactionAmount.toFixed(2) : '-';
  }

  formatAmount(amount: number) {
    const truncatedAmount = Math.floor(amount);
    return truncatedAmount.toString();
}
formatAmountremainingbalance(amount: number){
  return amount.toFixed(2);

}

  getDigitsAfterDecimal(amount: number): string {
    const numberString = amount.toFixed(2); 
    const splitNumber = numberString.split('.');
    if (splitNumber.length === 2) {
      return splitNumber[1];
    } else {
      return '00';
    }
  }

  DownloadStatement(): void {
    
    this.showOptions = !this.showOptions;
    this.cdr.detectChanges();
  }

  // downloadPDF() {
  //   const pdf = new jsPDF();
  //   const header = [['Date', 'Narration', 'Check/Ref No.', 'Withdrawal', 'Deposit', 'Closing Balance']];
  //   const data = this.statementData.map(item => [this.formatDate(item.transactionTime), item.narratives, item.transactionId, this.getWithdrawalAmount(item), this.getDepositAmount(item), item.remainingBalance]);

  //   pdf.text('Account Statement', 15, 10);
  //   pdf.text('Account Number: ' + this.chooseAccNo, 115, 10);

  //   autoTable(pdf, {
  //     head: header,
  //     body: data,
  //     startY: 20
  //   });

  //   const fileName = this.statementData.length > 0 ? this.statementData[0].transactionId : 'transactions';

  //   pdf.save(`${this.chooseAccNo}.pdf`);
  //   this.showOptions = false;
  // }

  downloadPDF() {
    if (this.statementData.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'No transaction data available for download.',
        showConfirmButton: false,
        timer: 1500,
      });
      return;
    }
  
    const pdf = new jsPDF();
    const header = [['Date', 'Narration', 'Check/Ref No.', 'Withdrawal', 'Deposit', 'Closing Balance']];
    const data = this.statementData.map(item => [this.formatDate(item.transactionTime), item.narratives, item.transactionId, this.getWithdrawalAmount(item), this.getDepositAmount(item), item.remainingBalance]);
  
    pdf.text('Account Statement', 15, 10);
    pdf.text('Account Number: ' + this.chooseAccNo, 115, 10);
   
    autoTable(pdf, {
      head: header,
      body: data,
      startY: 20
    });
  
    const fileName = this.statementData.length > 0 ? this.statementData[0].transactionId : 'transactions';
  
    pdf.save(`${this.chooseAccNo}.pdf`);
    this.showOptions = false;
  }
  

  downloadCSV() {
    if (this.statementData.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'No transaction data available for download.',
        showConfirmButton: false,
        timer: 1500,
      });
      return;
    }
    const csvData = this.convertToCSV(this.statementData);
    const blob = new Blob([csvData], { type: 'text/csv;charset=utf-8' });

    //fetch value from the API response to create the file name dynamically
    const fileName = this.statementData.length > 0 ? this.statementData[0].transactionId : 'transactions';

    saveAs(blob, `${fileName}.csv`);
    this.showOptions = false;
  }

  downloadXLS() {
    if (this.statementData.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'No transaction data available for download.',
        showConfirmButton: false,
        timer: 1500,
      });
      return;
    }
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.statementData);
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Transactions');

    // Use the xlsx library to create an ArrayBuffer
    const arrayBuffer: ArrayBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });

    // Create a Blob from the ArrayBuffer
    const blob: Blob = new Blob([arrayBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

    //fetch value from the API response to create the file name dynamically
    const fileName = this.statementData.length > 0 ? this.statementData[0].transactionId : 'transactions';

    // Use FileSaver.js to save the Blob as a file
    saveAs(blob, `${fileName}.xlsx`);
    this.showOptions = false;
  }

  private convertToCSV(data: any[]): string {
    const header = Object.keys(data[0]).join(',');
    const rows = data.map(item => Object.values(item).join(','));
    return `${header}\n${rows.join('\n')}`;
  }

  filterTransactions(): void {
    this.noData = false;
    if (this.chooseAccNo && this.chooseAcc) {
      const accountNumber = this.chooseAccNo;
      const accountType = this.chooseAcc;
      
 
      this.transactionService.getTransactionBetweenTwoDates(parseInt(accountNumber), accountType, this.startDate, this.endDate)
        .subscribe((response: any) => {
          if (response.status === 200) {
            if (response.status === 204 && response.error === 'No Data Found') {
              this.statementData = [];
              this.transactionList = [];
              this.dataSource = new MatTableDataSource<Transaction>(this.transactionList);
 
              
            } else {
              
              const responseData: any[] = response.data;
              this.statementData = responseData;
 
              this.transactionList = responseData.map(item => ({
                id: item.id,
                customerId: item.customerId,
                accountNumber: item.accountNumber,
                transactionId: item.transactionId,
                transactionAmount: item.transactionAmount,
                transactionType: item.transactionType,
                remainingBalance: item.remainingBalance,
                transactionTime: item.transactionTime,
                recipient: item.recipient,
                transactionCategory: item.transactionCategory,
                narratives: item.narratives
              }));
 
              this.dataSource = new MatTableDataSource<Transaction>(this.transactionList); // Update data source
            }
          }else if(response.status ===204) {
            this.statementData = [];
            this.dataSource = [];
            this.transactionList = [];
            this.noData = true;
          }
        }
          , (error: any) => {
            console.error(error);
            Swal.fire({
              icon: 'error',
              title: 'Internal Server Error',
              showConfirmButton: false,
              timer: 1500,
            });
          });
 
 
    } else {
            Swal.fire({
              icon: 'error',
              title: 'Selected account or account type is missing',
              showConfirmButton: false,
              timer: 1500,
            });
      console.error('Selected account or account type is missing');
    }
  }


  ngOnInit(): void {
    const storedUserId = localStorage.getItem('userId') ?? '';

    this.userService.userId$.subscribe(userId => {
      const numericUserId = parseInt(userId || storedUserId, 10);

      localStorage.setItem('userId', numericUserId.toString());

      this.accountService.getCustomerDetails(numericUserId).subscribe(
        (response: any) => {
          if (response && response.statusCode === 200) {
            this.updateUI(response);
            this.setDefaultSelectedAccount();
            this.selectedAccountType = this.chooseAcc === "CURRENT" ? "CURRENTS" : this.chooseAcc;
            if (this.selectedAccount && this.selectedAccountType) {
              this.selectedAccountType = this.chooseAcc === "CURRENT" ? "CURRENTS" : this.chooseAcc;
              this.filterRecentTransactions();
            } else {
            Swal.fire({
              icon: 'error',
              title: 'Selected account or account type is missing',
              showConfirmButton: false,
              timer: 1500,
            });
              console.error('Selected account or account type is missing');
            }
          } else {
            console.error('API Error:', response && response.statusMessage);
            Swal.fire({
              icon: 'error',
              title: 'Internal Server Error',
              showConfirmButton: false,
              timer: 1500,
            });
          }
        },
        (error) => {
          console.error('API Error:', error);
            Swal.fire({
              icon: 'error',
              title: 'Internal Server Error',
              showConfirmButton: false,
              timer: 1500,
            });
        }
      );
    });
    this.formattedAmount = this.formatAmount(this.balanceAmount);
  }
  onFilterChange(): void {
    if (this.transactionFilter === 'Recent') {
      this.filterRecentTransactions();
    } else if (this.transactionFilter === 'Monthly') {
      this.filterMonthlyTransactions();
    } else if (this.transactionFilter === 'Yearly') {
      this.filterYearlyTransactions()
    } else if (this.transactionFilter === 'Quarterly') {
      this.filterQuarterlyTransactions()
    }
  }

  filterRecentTransactions(): void {
    this.noData = false;

    if (this.chooseAccNo && this.chooseAcc) {
      const accountNumber = this.chooseAccNo;
      const accountType = this.chooseAcc;
      

      this.transactionService.getLatestTransactions(parseInt(accountNumber), accountType)
        .subscribe((response: any) => {
          if(response.status ===200){
            const responseData: any[] = response.data;
          this.statementData = responseData;

          this.transactionList = responseData.map(item => ({
            id: item.id,
            customerId: item.customerId,
            accountNumber: item.accountNumber,
            transactionId: item.transactionId,
            transactionAmount: item.transactionAmount,
            transactionType: item.transactionType,
            remainingBalance: item.remainingBalance,
            transactionTime: item.transactionTime,
            recipient: item.recipient,
            transactionCategory: item.transactionCategory,
            narratives: item.narratives
          }));

          
          

          
          this.dataSource = this.transactionList;
          }else if(response.status ===204) {
            this.statementData = [];
            this.dataSource = [];
            this.transactionList = [];
            this.noData = true;
          }
          
        }, (error: any) => {
          console.error(error);
          Swal.fire({
            icon: 'error',
            title: 'Internal Server Error',
            showConfirmButton: false,
            timer: 1500,
          });
        });
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Selected account or account type is missing',
        showConfirmButton: false,
        timer: 1500,
      });
      console.error('Selected account or account type is missing');
    }
  }

  filterMonthlyTransactions(): void {
    this.noData = false;

    if (this.chooseAccNo && this.chooseAcc) {
      const accountNumber = this.chooseAccNo;
      const accountType = this.chooseAcc;
      

      this.transactionService.getMonthlyTransactions(parseInt(accountNumber), accountType, this.year, this.month)
        .subscribe((response: any) => {
          if(response.status ===200){
          
          const responseData: any[] = response.data;
          this.statementData = responseData;

          this.transactionList = responseData.map(item => ({
            id: item.id,
            customerId: item.customerId,
            accountNumber: item.accountNumber,
            transactionId: item.transactionId,
            transactionAmount: item.transactionAmount,
            transactionType: item.transactionType,
            remainingBalance: item.remainingBalance,
            transactionTime: item.transactionTime,
            recipient: item.recipient,
            transactionCategory: item.transactionCategory,
            narratives: item.narratives
          }));
          this.dataSource = this.transactionList;
        }else if(response.status ===204) {
          this.statementData = [];
          this.dataSource = [];
          this.transactionList = [];
          this.noData = true;

        }
        }, (error: any) => {
          console.error(error);
          Swal.fire({
            icon: 'error',
            title: 'Internal Server Error',
            showConfirmButton: false,
            timer: 1500,
          });
        });
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Selected account or account type is missing',
        showConfirmButton: false,
        timer: 1500,
      });      
      console.error('Selected account or account type is missing');
    }
  }

  filterYearlyTransactions(): void {
    this.noData = false;

    if (this.chooseAccNo && this.chooseAcc) {
      const accountNumber = this.chooseAccNo;
      const accountType = this.chooseAcc;
      

      this.transactionService.getYearlyTransactions(parseInt(accountNumber), accountType, this.year)
        .subscribe((response: any) => {
          if(response.status === 200) {
          
          const responseData: any[] = response.data;
          this.statementData = responseData;

          this.transactionList = responseData.map(item => ({
            id: item.id,
            customerId: item.customerId,
            accountNumber: item.accountNumber,
            transactionId: item.transactionId,
            transactionAmount: item.transactionAmount,
            transactionType: item.transactionType,
            remainingBalance: item.remainingBalance,
            transactionTime: item.transactionTime,
            recipient: item.recipient,
            transactionCategory: item.transactionCategory,
            narratives: item.narratives
          }));

          this.dataSource = this.transactionList;
        }
        else if(response.status ===204) {
          this.statementData = [];
          this.dataSource = [];
          this.transactionList = [];
          this.noData = true;

        }
        }, (error: any) => {
          console.error(error);
          Swal.fire({
            icon: 'error',
            title: 'Internal Server Error',
            showConfirmButton: false,
            timer: 1500,
          });
        });
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Selected account or account type is missing',
        showConfirmButton: false,
        timer: 1500,
      });
      console.error('Selected account or account type is missing');
    }
  }

  filterQuarterlyTransactions(): void {
    this.noData = false;

    if (this.chooseAccNo && this.chooseAcc) {
      const accountNumber = this.chooseAccNo;
      const accountType = this.chooseAcc;
      

      this.transactionService.getQuarterlyTransactions(parseInt(accountNumber), accountType, this.year, this.quarter)
        .subscribe((response: any) => {
          if(response.status === 200){
          
          const responseData: any[] = response.data;
          this.statementData = responseData;

          this.transactionList = responseData.map(item => ({
            id: item.id,
            customerId: item.customerId,
            accountNumber: item.accountNumber,
            transactionId: item.transactionId,
            transactionAmount: item.transactionAmount,
            transactionType: item.transactionType,
            remainingBalance: item.remainingBalance,
            transactionTime: item.transactionTime,
            recipient: item.recipient,
            transactionCategory: item.transactionCategory,
            narratives: item.narratives
          }));
          this.dataSource = this.transactionList;
        }
        else if(response.status ===204) {
          this.statementData = [];
          this.dataSource = [];
          this.transactionList = [];
          this.noData = true;

        }
        }, (error: any) => {
          console.error(error);
          Swal.fire({
            icon: 'error',
            title: 'Internal Server Error',
            showConfirmButton: false,
            timer: 1500,
          });
        });
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Selected account or account type is missing',
        showConfirmButton: false,
        timer: 1500,
      });
      console.error('Selected account or account type is missing');
    }
  }

  updateSelectedAccount(): void {
    this.selectedAccount = this.chooseAccNo;
    this.selectedAccountType = this.chooseAcc === "CURRENT" ? "CURRENTS" : this.chooseAcc;;
  }


  updateUI(response: any): void {
    this.customerDetails = response.data;
    this.customerAccounts = response.accounts;
    this.dropdownOptionschooseAcc = [...new Set(this.customerAccounts.map((acc: { accounttype: any }) => acc.accounttype))]
      .map((accountType: any) => ({ value: accountType, label: accountType }));

    if (this.dropdownOptionschooseAcc.length > 0) {
      this.chooseAcc = this.dropdownOptionschooseAcc[0].value;
      this.balanceAmount = response.accounts[0].totalbalance;
      

      this.onAccountTypeChange();
    }
  }

  setDefaultSelectedAccount(): void {
    if (this.dropdownOptionschooseAcc.length > 0 && this.dropdownOptionschooseAccNo.length > 0) {
      this.chooseAcc = this.dropdownOptionschooseAcc[0].value;
      this.chooseAccNo = this.dropdownOptionschooseAccNo[0].value;
    
      this.updateSelectedAccount();
    }
  }
  onViewClick() {
    this.showBalance = true;
    
    

    this.updateSelectedAccount();
    this.selectedAccountType = this.chooseAcc === "CURRENT" ? "CURRENTS" : this.chooseAcc;

    this.filterRecentTransactions();
  }

  onAccountTypeChange(): void {
    
    if (this.chooseAcc) {
      const filteredAccounts = this.customerAccounts.filter(
        (acc: { accounttype: string }) => acc.accounttype === this.chooseAcc
      );

      this.dropdownOptionschooseAccNo = filteredAccounts.map((acc: { accountnumber: any }) => ({
        value: acc.accountnumber,
        label: acc.accountnumber.toString(),
      }));


      if (this.dropdownOptionschooseAccNo.length > 0) {
        this.chooseAccNo = this.dropdownOptionschooseAccNo[0].value === "CURRENT" ? "CURRENTS" : this.chooseAcc;;
        this.getSelectedAccount(this.chooseAccNo);
        this.onAccountNumberChange();
      }
    }
  }


  onSelectAccountNo(event: any): void {
    const selectedIndex = (event.target as HTMLSelectElement).selectedIndex;

    if (this.dropdownOptionschooseAccNo.length > 0) {
      this.chooseAccNo = this.dropdownOptionschooseAccNo[selectedIndex].value;
      const selectedAccount = this.getSelectedAccount(this.chooseAccNo);
      this.updateAccountDetails(selectedAccount);
      this.handleAccountSelection();
    }
  }

  onSelectTimePeriod() {
  }

  private handleAccountSelection(): void {
    this.updateSelectedAccount();
    this.selectedAccountType = this.chooseAcc === "CURRENT" ? "CURRENTS" : this.chooseAcc;
    this.filterRecentTransactions();
  }

  onAccountNumberChange(): void {
    if (this.chooseAccNo) {
      const selectedAccount = this.getSelectedAccount(this.chooseAccNo);
      this.updateAccountDetails(selectedAccount);
    }
  }

  getSelectedAccount(accountNumber: string): any {
    return this.customerAccounts.find((acc: { accountnumber: string }) => acc.accountnumber === accountNumber);
  }

  updateAccountDetails(selectedAccount: any): void {
    if (selectedAccount) {
      this.balanceAmount = selectedAccount.totalbalance;
    }
  }
}
