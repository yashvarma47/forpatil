import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountService } from 'src/app/Services/account.service';
import Swal from 'sweetalert2';
 
@Component({
  selector: 'app-block-account',
  templateUrl: './block-account.component.html',
  styleUrls: ['./block-account.component.css']
})
export class BlockAccountComponent {
  OpenBlockForm: FormGroup;
  ChooseBlock: string = "";
 
  constructor(private fb: FormBuilder, private accountService: AccountService) {
    this.OpenBlockForm = this.fb.group({
      accountNumber: ['', [Validators.required, Validators.pattern(/^\d{12}$/)]],
      blockType: ['', Validators.required],
      reason: ['', Validators.required],
    });
  }
 
  limitTo11Digits(event: any) {
    const input = event.target;
    let value = input.value;
 
    value = value.replace(/\D/g, '');
 
    if (value.length > 11) {
      value = value.slice(0, 11);
    }
 
    input.value = value;
 
    this.OpenBlockForm.get('customerId')!.setValue(value);
  }
  
  dropdownChooseBlock = [
    { value: 'CREDIT', label: 'CREDIT' },
    { value: 'DEBIT', label: 'DEBIT' },
    { value: 'BLOCKED', label: 'BOTH' }
  ];
 
  submitbtn(event: Event): void {
    event.stopPropagation();
    event.preventDefault();
 
    if (this.OpenBlockForm.valid != null) {
      const accountNumber = this.OpenBlockForm.get('accountNumber')!.value;
      const status = this.OpenBlockForm.get('blockType')!.value;
      const reason = this.OpenBlockForm.get('reason')!.value;
 
      
 
      this.accountService.blockAccount(accountNumber, status, reason).subscribe(
        (response: any) => {
          
 
          if (response.statusCode === 200) {
            let successMessage = '';
                    if (status === 'DEBITBLOCKED') {
                        successMessage = 'Successfully Blocked Debit.';
                    } else if (status === 'CREDITBLOCKED') {
                        successMessage = 'Successfully Blocked Credit.';
                    } else {
                        successMessage = response.statusMessage;
                    }
            Swal.fire('Success',successMessage, 'success');
          } else if (response.statusCode === 302) {
            Swal.fire('Warning', response.statusMessage, 'warning');
          } else if (response.statusCode === 400){
            Swal.fire('error', response.statusMessage, 'error');
          } else {
            // Other errors
            Swal.fire('Error', response.statusMessage, 'error');
          }
        },
        (error) => {
          console.error('HTTP Error:', error);
 
          // Handle HTTP errors here
          Swal.fire('Error', 'All fields are required', 'error');
        }
      );
    } else {
      
 
      // Display validation error
      Swal.fire('Validation Error', 'Please fill in all required fields.', 'error');
    }
  }
}