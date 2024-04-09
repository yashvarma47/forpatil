import { Component, OnInit } from '@angular/core';
import { TransactionService } from 'src/app/Services/transaction.service';
import Swal from 'sweetalert2';
import { UserService } from 'src/app/Services/user.service';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-beneficiary',
  templateUrl: './add-beneficiary.component.html',
  styleUrls: ['./add-beneficiary.component.css']
})
export class AddBeneficiaryComponent implements OnInit {
  beneficiaryForm: FormGroup | undefined;
  beneficiaryAccountNumber!: number;
  reenteredAccountNumber!: number;
  name: string = '';
  email: string = '';
  successMessage: string = '';
  errorMessage: string = '';
  id: number = 0;
  constructor(private fb: FormBuilder,private transactionService: TransactionService, private userService: UserService) { }


  ngOnInit(): void {
    // Retrieve userId from localStorage with a default value of an empty string
    const storedUserId = localStorage.getItem('userId') ?? '';

    // Subscribe to the userId$ observable to get the user ID dynamically
    this.userService.userId$.subscribe(userId => {
      // Convert the user ID to a number before passing it to the service
      const numericUserId = parseInt(userId || storedUserId, 10); // Use storedUserId if userId is not present
      this.id = numericUserId;
      // Save userId to localStorage for future reference
      localStorage.setItem('userId', numericUserId.toString());
    })
    this.initializeForm();

  }
  initializeForm(): void {
    this.beneficiaryForm = this.fb.group({
      reenteredAccountNumber: [null, Validators.required],
      name: [null, Validators.required],
      email: [null, [Validators.required, Validators.email]]
    });
  }

  onSubmit(form: any) {
    // Here you can handle the submission logic
    if (form.valid) {
      // For now, let's just log the values to the console
      if (this.beneficiaryAccountNumber === this.reenteredAccountNumber) {
        // const jsonData = this.convertFieldsToJson();
        // 
        // Prepare the request object
        const benResponse = {
          accountNumber: this.reenteredAccountNumber,
          name: this.name,
          email: this.email
        };
        
        
        
        
        
        this.transactionService.addBeneficiary(benResponse,this.id).subscribe(
          (response: any) => {
            
            if (response.status === 400) {
              // this.successMessage = 'Beneficiary already exist: ' ;
              Swal.fire({
                icon: 'warning',
                // title: 'warning',
                text: 'Beneficiary already exist',
                showConfirmButton: false,
                timer: 3000,
              });
            } else if (response.status === 200) {
              this.successMessage = 'Beneficiary added successfully: ';
              Swal.fire({
                icon: 'success',
                // title: 'success',
                text: 'Beneficiary added successfully',
                showConfirmButton: false,
                timer: 3000,
              });
            }
            // Optionally, fetch updated data or reset form values
            
          },
          (error: any) => {
            if (error.error.status === 404) {
              this.errorMessage = 'Account number does not exist: ';
              Swal.fire({
                icon: 'error',
                // title: 'Error',
                text: 'Account number does not exist: ',
                showConfirmButton: false,
                timer: 3000,
              });
            }else if (error.error.status === 400) {
              this.successMessage = 'Beneficiary already exist';
              Swal.fire({
                icon: 'error',
                // title: 'success',
                text: 'Beneficiary already exist',
                showConfirmButton: false,
                timer: 3000,
              });
            } else {
              this.errorMessage = 'Update failed: ' + error;
              Swal.fire({
                icon: 'error',
                // title: 'success',
                text: 'Unable to add Benificary',
                showConfirmButton: false,
                timer: 3000,
              });
            }
            
            console.error('Update failed:', error);
          }
        );
        // 

        // Clear the input fields after successful submission
        this.clearInputs();

        // You can add your actual submission logic here
        // For example, you can make an HTTP request to submit the data to a server
      } else {
        // Display an error message or take appropriate action when the account numbers don't match
        console.error('Beneficiary Account Numbers do not match.');
        Swal.fire({
          icon: 'error',
          // title: 'Error',
          text: 'Account number does not exist: ',
          showConfirmButton: false,
          timer: 3000,
        });
      }
    } else {
      // Display an error message or take appropriate action when the form is invalid
      console.error('Form is invalid.');
    }
  }
  

  private clearInputs() {
    // Reset input properties to empty strings
    this.beneficiaryAccountNumber  = NaN;
    this.reenteredAccountNumber  = NaN;
    this.name = '';
    this.email = '';
  }


  limitTo11Digits(event: any) {
    const input = event.target;
    let value = input.value;

    value = value.replace(/\D/g, '');

    if (value.length > 11) {
      value = value.slice(0, 11);
    }

    input.value = value;
  }


  
  onCancel() {
    this.clearInputs();
  }

}
