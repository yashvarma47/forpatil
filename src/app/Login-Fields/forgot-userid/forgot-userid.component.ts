// forgot-userid.component.ts
 
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CustomerService } from 'src/app/Services/customer.service'; 
import Swal from 'sweetalert2';
 
function panNumberValidator(control: import("@angular/forms").AbstractControl): { [key: string]: boolean } | null {
  const panNumberRegex = /^([a-zA-Z]){5}([0-9]){4}([a-zA-Z]){1}$/;
 
  if (!control.value) {
    return null; // If empty, don't perform validation
  }
 
  if (panNumberRegex.test(control.value)) {
    return null; // Valid PAN number
  } else {
    return { invalidPanNumber: true }; // Invalid PAN number format
  }
}
export interface ForgotUserIdDTO {
  dateOfBirth: Date;
  pan: string;
  aadhar: string;
  phoneNumber: string;
}

 
@Component({
  selector: 'app-forgot-userid',
  templateUrl: './forgot-userid.component.html',
  styleUrls: ['./forgot-userid.component.css']
})
export class ForgotUseridComponent {
  forgotUserIdForm: FormGroup;
  resetLinkSent: boolean = false;
  dateOfBirth: Date | undefined ;
 
  constructor(private fb: FormBuilder, private router: Router, private customerService:CustomerService,) {
    this.forgotUserIdForm = this.fb.group({
      pan: ['', [Validators.required, panNumberValidator, Validators.maxLength(10)]], // Limit PAN to 10 characters
      aadhar: ['', [Validators.required, Validators.minLength(12), Validators.maxLength(12)]], // Aadhar should be exactly 12 digits
      dateOfBirth: ['', Validators.required],
      mobno: ['', [Validators.required, Validators.pattern(/^\d*$/), Validators.minLength(10), Validators.maxLength(10)]], // Allow only numeric values in mobile number and limit to 10 digits
    });
 
    // Ensure panControl is not null before subscribing
    if (this.panControl) {
      this.panControl.valueChanges.subscribe(value => {
        // Trim the value to limit to 10 characters
        const trimmedValue = value.toString().slice(0, 10);
        // Update the value in the control
        this.panControl!.setValue(trimmedValue, { emitEvent: false });
      });
    }
 
    // Ensure aadharControl is not null before subscribing
    if (this.aadharControl) {
      this.aadharControl.valueChanges.subscribe(value => {
        // Trim the value to limit to 12 characters
        const trimmedValue = value.toString().slice(0, 12);
        // Update the value in the control
        this.aadharControl!.setValue(trimmedValue, { emitEvent: false });
      });
    }
 
    // Ensure mobnoControl is not null before subscribing
    if (this.mobnoControl) {
      this.mobnoControl.valueChanges.subscribe(value => {
        // Trim the value to limit to 10 digits
        const trimmedValue = value.toString().slice(0, 10);
        // Update the value in the control
        this.mobnoControl!.setValue(trimmedValue, { emitEvent: false });
      });
    }
  }
 
  get panControl() {
    return this.forgotUserIdForm.get('pan');
  }
 
  get aadharControl() {
    return this.forgotUserIdForm.get('aadhar');
  }
 
  get dobControl() {
    return this.forgotUserIdForm.get('dateOfBirth');
  }
 
  get mobnoControl() {
    return this.forgotUserIdForm.get('mobno');
  }

  goBack() {
    this.router.navigate(['']); // Change '/login' to the route path you want to navigate back to
  }
  
 
  onSubmit() {
    if (this.forgotUserIdForm.valid) {

      const forgotUserIdDTO: ForgotUserIdDTO = {
        dateOfBirth: this.forgotUserIdForm.get('dateOfBirth')?.value,
        pan: this.forgotUserIdForm.value.pan,
        aadhar: this.forgotUserIdForm.value.aadhar,
        phoneNumber: this.forgotUserIdForm.value.mobno
      };

      
      
      // Check Aadhar number correctness before submission
      const aadharControl = this.aadharControl;    
      
      if (aadharControl && aadharControl.value) {
        const aadharNumber = aadharControl.value;
 
        if (!this.isValidAadharNumber(aadharNumber)) {
          // Display error message for incorrect Aadhar number
          aadharControl.setErrors({ 'invalidAadharNumber': true });
          return;
        }
      }
 
      // Check mobile number correctness before submission
      const mobnoControl = this.mobnoControl;
     
      if (mobnoControl && mobnoControl.value) {
        const mobno = mobnoControl.value;
 
        if (!this.isValidMobileNumber(mobno)) {
          // Display error message for incorrect mobile number
          mobnoControl.setErrors({ 'invalidMobileNumber': true });
          return;
        }
      }
 
      // Perform submission logic (you can add your logic here)
      this.customerService.forgotUserId(forgotUserIdDTO).subscribe(
        (response) => {
          // Handle success response
          if (response && response.status === 200){
          
          Swal.fire({
            icon: 'success',
            text: 'UserId is sent to your registered email',
            showConfirmButton: false,
            timer: 3000,
          });
          this.forgotUserIdForm.reset();
          this.resetLinkSent = true;
          }
        },
        (error) => {
          // Handle error response
          console.error('Error submitting forgot user ID:', error);
          console.error(error.message);
        }
      );
      
 
      // Reset the form and set the resetLinkSent flag
      this.forgotUserIdForm.reset();
      this.resetLinkSent = true;
      
    }
  }
 
  isValidAadharNumber(aadharNumber: string): boolean {
    // Implement your logic to validate Aadhar number here
    // For example, you can use a regular expression
    const aadharRegex = /^\d{12}$/;
    return aadharRegex.test(aadharNumber);
  }
 
  isValidMobileNumber(mobno: string): boolean {
    // Implement your logic to validate mobile number here
    // For example, you can use a regular expression
    const mobileRegex = /^\d{10}$/;
    return mobileRegex.test(mobno);
  }
}