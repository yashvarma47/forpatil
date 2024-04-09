import { Component, OnInit } from '@angular/core';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import { TransactionService } from 'src/app/Services/transaction.service';
import { FormBuilder, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
 
@Component({
  selector: 'app-transfer-within-my-account',
  templateUrl: './transfer-within-my-account.component.html',
  styleUrls: ['./transfer-within-my-account.component.css']
})
export class TransferWithinMyAccountComponent implements OnInit {
 
  dropdownOptionsFrom: any[] = [];
  dropdownOptionsTo: any[] = [];
  customerDetails: any;
  customerAccounts: any;
  selectedFromAccount: string = '';
  selectedToAccount: string = '';
  selectOptionTo: any;
  selectOptionFrom: any;
  transferForm: any;
 
 
  constructor(private accountService: AccountService, private fb: FormBuilder, private userService: UserService, private transactionService: TransactionService) { }
 
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
    // Initialize the form group
    this.transferForm = this.fb.group({
      fromAccount: [null, Validators.required],
      toAccount: [null, Validators.required],
      currencyAmount: ['INR', Validators.required],
      transferAmount: [null, [Validators.required, Validators.min(0)]],
      transactionNote: [''],
      budgetTag: ['Select'],
      scheduleTransfer: [''],
    });
  }
 
  updateUI(response: any): void {
    this.customerDetails = response.data;
    this.customerAccounts = response.accounts;
 
    // Populate "From Account" dropdown
    this.dropdownOptionsFrom = this.customerAccounts.map((acc: { accountnumber: any }) => ({
      value: acc.accountnumber,
      label: acc.accountnumber.toString(),
    }));
 
    // Set default selection for "From Account"
    if (this.dropdownOptionsFrom.length > 0) {
      this.selectedFromAccount = this.dropdownOptionsFrom[0].value;
    }
 
    // Populate "To Account" dropdown excluding the selected "From Account"
    this.updateToAccounts();
 
    // Other UI update logic...
  }
 
  updateToAccounts(): void {
    this.dropdownOptionsTo = this.customerAccounts
      // .filter((acc: { accountnumber: any }) => acc.accountnumber != this.selectedFromAccount)
      .map((acc: { accountnumber: any }) => ({
        value: acc.accountnumber,
        label: acc.accountnumber.toString(),
      }));
 
    // Set default selection for "To Account"
    if (this.dropdownOptionsTo.length > 0) {
      this.selectedToAccount = this.dropdownOptionsTo[0].value;
    }
  }
 
  onFromAccountChange(): void {
    // Update "To Account" dropdown when "From Account" changes
    this.updateToAccounts();
  }
  // onChange(){
  // 
  // }
 
  isValidAmount(): boolean {
    const amountControl = this.transferForm.get('transferAmount');
    if (!amountControl || amountControl.value === null || amountControl.value <= 0) return false;
 
    const amountString: string = amountControl.value.toString();
    return /^\d+(\.\d{1,2})?$/.test(amountString);
}

  cancelbtn(event: Event): void {
    
    event.stopPropagation();
    event.preventDefault();

    // Reset the form
    this.transferForm.reset({
      fromAccount: null,
      toAccount: null,
      currencyAmount: 'INR',
      transferAmount: null,
      transactionNote: '',
      budgetTag: 'Select',
      scheduleTransfer: ''
    });
}
 
  transferbtn(event: Event): void {
    
    // event.stopPropagation();
    // event.preventDefault();
    
    
    
    
    
 
    // Check if the form is defined and valid
    if (this.transferForm && this.transferForm.valid) {

      // Check if budgetTag is null, undefined, or 'Select'
      const budgetTagValue = this.transferForm.value.budgetTag === 'Select' ? 'miscellaneous' : this.transferForm.value.budgetTag;

      // Prepare the request object
      const transactionRequestForSelf = {
        fromAccountNumber: parseInt(this.transferForm.value.fromAccount),
        toAccountNumber: parseInt(this.transferForm.value.toAccount),
        transferAmount: parseFloat(this.transferForm.value.transferAmount),
        transactionNote: this.transferForm.value.transactionNote,
        transactionCategory: budgetTagValue,
      };
      
  
  
 
      // Call the transaction service
      this.transactionService.withinSelfAccount(transactionRequestForSelf).subscribe(
        (response: any) => {
          if (response && response.status === 200) {
            
            Swal.fire({
              icon: 'success',
              title: 'Transaction Successfull!',
              showConfirmButton: false,
              timer: 3000,
            });
            // Handle the successful transaction here if needed
          } 
         else if(response.faultCode === "ERR-RBA-100C&DB-BENF"){
            console.error('Transaction Failed:', response.message);
            Swal.fire({
              icon: 'error',
              title: "Transaction Failed!\nThe sender's account is blocked for all type of transactions (CREDIT/DEBIT)!",
              showConfirmButton: true,
              // timer: 3000,
            });
            // Handle the failed transaction here if needed
          }
          else{
            Swal.fire({
              icon: 'error',
              title: "Transaction Failed!\nEnter Valid Details",
              showConfirmButton: true,
              // timer: 3000,
            });
          }
        },
        (error) => {
          console.error('API Error:', error);
          if(error.error.faultCode === "ERR-RBA-1001-SEND"){
            Swal.fire({
              icon: 'error',
              title: 'You have Insufficient Balance to perform the transaction!',
              showConfirmButton: true,
              // timer: 3000,
            });
          }  else if(error.error.faultCode === "ERR-RBA-1006"){
            Swal.fire({
              icon: 'error',
              title: 'Transaction Failed!\nAccount details not found!',
              showConfirmButton: true,
              // timer: 3000,
            });
          }else if(error.error.faultCode === "ERR-RBA-100C&DB-SEND"){
            Swal.fire({
              icon: 'error',
              title: "Transaction Failed!\nThe sender's account is blocked for all type of transactions (CREDIT/DEBIT)!",
              showConfirmButton: true,
              // timer: 3000,
            });
          } else if(error.error.faultCode === "ERR-RBA-100CB-SEND"){
            Swal.fire({
              icon: 'error',
              title: "Transaction Failed!\nThe sender's account is blocked for all CREDIT transactions!",
              showConfirmButton: true,
              // timer: 3000,
            });
          }else if(error.error.faultCode === "ERR-RBA-100DB-SEND"){
            Swal.fire({
              icon: 'error',
              title: "Transaction Failed!\nThe sender's account is blocked for all DEBIT transactions!",
              showConfirmButton: true,
              // timer: 3000,
            });
          }else if(error.error.faultCode === "ERR-RBA-100C&DB-DEST"){
            Swal.fire({
              icon: 'error',
              title: "Transaction Failed!\nThe receiver's account is blocked for all type of transactions (CREDIT/DEBIT)!",
              showConfirmButton: true,
              // timer: 3000,
            });
          }else if(error.error.faultCode === "ERR-RBA-100CB-DEST"){
            Swal.fire({
              icon: 'error',
              title: "Transaction Failed!\nThe receiver's account is blocked for all CREDIT transactions!",
              showConfirmButton: true,
              // timer: 3000,
            });
          }
          else if (error && error.error && error.error.details) {
            error.error.details.forEach((detail: { field: string, message: string }) => {
                if (detail.field === "transferAmount" && detail.message === "must be greater than 0") {
                    Swal.fire({
                        icon: 'error',
                        title: 'Transaction Failed!\nAmount must be greater than 0',
                        showConfirmButton: true,
                        // timer: 3000,
                    });
                }
            });
        }else{
            Swal.fire({
              icon: 'error',
              title: 'Verify Your Details!',
              showConfirmButton: true,
              // timer: 3000,
            });
          }
        }
      );
    } else {
      // console.error('Form is not valid.');
      Swal.fire({
        icon: 'error',
        title: "Please Enter Required Detais",
        showConfirmButton: false,
        timer: 3000,
      });
    }
  }
 
  selectedOptionCurrencyAmount: string = 'INR';
  selectOptionBudget: string = 'Select';
  dropdownOptionsCurrencyAmount = [
    { value: 'INR', label: 'INR' }
    // { value: 'USD', label: 'USD' },
    // Add more options as needed
  ];
  dropdownOptionsBudget = [
    { value: 'food', label: 'FOOD' },
    { value: 'fuel', label: 'FUEL' },
    { value: 'travel', label: 'TRAVEL' },
    { value: 'bills', label: 'BILLS' },
    { value: 'entertainment', label: 'ENTERTAINMENT' },
    { value: 'loan', label: 'LOAN' },
    { value: 'recharge', label: 'RECHARGE' },
    { value: 'shopping', label: 'SHOPPING' },
    { value: 'miscellaneous', label: 'MISCELLANEOUS' }
 
    // Add more options as needed
  ];
}