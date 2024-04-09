import { Component, OnInit } from '@angular/core';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import Swal from 'sweetalert2';
 
@Component({
  selector: 'app-account-closure',
  templateUrl: './account-closure.component.html',
  styleUrls: ['./account-closure.component.css']
})
export class AccountClosureComponent implements OnInit {
 
  selectOptionTo: any[] = [];
  selectOptionAccType: any[] = [];
  selectOptionFunds: string = 'Select';
  customerDetails: any = {};
  recipientAccountNumber: number = 0;
  recipientName: string = '';
  confirmAndProceed: boolean = false;
  customerAccounts: any[] = [];
  chooseAcc: string = '';
  chooseAccNo: string = '';
  feedback: string = '';
  balanceAmount: number = 0;
 
  ngOnInit(): void {
    const storedUserId = localStorage.getItem('userId') ?? '';
 
    this.userService.userId$.subscribe(userId => {
      const numericUserId = parseInt(userId || storedUserId, 10);
 
      localStorage.setItem('userId', numericUserId.toString());
 
      this.accountService.getCustomerDetails(numericUserId).subscribe(
        (response: any) => {
          if (response && response.statusCode === 200) {
            this.updateUI(response);
          } else {
            console.error('API Error:', response && response.statusMessage);
          }
        },
        (error) => {
          console.error('API Error:', error);
        }
      );
    });
  }
 
 
  constructor(private accountService: AccountService, private userService: UserService) { }
 
  updateUI(response: any): void {
    this.customerDetails = response.data;
    this.customerAccounts = response.accounts;
    this.selectOptionAccType = [...new Set(this.customerAccounts.map((acc: { accounttype: any }) => acc.accounttype))]
      .map((accountType: any) => ({ value: accountType, label: accountType }));
 
    if (this.selectOptionAccType.length > 0) {
      this.chooseAcc = this.selectOptionAccType[0].value;
      this.onAccountTypeChange();
    }
  }
 
  onAccountTypeChange(): void {
    if (this.chooseAcc) {
      const filteredAccounts = this.customerAccounts.filter(
        (acc: { accounttype: string }) => acc.accounttype === this.chooseAcc
      );
 
      this.selectOptionTo = filteredAccounts.map((acc: { accountnumber: any }) => ({
        value: acc.accountnumber,
        label: acc.accountnumber.toString(),
      }));
 
      if (this.selectOptionTo.length > 0) {
        this.chooseAccNo = this.selectOptionTo[0].value;
        this.getSelectedAccount(this.chooseAccNo);
        this.onAccountNumberChange();
      }
    }
  }
 
  onSelectAccountNo(event: any): void {
    const selectedIndex = (event.target as HTMLSelectElement).selectedIndex;
 
    if (this.selectOptionTo.length > 0) {
      this.chooseAccNo = this.selectOptionTo[selectedIndex].value;
      const selectedAccount = this.getSelectedAccount(this.chooseAccNo);
      this.updateAccountDetails(selectedAccount);
      this.onAccountNumberChange();
    }
  }
  getSelectedAccount(accountNumber: string): any {
    return this.customerAccounts.find((acc: { accountnumber: string }) => acc.accountnumber === accountNumber);
  }
 
  onAccountNumberChange(): void {
    if (this.chooseAccNo) {
      const selectedAccount = this.getSelectedAccount(this.chooseAccNo);
      this.updateAccountDetails(selectedAccount);
    }
  }
 
  updateAccountDetails(selectedAccount: any): void {
    if (selectedAccount) {
 
      // this.customerDetails.accountHolder = `${this.customerDetails.firstName} ${this.customerDetails.lastName}`;
      // this.customerDetails.branch = "Kharadi, Pune";
      // this.customerDetails.ifscCode = "RBA000123";
      this.balanceAmount = selectedAccount.totalbalance;
      
 
      // this.customerDetails.unclearedFunds = 0;
      // this.customerDetails.amountOnHold = 0;
      // this.customerDetails.status = selectedAccount.accountstatus;
      // this.customerDetails.primaryCard = selectedAccount.primaryCard;
      // this.customerDetails.spendingLimit = selectedAccount.spendingLimit;
    }
  }
 
  dropdownOptionsFunds = [
    { value: 'Withdraw as Cash/Cheque', label: 'Withdraw as Cash/Cheque' },
    { value: 'Transfer to other Account', label: 'Transfer to other Account' },
    { value: 'Transfer to other Customer', label: 'Transfer to other Customer' },
    // Add more options as needed
  ];
 
  submitbtn(event: Event): void {
    const ticketDTO = {
      "ticketType": "CLOSURE",
      "customerId": localStorage.getItem('userId') ?? '',
      "accountNumber": this.chooseAccNo,
      "accountType": this.chooseAcc,
      "recipientAccountNumber": this.recipientAccountNumber,
      "ticketStatus": "OPEN", // Assuming the initial status is new
      "transferFund": this.selectOptionFunds, // Assuming no fund transfer is involved
      "feedback": this.feedback
    }
    
    
 
    this.accountService.createTicket(ticketDTO).subscribe(
      (response: any) => {
       
        
        if (response.status === 201) {
          const ticketId = response.data.ticketId;
          Swal.fire({
            icon: 'success',
            title: 'Ticket with ticketId ' + ticketId + ' created successfully!',
            showConfirmButton: false,
            timer: 3000,
          });
        }
      },
      (error) => {
        console.error('Error submitting feedback:', error);
        Swal.fire({
          icon: 'error',
          title: 'A ticket already exists for the given customer and account details with status other than "Rejected". Cannot create a new ticket.',
          showConfirmButton: false,
          timer: 10000,
        });
      }
    );
    event.stopPropagation();
    event.preventDefault();
 
  }
  limitTo11Digits(event: any) {
    const input = event.target;
    let value = input.value;
 
    // Remove non-numeric characters
    value = value.replace(/\D/g, '');
 
    // Limit to 8 digits
    if (value.length > 11) {
      value = value.slice(0, 11);
    }
 
    // Update the input value
    input.value = value;
 
    // Update the form control value
  }
 
 
}