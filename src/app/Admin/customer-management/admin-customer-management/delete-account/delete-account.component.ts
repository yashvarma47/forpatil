import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from 'src/app/Services/login.service';
import Swal from 'sweetalert2';
 
@Component({
  selector: 'app-delete-account',
  templateUrl: './delete-account.component.html',
  styleUrls: ['./delete-account.component.css']
})
export class DeleteAccountComponent {
  deleteForm: FormGroup;
  errorMessage: string | null = null;
 
  constructor(private fb: FormBuilder, private loginService: LoginService) {
    this.deleteForm = this.fb.group({
      customerId: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
      reason: ['', [Validators.required]]
    });
  }
 
  limitTo8Digits(event: any) {
    const input = event.target;
    let value = input.value;
 
    value = value.replace(/\D/g, '');
 
    if (value.length > 8) {
      value = value.slice(0, 8);
    }
 
    input.value = value;
 
    this.deleteForm.get('customerId')!.setValue(value);
  }
 
  deleteCustomer() {
    const customerId = this.deleteForm.get('customerId')?.value;
    const reason = this.deleteForm.get('reason')?.value;
 
    // Additional logic for deleting customer with service call
    this.loginService.deleteCustomer(customerId).subscribe(
      (response) => {
        
        if (response.status === 200) {
          Swal.fire('Success', response.message, 'success');
        }
 
      },
      (error) => {
        this.errorMessage = "The customer cannot be deleted as there are accounts associated with their profile. To proceed, please close the associated accounts first.";
        // console.error(error);
        // Handle the error response here
      }
    );
  }
}