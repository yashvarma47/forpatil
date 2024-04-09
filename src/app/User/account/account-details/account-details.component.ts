import { Component, OnInit } from '@angular/core';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';

@Component({
  selector: 'app-account-details',
  templateUrl: './account-details.component.html',
  styleUrls: ['./account-details.component.css']
})
export class AccountDetailsComponent implements OnInit {

  chooseAcc: string = '';
  chooseAccNo: string = '';
  dropdownOptionschooseAcc: any[] = [];
  dropdownOptionschooseAccNo: any[] = [];

  customerDetails: any = {};
  customerAccounts: any[] = [];

  constructor(private accountService: AccountService, private userService: UserService) {}

  ngOnInit(): void {
    // Retrieve userId from localStorage with a default value of an empty string
    const storedUserId = localStorage.getItem('userId') ?? '';

    // Subscribe to the userId$ observable to get the user ID dynamically
    this.userService.userId$.subscribe(userId => {
      // Convert the user ID to a number before passing it to the service
      const numericUserId = parseInt(userId || storedUserId, 10); // Use storedUserId if userId is not present
      
      // Save userId to localStorage for future reference
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

  updateUI(response: any): void {
    this.customerDetails = response.data;
    this.customerAccounts = response.accounts;
    this.dropdownOptionschooseAcc = [...new Set(this.customerAccounts.map((acc: { accounttype: any }) => acc.accounttype))]
      .map((accountType: any) => ({ value: accountType, label: accountType }));

    if (this.dropdownOptionschooseAcc.length > 0) {
      this.chooseAcc = this.dropdownOptionschooseAcc[0].value;
      this.onAccountTypeChange();
    }
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
        this.chooseAccNo = this.dropdownOptionschooseAccNo[0].value;
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
      this.onAccountNumberChange();
    }
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

      this.customerDetails.accountHolder = `${this.customerDetails.firstName} ${this.customerDetails.lastName}`;
      this.customerDetails.branch = "Kharadi, Pune";
      this.customerDetails.ifscCode = "RBA000123";
      this.customerDetails.accountBalance = selectedAccount.totalbalance;
      this.customerDetails.unclearedFunds = 0;
      this.customerDetails.amountOnHold = 0;
      this.customerDetails.status = selectedAccount.accountstatus;
      this.customerDetails.primaryCard = selectedAccount.primaryCard;
      this.customerDetails.spendingLimit = selectedAccount.spendingLimit;
    }
  }
}

