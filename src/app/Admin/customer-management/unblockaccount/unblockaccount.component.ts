import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AccountService } from 'src/app/Services/account.service';
import { CustomerService } from 'src/app/Services/customer.service';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
 
@Component({
  selector: 'app-unblockaccount',
  templateUrl: './unblockaccount.component.html',
  styleUrls: ['./unblockaccount.component.css']
})
export class UnblockaccountComponent implements OnInit {

  errormsg: String = '';
  showCustomerDetails = true ;
  showAccountDetails = true;
  showAllBlockAccountDetails= true;
  showCustomerBlockAccountDetails=true;

  customers: any[] = [];
  accounts: any[] = [];
  customerSearchInput: any;
  accountSearchInput: any;
  customersearch: any;
  OpenBlockForm: any;
choose: any;
dropdownOptionschoose: any;
selectedOption: string = 'Account Number'; // Default option
 
  constructor(
    private customerService: CustomerService,
    private accountService: AccountService,
    private router: Router
  ) { }
 
 
  onDropdownoptions() {
    
  this.customerSearchInput = ''; // Reset the input field
  this.onSearchChange(); // Update the view based on the new option
  }
 
 
  ngOnInit() {
    //this.loadCustomerData();
    //this.loadAccountData();

     // Fetch blocked accounts only if "Account Number" option is selected initially
    //  if (this.selectedOption === 'Account Number') {
    this.getBlockedAccounts();
    // }
  }

  onSearchChange() {
    
    if (this.selectedOption === 'Customer ID' && this.customerSearchInput && this.customerSearchInput.toString().length === 8) {
      this.accountService.getBlockedAccountsByCustomerId(this.customerSearchInput).subscribe(
        response => {
          if (response.status === 200) {
            this.accounts = response.data;
          } else {
            console.error('Error:', response.message);
          }
        },
        error => {
          console.error('API Error:', error);
        }
      );
    } else if (this.selectedOption === 'Account Number' && this.customerSearchInput && this.customerSearchInput.toString().length === 11) {
     
      this.accountService.getBlockedAccountsByAccountNumber(this.customerSearchInput).subscribe(
        response => {
          if (response.status === 200) {
            this.accounts = [response.accountDetails];
            
          } else {
            console.error('Error:', response.message);
            this.accounts = [];
            this.errormsg = "No records found"
          }
        },
        error => {
          if(error.error.status === 302){
            this.accounts = [];
          }
          console.error('API Error:', error);
        }
      );
    } else {
      this.getBlockedAccounts();
    }
    
  }

  getBlockedAccounts() {
    this.accountService.getAllBlockedAccounts().subscribe(
      response => {
        if (response.status === 200) {
          this.accounts = response.data;
        } else {
          console.error('Error:', response.message);
        }
      },
      error => {
        console.error('API Error:', error);
      }
    );
  }
 
  onCustomerBtnClick() {
    this.showCustomerDetails = true;
    this.showAccountDetails = false;
    this.loadCustomerData();
  }
 
  onAccountBtnClick() {
    this.showCustomerDetails = false;
    this.showAccountDetails = true;
    this.loadAccountData();
  }
 
 
 
  onBlockAccountClick() {
    this.router.navigateByUrl('/block-account');
  }
 
 
  onUnblockAccount(account: any) {
    // Add your logic to unblock the account here
    //this.router.navigateByUrl('/unblockpopup');
    this.router.navigate(['/unblockpopup', { accountNumber: account.accountnumber, accountStatus: account.accountstatus }]);
  
    
  }
 
 
  private loadCustomerData() {
    // Call the customer service method to get customer data
    if (this.customerSearchInput) {
      // If search input is present, fetch data based on search
      this.customerService.getCustomerDetails(this.customerSearchInput).subscribe(
        (response) => {
          if (response.status === 200) {
            this.customers = [response.data]; // Assuming the response is a single customer, modify as needed
          } else {
            // Handle error
            console.error('Error fetching customers:', response.message);
          }
        },
        (error) => {
          // Handle HTTP error
          console.error('HTTP error while fetching customers:', error);
        }
      );
    } else {
      // If no search input, fetch all customers
      this.customerService.getAllCustomer().subscribe(
        (response) => {
          if (response.status === 200) {
            this.customers = response.data;
          } else {
            // Handle error
            console.error('Error fetching customers:', response.message);
          }
        },
        (error) => {
          // Handle HTTP error
          console.error('HTTP error while fetching customers:', error);
        }
      );
    }
  }
 
  private loadAccountData() {
    // Call the account service method to get account data
    if (this.accountSearchInput) {
      // If search input is present, fetch data based on search
      this.accountService.getAccountDetails(this.accountSearchInput).subscribe(
        (response) => {
          if (response) {
            this.accounts = [response]; // Assuming the response is a single account, modify as needed
          } else {
            // Handle error
            console.error('Error fetching accounts:', response ? response.message : 'Unknown error');
          }
        },
        (error) => {
          // Handle HTTP error
          console.error('HTTP error while fetching accounts:', error);
        }
      );
    } else {
      // If no search input, fetch all accounts
      this.accountService.getAllAccounts().subscribe(
        (response) => {
          if (response.status === 200) {
            this.accounts = response.data;
          } else {
            // Handle error
            console.error('Error fetching accounts:', response.message);
          }
        },
        (error) => {
          // Handle HTTP error
          console.error('HTTP error while fetching accounts:', error);
        }
      );
    }
  }
 
  // Handle customer search input changes
  onCustomerSearchChange() {
    // You may add a validation check for the minimum length if required
    if (this.customerSearchInput && this.customerSearchInput.toString().length === 8) {
      this.loadCustomerData();
    }
 
    // this.loadCustomerData();
  }
 
  // Handle account search input changes
  onAccountSearchChange() {
    // You may add a validation check for the minimum length if required
    if (this.accountSearchInput && this.accountSearchInput.toString().length === 11) {
      this.loadAccountData();
    }
    // this.loadAccountData();
  }
 
 
 
 
 
  // Handle color based on account status
  getAccountStatusColor(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'green';
      case 'BLOCKED':
        return 'red';
      case 'CREDITBLOCKED':
      case 'DEBITBLOCKED':
        return 'orange';
      default:
        return '';
    }
  }
}
 