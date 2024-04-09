import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CustomerService } from 'src/app/Services/customer.service';
import Swal from 'sweetalert2';
 
 
@Component({
  selector: 'app-forget-pass',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  isDobDisabled(isDobDisabled: any) {
    throw new Error('Method not implemented.');
  }
  forgetPassForm: FormGroup;
  resetLinkSent: boolean = false;
  // isDobDisabled: boolean = true;
 
 
  constructor(private fb: FormBuilder, private router: Router,private forgotPasswordService: CustomerService) {
    this.forgetPassForm = this.fb.group({
      customerId: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
      // dob: [{ value: '', disabled: true }, [Validators.required]],
      dob: ['', [Validators.required]],
 
      panNumber: [
        '',
        [
          Validators.required,
          Validators.pattern('[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}'), // Allow both upper and lower case
          this.uppercaseInput
        ]
      ],
      phone: ['', [Validators.required, this.validatePhoneNumber]] // Allow only numeric values in mobile number and limit to 10 digits
    });
  }
 
  validatePhoneNumber(control: { value: string }): { [key: string]: boolean } | null {
    const isValid = /^[0-9]{10}$/.test(control.value);
 
    if (!isValid) {
      return { 'invalidPhone': true };
    }
 
    return null;
  }
 
  shouldShowError(controlName: string): boolean {
    const control = this.forgetPassForm.get(controlName);
    return control ? (control.invalid && (control.touched || this.resetLinkSent)) : false;
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
  limitTo8Digits(event: any) {
    const input = event.target;
    let value = input.value;
 
    // Remove non-numeric characters
    value = value.replace(/\D/g, '');
 
    // Limit to 8 digits
    if (value.length > 8) {
      value = value.slice(0, 8);
    }
 
    // Update the input value
    input.value = value;
 
    // Update the form control value
    this.forgetPassForm.get('customerId')!.setValue(value);
  }
  getCurrentDate(): string {
    const currentDate = new Date();
    return currentDate.toISOString().split('T')[0];
  }
  // enableDob() {
  //   this.isDobDisabled = false;
  // }
 
  get customerIdControl() {
    return this.forgetPassForm.get('customerId');
  }
 
  goBack() {
    this.router.navigate(['']); // Change '/login' to the route path you want to navigate back to
  }
 
  onSubmit() {
    if (this.forgetPassForm.valid) {
      const forgotPasswordDTO = {
        userId: this.forgetPassForm.value.customerId,
        pan: this.forgetPassForm.value.panNumber,
        dateOfBirth: this.forgetPassForm.value.dob,
        phoneNumber: this.forgetPassForm.value.phone
      };
 
     
      this.forgotPasswordService.forgotPassword(forgotPasswordDTO).subscribe(
        response => {
          
         
 
          if (response.status === 200) {
            
            Swal.fire({
              icon: 'success',
              text: 'Mail sent Successfully',
              showConfirmButton: false,
              timer: 3000,
            });
            // this.resetLinkSent = true;
          } else {
            Swal.fire({
              icon: 'error',
              text: 'Invalid data',
              showConfirmButton: false,
              timer: 3000,
            });
            // Handle invalid data error, such as showing an error message to the user
          }
        },
        error => {
          if (error.status === 400) {
            
            Swal.fire({
              icon: 'error',
              text: 'Invalid data',
              showConfirmButton: false,
              timer: 3000,
            });
          }
            else{
              Swal.fire({
                icon: 'error',
                text: 'Something went wrong',
                showConfirmButton: false,
                timer: 3000,
              });
            }
            // this.resetLinkSent = true;
          }
         
          // Handle other errors, such as showing an error message to the user
       
      );
 
      // Perform submission logic (you can add your logic here)
      
      
      
 
      // this.resetLinkSent = true;
    };
    // Call your service method to send the form data
   
  }
}