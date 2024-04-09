// import { Component, OnInit } from '@angular/core';
// import { Router } from '@angular/router';
// import { CustomerService } from 'src/app/Services/customer.service';
// import { AccountService } from 'src/app/Services/account.service';

// @Component({
//   selector: 'app-admin-home',
//   templateUrl: './admin-home.component.html',
//   styleUrls: ['./admin-home.component.css']
// })
// export class AdminHomeComponent implements OnInit {
//   showCustomerDetails = true;
//   showAccountDetails = false;
//   customers: any[] = [];
//   accounts: any[] = [];

//   constructor(private customerService: CustomerService, private accountService: AccountService, private router: Router) {}

//   ngOnInit() {
//     this.loadCustomerData();
//     this.loadAccountData();
//   }

//   onCustomerBtnClick() {
//     this.showCustomerDetails = true;
//     this.showAccountDetails = false;
//     this.loadCustomerData();
//   }

//   onAccountBtnClick() {
//     this.showCustomerDetails = false;
//     this.showAccountDetails = true;
//     this.loadAccountData();
//   }

//   onCreateCustomerClick() {
//     // Redirect to create customer page
//     this.router.navigateByUrl('/create-customer');
//   }

//   onCreateAccountClick() {
//     this.router.navigateByUrl('/open-account');
//   }

//   private loadCustomerData() {
//     this.customerService.getAllCustomer().subscribe(
//       (response) => {
//         if (response.status === 200) {
//           this.customers = response.data;
//         } else {
//           // Handle error
//           console.error('Error fetching customers:', response.message);
//         }
//       },
//       (error) => {
//         // Handle HTTP error
//         console.error('HTTP error while fetching customers:', error);
//       }
//     );
//   }

//   private loadAccountData() {
//     this.accountService.getAllAccounts().subscribe(
//       (response) => {
//         if (response.status === 200) {
//           this.accounts = response.data;
//         } else {
//           // Handle error
//           console.error('Error fetching accounts:', response.message);
//         }
//       },
//       (error) => {
//         // Handle HTTP error
//         console.error('HTTP error while fetching accounts:', error);
//       }
//     );
//   }
//   getAccountStatusColor(status: string): string {
//     switch (status) {
//       case 'ACTIVE':
//         return 'green';
//       case 'BLOCKED':
//         return 'red';
//       case 'CREDITBLOCKED':
//       case 'DEBITBLOCKED':
//         return 'orange';
//       default:
//         return '';
//     }
//   }

// }


import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CustomerService } from 'src/app/Services/customer.service';
import { AccountService } from 'src/app/Services/account.service';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css']
})
export class AdminHomeComponent implements OnInit {
  showCustomerDetails = true;
  showAccountDetails = false;
  customers: any[] = [];
  accounts: any[] = [];
  customerSearchInput: any;
  accountSearchInput: any;
  customersearch: any;
  OpenBlockForm: any;

  constructor(
    private customerService: CustomerService,
    private accountService: AccountService,
    private router: Router
  ) { }

  ngOnInit() {
    this.loadCustomerData();
    this.loadAccountData();
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

  onCreateCustomerClick() {
    // Redirect to create customer page
    this.router.navigateByUrl('/create-customer');
  }

  onCreateAccountClick() {
    this.router.navigateByUrl('/open-account');
  }

  // limitTo11Digits(event: any, inputField: string) {
  //   const input = event.target;
  //   let value = input.value;
 
  //   // Remove non-numeric characters
  //   value = value.replace(/\D/g, '');
 
  //   // Limit to 11 digits
  //   if (value.length > 11) {
  //     value = value.slice(0, 11);
  //   }
 
  //   // Update the input value
  //   input.value = value;
 
  //   // Update the corresponding form control in your Angular form
  //   this.OpenBlockForm.get(inputField)!.setValue(value);
  // }
  
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
