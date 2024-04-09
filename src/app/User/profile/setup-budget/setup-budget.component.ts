import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import { TransactionService } from 'src/app/Services/transaction.service';
import Swal from 'sweetalert2';
 
interface BudgetThreshold {
  [key: string]: number; // Index signature
}
 
export class BudgetSetupDTO {
  month: number = new Date().getMonth() + 1; // Adding 1 because getMonth() returns zero-based index
  budgetThreshold: BudgetThreshold = {
    food: 0,
    fuel: 0,
    travel: 0,
    bills: 0,
    entertainment: 0,
    loan: 0,
    recharge: 0,
    shopping: 0,
    miscellaneous: 0,
  };
}
 
 
@Component({
  selector: 'app-setup-budget',
  templateUrl: './setup-budget.component.html',
  styleUrls: ['./setup-budget.component.css']
})
export class SetupBudgetComponent implements OnInit {
 
  editMode: boolean = false;
  categories: string[] = [""];
  amounts: string[] = [""];
  amountForm!: FormGroup;
  customerDetails: any;
  customerAccounts: any;
  dropdownChooseoptions: any[] = [];
  selAcc : string ='';
  selectedFromAccount: string = '';
  budgetSetupDTO: BudgetSetupDTO = {
    month: new Date().getMonth() + 1,
    budgetThreshold: {
      food: 0,
      fuel: 0,
      travel: 0,
      bills: 0,
      entertainment: 0,
      loan: 0,
      recharge: 0,
      shopping: 0,
      miscellaneous: 0
    }
  };
 
  constructor(private accountService: AccountService, private fb: FormBuilder, private userService: UserService, private transactionService: TransactionService) { }
 
  ngOnInit(): void {
    // Initialize the form
    this.amountForm = this.fb.group({
      selectedAccount: ['', Validators.required], // Form control for the selected account
      amounts: this.fb.array([]) // Form control for amounts (you can create dynamic form controls for each category)
    });
 
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
 
  toggleEditMode() {
    this.editMode = !this.editMode;
    if (!this.editMode) {
      // Call the function for submit action
      this.onSubmit();
    }
  }
 
  onSubmit() {
 
    Object.keys(this.budgetSetupDTO.budgetThreshold).forEach((key, index) => {
      this.budgetSetupDTO.budgetThreshold[key] = parseFloat(this.amounts[index]);
    });
 
 
    
 
    this.transactionService.updateBudgetLimit(parseInt(this.selAcc), this.budgetSetupDTO).subscribe(
      (response: any) => {
        if (response && response.status === 200) {
          if (response.status === 200) {
            Swal.fire({
              icon: 'success',
              text: 'Update successful!',
              showConfirmButton: false,
              timer: 3000,
            });
          }
        } else if (response && response.status === 200) {
          if (response.status === 404) {
            Swal.fire({
              icon: 'error',
              text: 'Expense wise budgets not found for requested account number!',
              showConfirmButton: false,
              timer: 3000,
            });
          }
        } else {
          Swal.fire({
            icon: 'error',
            text: 'Internal Server Error',
            showConfirmButton: false,
            timer: 3000,
          });
        }
      },
      (error) => {
        Swal.fire({
          icon: 'error',
          text: 'Internal Server Error',
          showConfirmButton: false,
          timer: 3000,
        });
      }
    );
 
  }
 
  updateUI(response: any): void {
    this.customerDetails = response.data;
    this.customerAccounts = response.accounts;
 
    // Populate "From Account" dropdown
    this.dropdownChooseoptions = this.customerAccounts.map((acc: { accountnumber: any }) => ({
      value: acc.accountnumber,
      label: acc.accountnumber.toString(),
    }));
 
    // Set default selection for "From Account"
    if (this.dropdownChooseoptions.length > 0) {
      this.selectedFromAccount = this.dropdownChooseoptions[0].value;
      this.amountForm.get('selectedAccount')?.setValue(this.selectedFromAccount); // Set the default selected account in the form using optional chaining
    }
  }
 
  nonPositiveValidator(control: AbstractControl<any>): { [key: string]: any } | null {
    if (control.value <= 0) {
      return { nonPositive: true };
    }
    return null;
  }
 
  onAccountSelectionChange(event: any): void {
    this.selAcc= event?.target?.value;
    const selectedValue = event?.target?.value;
    if (selectedValue) {
      this.transactionService.getBudgetLimit(selectedValue).subscribe(
        (response: any) => {
          if (response && response.status === 200) {
            this.amounts = Object.values(response.data);
            // this.updateUI(response);
 
            this.categories = Object.keys(this.budgetSetupDTO.budgetThreshold);
            this.amounts[0] = response.data.food_threshold
            this.amounts[1] = response.data.fuel_threshold
            this.amounts[2] = response.data.travel_threshold
            this.amounts[3] = response.data.bills_threshold
            this.amounts[4] = response.data.entertainment_threshold
            this.amounts[5] = response.data.loan_threshold
            this.amounts[6] = response.data.recharge_threshold
            this.amounts[7] = response.data.shopping_threshold
            this.amounts[8] = response.data.miscellaneous_threshold
 
 
          } else if (response && response.status === 404) {
         
            this.amounts[0] = "0";
            this.amounts[1] = "0";
            this.amounts[2] = "0";
            this.amounts[3] = "0";
            this.amounts[4] = "0";
            this.amounts[5] = "0";
            this.amounts[6] = "0";
            this.amounts[7] = "0";
            this.amounts[8] = "0";
 
          } else {
            Swal.fire({
              icon: 'error',
              text: 'Internal Server Error',
              showConfirmButton: false,
              timer: 3000,
            });
          }
        },
        (error) => {
          console.error('API Error2:', error);
        }
      );
    }
  }
  validateAmount(event: Event): void {
    const inputElement: HTMLInputElement = event.target as HTMLInputElement;
    const inputValue: string = inputElement.value;

    const isValid: boolean = /^\d+(\.\d{0,2})?$/.test(inputValue);

    if (!isValid) {
      // Prevent entering invalid characters
      inputElement.value = inputValue.slice(0, -1);
    }
  }
}