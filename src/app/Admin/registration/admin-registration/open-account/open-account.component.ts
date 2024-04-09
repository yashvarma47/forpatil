import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginService } from 'src/app/Services/login.service';
import { UserService } from 'src/app/Services/user.service';
import { Subscription, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { AccountService } from 'src/app/Services/account.service';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
 
@Component({
  selector: 'app-open-account',
  templateUrl: './open-account.component.html',
  styleUrls: ['./open-account.component.css']
})
export class OpenAccountComponent {
 
  private userIdSubscription: Subscription | undefined;
  OpenAccountForm: FormGroup;
  userid: string = '';
  ChooseAcc: any;
  ChooseCurr: String = "INR";
  isUserIdValid: boolean = false;
  UserIDerrorMessage: string = '';
 
  constructor(private fb: FormBuilder, private accountService: AccountService, private loginService: LoginService, private userService: UserService, private router: Router) {
    this.OpenAccountForm = this.fb.group({
      customerId: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
 
    });
  }
 
  navigateTo() {
    this.router.navigate(['create-customer'])
    }
 
  // Disable login button and reset user ID validation status
  private disableLogin() {
    this.isUserIdValid = false;
  }
 
  // Validate user ID input
  onUserIdInputChange() {
    this.UserIDerrorMessage = '';
 
    if (this.userid.length !== 8) {
      this.disableLogin();
      return;
    }
 
    const loginRequest = { userId: this.userid };
 
    // Cancel any previous subscription to avoid race conditions
    if (this.userIdSubscription) {
      this.userIdSubscription.unsubscribe();
    }
 
    // Validate user ID through the login service
    this.userIdSubscription = this.loginService.validateUserId(loginRequest).pipe(
      tap((response: any) => {
        this.isUserIdValid = response.status === 200;
 
        if (!this.isUserIdValid) {
          if (response.status === 402 || response.status === 405 || response.status === 404) {
            this.UserIDerrorMessage = "Customer doesn’t exist";
          } else {
            this.UserIDerrorMessage = 'Error validating user ID. Please try again.';
          }
          
        }
 
      }),
      catchError((error) => {
        console.error('Error validating user ID:', error);
        // this.disableLogin();
        this.UserIDerrorMessage = 'Error validating user ID. Please try again.';
        return throwError(error); // Rethrow the error after handling it
      })
    ).subscribe();
  }
 
  limitTo8Digits(event: any) {
    const input = event.target;
    let value = input.value;
 
    value = value.replace(/\D/g, '');
 
    if (value.length > 8) {
      value = value.slice(0, 8);
    }
 
    input.value = value;
 
    this.OpenAccountForm.get('customerId')!.setValue(value);
  }
 
  get customerIdControl() {
    return this.OpenAccountForm.get('customerId');
  }
 
  dropdownChooseAcc = [
    { value: 'SAVINGS', label: 'SAVINGS' },
    { value: 'CURRENT ', label: 'CURRENT' }
  ];
  dropdownChooseCurr = [
    { value: 'INR', label: 'INR' },
  ];
 
  submitbtn(event: Event): void {
    if (this.isUserIdValid) {
      if (this.ChooseAcc !== "SAVINGS" || this.ChooseAcc !== "CURRENT") {
        Swal.fire({
          icon: 'error',
          title: 'Invalid Account Type',
          showConfirmButton: false,
          timer: 2000,
        });
      }
      this.accountService.createAccount(+this.userid, this.ChooseAcc)
        .subscribe((response) => {
          if(response.status === 200) {
            Swal.fire({
              icon: 'success',
              title: 'Account Created Successfully!',
              showConfirmButton: false,
              timer: 2000,
            });
          }

        }, (error) => {
          console.error('Error creating account:', error);
          Swal.fire({
            icon: 'error',
            title: 'Account Created Failed!',
            showConfirmButton: false,
            timer: 1500,
          });
          // Handle the error as needed
        });
    }
    event.stopPropagation();
    event.preventDefault();
  }
}