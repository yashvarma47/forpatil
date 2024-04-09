import { Component, OnInit } from '@angular/core';
import { NgForm, AbstractControl } from '@angular/forms';
import { LoginService } from 'src/app/Services/login.service';
import { UserService } from 'src/app/Services/user.service';  // Assuming you have a UserService for managing user-related data
import Swal from 'sweetalert2';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {
  currentPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  userId: number | null = null;

  constructor(private loginService: LoginService, private userService: UserService) {}  // Inject the LoginService and UserService

  ngOnInit(): void {
    const storedUserId = localStorage.getItem('userId');

    if (storedUserId !== null && !isNaN(+storedUserId)) {
      this.userId = +storedUserId;
    } else {
      console.error('Invalid or missing user ID in localStorage.');
      this.userId = null;
    }

    // Subscribe to the userId$ observable to get the user ID dynamically
    this.userService.userId$.subscribe(userId => {
      // Convert the user ID to a number before passing it to the service
      const numericUserId = parseInt(userId || storedUserId || '0', 10); 
      
      // Save userId to localStorage for future reference
      localStorage.setItem('userId', numericUserId.toString());
      this.userId = numericUserId;
    });
  }


  updatePassword(): void {
    // Check if the new password and confirm password are the same
    if (this.newPassword !== this.confirmPassword) {
      
      return;
    }

    // Check if userId is available
    if (this.userId === null) {
      console.error('User ID not available.');
      return;
    }

    // Call the API to update the password
    this.loginService.changePassword(this.userId, this.currentPassword, this.newPassword)
      .subscribe(response => {
        if (response.status === 200) {
          Swal.fire({
            icon: 'success',
            text: 'Password changed successfully',
            showConfirmButton: false,
            timer: 3000,
          });
          
          // Reset form and clear values after successful update
          this.resetForm();
        } else if (response.status === 400) {
          Swal.fire({
            icon: 'error',
            text: 'Old password does not match',
            showConfirmButton: false,
            timer: 3000,
          });
          
          // Reset form and clear values after unsuccessful update
          this.resetForm();
        }
        else if (response.status === 401) {
          Swal.fire({
            icon: 'error',
            text: 'New Password cannot be same as old password',
            showConfirmButton: false,
            timer: 3000,
          });
          
          // Reset form and clear values after unsuccessful update
          this.resetForm();
        }
      }, error => {
        console.error('Error updating password:', error);
        // Handle error as needed (display message to the user, etc.)
        if(error.error.status === 400) {

          Swal.fire({
            icon: 'error',
            text: 'Old password does not match',
            showConfirmButton: false,
            timer: 3000,
          });
         }else if (error.error.status === 401) {
          Swal.fire({
            icon: 'error',
            text: 'New Password cannot be same as old password',
            showConfirmButton: false,
            timer: 3000,
          });
          
          // Reset form and clear values after unsuccessful update
          this.resetForm();
        }else{
          Swal.fire({
            icon: 'error',
            text: 'Something went wrong',
            showConfirmButton: false,
            timer: 3000,
          });
        }
        
      });
  }

  cancelUpdate(): void {
    // Add your logic to cancel the password update
    

    // Reset form
    this.resetForm();
  }

  private resetForm(): void {
    this.currentPassword = '';
    this.newPassword = '';
    this.confirmPassword = '';
  }

  passwordsDoNotMatch(): boolean {
    return this.newPassword !== this.confirmPassword;
  }

  isTouched(control: AbstractControl | null): boolean {
    return control?.touched ?? false;
  }
}
