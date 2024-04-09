import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from 'src/app/Services/account.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-unblockpopup',
  templateUrl: './unblockpopup.component.html',
  styleUrls: ['./unblockpopup.component.css']
})
export class UnblockpopupComponent {
  OpenBlockForm: FormGroup;
  ChooseBlock: string = "";
 
  constructor(private fb: FormBuilder, private accountService: AccountService, private route: ActivatedRoute) {
    this.OpenBlockForm = this.fb.group({
      accountNumber: ['', [Validators.required, Validators.pattern(/^\d{12}$/)]],
      blockType: ['', Validators.required],
      //blockType: [{ value: 'ACTIVE', disabled: true }, Validators.required],
      reason: ['', Validators.required],
    });

    this.route.params.subscribe(params => {
      const accountNumber = params['accountNumber'];
      // Set the retrieved accountNumber to the form control
      this.OpenBlockForm.patchValue({
        accountNumber: accountNumber
      });

      const accountStatus = params['accountStatus'];
      this.updateDropdownOptions(accountStatus);
    });
  }

  updateDropdownOptions(accountStatus: string): void {
    
    switch (accountStatus.toUpperCase()) {
      case 'BLOCKED':
        this.dropdownChooseBlock = [
          { value: 'ACTIVE', label: 'Active' },
          { value: 'CREDITUNBLOCK', label: 'Credit Unblock' },
          { value: 'DEBITUNBLOCK', label: 'Debit Unblock' }
        ];
        
        break;
      case 'DEBITBLOCKED':
        this.dropdownChooseBlock = [
          { value: 'DEBITUNBLOCK', label: 'Debit Unblock' }
        ];
        break;
      case 'CREDITBLOCKED':
        this.dropdownChooseBlock = [
          { value: 'CREDITUNBLOCK', label: 'Credit Unblock' }
        ];
        break;
      default:
        this.dropdownChooseBlock = [];
    }
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

        

        this.accountService.unblockAccount(accountNumber, status, reason).subscribe(
            (response: any) => {
                

                if (response.statusCode === 200) {
                    let successMessage = '';
                    if (status === 'DEBITUNBLOCK') {
                        successMessage = 'Successfully Debit Unblocked.';
                    } else if (status === 'CREDITUNBLOCK') {
                        successMessage = 'Successfully Credit Unblocked.';
                    } else {
                        successMessage = response.statusMessage;
                    }
                    Swal.fire('Success', successMessage, 'success');
                    // Clear all fields in the form
                    this.OpenBlockForm.reset();
                    // Disable the button
                    this.OpenBlockForm.disable();
                } else if (response.statusCode === 302) {
                    Swal.fire('Warning', response.statusMessage, 'warning');
                } else if (response.statusCode === 400) {
                    Swal.fire('error', response.statusMessage, 'error');
                } else {
                    // Other errors
                    Swal.fire('Error', response.statusMessage, 'error');
                }
            },
            (error) => {
                console.error('HTTP Error:', error);

                // Handle HTTP errors here
                Swal.fire('Error', 'Something went wrong. Please try again later.', 'error');
            }
        );
    } else {
        

        // Display validation error
        Swal.fire('Validation Error', 'Please fill in all required fields.', 'error');
    }
}

}