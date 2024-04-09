import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CustomerService } from 'src/app/Services/customer.service';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-create-customer',
  templateUrl: './create-customer.component.html',
  styleUrls: ['./create-customer.component.css']
})
export class CreateCustomerComponent implements OnInit {
  newCustomerForm!: FormGroup;
  registrationSubscription: any;
  formSubmitted: boolean = false;
  OpenBlockForm: any;
  constructor(private formBuilder: FormBuilder, private snackBar: MatSnackBar, private customerService: CustomerService) { }
  ngOnInit(): void {
    this.initializeForm();
  }
  limitTo10Digits(event: any, controlName: string) {
    const input = event.target;
    let value = input.value;
    value = value.replace(/\D/g, '');         // Remove non-numeric characters
    if (value.length > 10) {
      value = value.slice(0, 10);  // Limit to the specified maximum length
    }
    input.value = value;    // Update the input value
    this.OpenBlockForm.get(controlName)!.setValue(value);    // Update the corresponding form control in your Angular form
  }

  limitTo6Digits(event: any, controlName: string) {
    const input = event.target;
    let value = input.value;
    value = value.replace(/\D/g, ''); // Remove non-numeric characters
    if (value.length > 6) {
      value = value.slice(0, 6);        // Limit to the specified maximum length
    }
    input.value = value;     // Update the input value
    this.OpenBlockForm.get(controlName)!.setValue(value);     // Update the corresponding form control in your Angular form
  }
  initializeForm(): void {
    this.newCustomerForm = this.formBuilder.group({
      firstName: ['', [Validators.required, this.noNumbersValidator]],
      lastName: ['', [Validators.required, this.noNumbersValidator]],
      street: ['', Validators.required],
      city: ['', [Validators.required, this.noNumbersValidator]],
      state: ['', Validators.required],
      zipcode: ['', [Validators.required, Validators.pattern('[0-9]{6}')]],
      dateOfBirth: ['', [Validators.required, this.validateAbove18]],
      gender: ['', Validators.required],
      email: ['', [Validators.required, this.customEmailValidator]],
      phoneNumber: ['', [Validators.required, this.validatePhoneNumber]],
      panNumber: [
        '',
        [
          Validators.required,
          Validators.pattern('[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}'),
          this.uppercaseInput
        ]
      ],
      aadharNumber: ['', [Validators.required, Validators.pattern('[0-9]{12}')]],
    });
  }
  validateAbove18(control: AbstractControl): { [key: string]: boolean } | null {
    if (control.value) {
      const birthDate = new Date(control.value);
      const currentDate = new Date();
      const age = currentDate.getFullYear() - birthDate.getFullYear();
      if (age < 18) {
        return { 'below18': true };
      }
    }
    return null;
  }
// Do not edit below
submitNewCustomerForm() {
  this.formSubmitted = true;
  if (this.newCustomerForm.valid) {
    const address = {
      street: this.newCustomerForm.get('street')?.value,
      city: this.newCustomerForm.get('city')?.value,
      state: this.newCustomerForm.get('state')?.value,
      zipcode: this.newCustomerForm.get('zipcode')?.value,
    }
    const customerRequest = {
      firstName: this.newCustomerForm.get('firstName')?.value,
      lastName: this.newCustomerForm.get('lastName')?.value,
      addressDTO: address,
      dateOfBirth: this.newCustomerForm.get('dateOfBirth')?.value,
      gender: this.newCustomerForm.get('gender')?.value,
      email: this.newCustomerForm.get('email')?.value,
      phoneNumber: this.newCustomerForm.get('phoneNumber')?.value,
      panNumber: this.newCustomerForm.get('panNumber')?.value,
      aadharNumber: this.newCustomerForm.get('aadharNumber')?.value,
    };
 
    const panNumberControl = this.newCustomerForm.get('panNumber');
    if (panNumberControl) {
      panNumberControl.setValue(panNumberControl.value.toUpperCase());
    }
 
    // Make the HTTP request to register the customer
    this.customerService.registerCustomer(customerRequest).subscribe(
      (response) => {
        // Handle the successful response
        
 
        if (response.status === 200) {
          Swal.fire({
            icon: 'success',
            title: response.message,
            showConfirmButton: true,
          }).then((result) => {
            if (result.isConfirmed) {
              // Reload the browser session
              window.location.reload();
            }
          });
 
        } else if (response.status === 404) {
          Swal.fire({
            icon: 'error',
            title: 'Error Registering Customer',
            text: 'Customer already exists. Please check your details.',
            showConfirmButton: false,
            timer: 3000,
          });
        } else {
          Swal.fire({
            icon: 'warning',
            title: 'Error registering customer. Please try again.',
            showConfirmButton: false,
            timer: 1500,
          });
        }
      },
      (error) => {
        // Handle the error response
        console.error('Error:', error);
        // Display an error message using MatSnackBar
        this.snackBar.open(error.error.message, 'Close', { duration: 3000, panelClass: 'error-snackbar' });
      }
    );
  } else {
    // Mark only the touched controls as invalid
    Object.values(this.newCustomerForm.controls).forEach(control => {
      if (control instanceof FormGroup) {
        this.markAllControlsAsTouched(control);
      } else {
        control.markAsTouched();
      }
    });
  }
}
 

  markAllControlsAsTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markAllControlsAsTouched(control);
      }
    });
  }
  validatePhoneNumber(control: { value: string }): { [key: string]: boolean } | null {
    const isValid = /^[0-9]{10}$/.test(control.value);
    if (!isValid) {
      return { 'invalidPhone': true };
    }
    return null;
  }
  customEmailValidator(control: AbstractControl): ValidationErrors | null {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@maveric-systems\.com$/; // Regex pattern for email validation
    if (control.value && !emailPattern.test(control.value)) {
      return { 'customEmailPattern': true }; // Return error if email format is invalid
    }
    return null; // Return null if email format is valid
  }
  noNumbersValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const hasNumber = /\d/.test(control.value);
    return hasNumber ? { 'containsNumber': true } : null;
  }
  uppercaseInput(control: AbstractControl): ValidationErrors | null {
    const enteredValue = control.value;
    const uppercaseValue = enteredValue.toUpperCase();
    if (enteredValue !== uppercaseValue) {
      // Update the value in the control only if it's not already in uppercase
      control.patchValue(uppercaseValue);
    }
    return null;
  }
  shouldShowError(controlName: string): boolean {
    const control = this.newCustomerForm.get(controlName);
    return control ? (control.invalid && (control.touched || this.formSubmitted)) : false;
  }
  cancelNewCustomerForm() {
    // Handle cancel button logic here
    
  }
}