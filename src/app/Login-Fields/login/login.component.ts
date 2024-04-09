import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from 'src/app/Services/login.service';
import { Subscription, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { UserService } from 'src/app/Services/user.service';
 
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnDestroy {
  private userIdSubscription: Subscription | undefined;
  private loginSubscription: Subscription | undefined;
 
  constructor(private router: Router, private loginService: LoginService, private userService: UserService) { }
 
  userid: string = '';
  password: string = '';
  isLoginDisabled: boolean = true;
  isUserIdValid: boolean = false;
  UserIDerrorMessage: string = '';
  loginerrorMessage: string = '';
 
  // Do not edit below
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
        this.isLoginDisabled = !this.isUserIdValid;
 
        if (!this.isUserIdValid) {
          if (response.status === 402 || response.status === 405 || response.status === 404) {
            this.UserIDerrorMessage = 'Invalid Credentials!';
          } else {
            this.UserIDerrorMessage = 'Error validating user ID. Please try again.';
          }
          
        }
 
      }),
      catchError((error) => {
        console.error('Error validating user ID:', error);
        this.disableLogin();
        this.UserIDerrorMessage = 'Error validating user ID. Please try again.';
        return throwError(error); // Rethrow the error after handling it
      })
    ).subscribe();
  }
 
  // Validate password input (if additional validation is needed)
  onPasswordInputChange() { }
 
  // Handle login action
  // Do not edit below
  login() {
    if (this.userid.length !== 8) {
      this.disableLogin();
      return;
    }
    const loginRequest = { userId: this.userid, password: this.password };
 
    // Cancel any previous subscription to avoid race conditions
    if (this.loginSubscription) {
      this.loginSubscription.unsubscribe();
    }
 
    // Perform login through the login service
    this.loginSubscription = this.loginService.login(loginRequest).pipe(
      tap((response: any) => {
        if (response.status === 200) {
          this.router.navigate([response.data.role === 'CUSTOMER' ? '/user-dashboard' : '/admin-home']);
          // Set userId in the service
          this.userService.setUserId(this.userid);
        } else {
          this.handleLoginError(response);
        }
      }),
      catchError((error) => {
        this.handleLoginError(error);
        return throwError(error); // Rethrow the error after handling it
      })
    ).subscribe();
  }
 
  // Handle login errors
  private handleLoginError(response: any) {
    console.error('Error logging in:', response.message);
 
    if (response.status === 401) {
      this.loginerrorMessage = 'Invalid Credentials!';
    } else {
      this.loginerrorMessage = 'Error logging in. Please try again.';
    }
  }
 
  // Enable login button
  private enableLogin() {
    this.isLoginDisabled = false;
  }
  // Check if userid is a valid number and within the specified range
  private isValidUserId() {
    return typeof this.userid === 'number' && this.userid >= 10000000 && this.userid <= 99999999;
  }
 
  // Disable login button and reset user ID validation status
  private disableLogin() {
    this.isLoginDisabled = true;
    this.isUserIdValid = false;
  }
 
  ngOnDestroy() {
    // Unsubscribe to avoid memory leaks
    if (this.userIdSubscription) {
      this.userIdSubscription.unsubscribe();
    }
    if (this.loginSubscription) {
      this.loginSubscription.unsubscribe();
    }
  }

  forgotCredentials() {
    // Your logic for handling both forgot password and forgot user ID
    // You can implement a modal, show a message, or navigate to a specific page
    // based on the user's choice
    // For example:
    // if (userWantsPassword) {
    //   this.forgotPassword();
    // } else {
    //   this.forgotUserId();
    // }
  }
}