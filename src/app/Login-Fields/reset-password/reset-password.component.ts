import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CustomerService } from 'src/app/Services/customer.service';
import Swal from 'sweetalert2';
 
@Component({
  selector: 'app-password-reset',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit  {
 
  passwordToken: string="";
 
  token: string="";
  isTokenValid: boolean = false;
 
  @ViewChild('passwordForm') passwordForm: any;
  Token: any;
 
  constructor(private route: ActivatedRoute, private customerService: CustomerService) {}
 
  ngOnInit(): void {
    // Extract the token from the URL
    this.route.queryParams.subscribe(params => {
      this.passwordToken = params['token'];
    });
   
    this.route.queryParams.subscribe(params => {
      this.Token = params['token'];
      // Call the API to validate the token
      this.validateToken();
    });
  }
 
  validateToken(): void {
    // this.customerService.resetPassword(passwordToken, newPassword).subscribe(
    this.customerService.validateToken(this.Token).subscribe(
      (response: any) => {
        
        
        
 
        if (response.status === 200) {
         
          this.isTokenValid = true;
        } else if (response.status === 401) {
          // Token is expired
         
          
          Swal.fire({
            icon: 'error',
            text: 'Link expired',
            showConfirmButton: false,
            timer: 40000,
          });
        } else if (response.status === 404) {
          // Invalid token
          
          Swal.fire({
            icon: 'error',
            text: 'Link is not valid',
            showConfirmButton: false,
            timer: 40000,
          });
        } else {
          // Handle other status codes
          
        }
      },
      (error: any) => {
        console.error('Error from server:', error);
        
        Swal.fire({
          icon: 'error',
          text: 'Something went wrong',
          showConfirmButton: false,
          timer: 40000,
        });
      }
    );
  }
 
 
 
 
 
 
  validatePassword(): void {
    const newPassword = this.passwordForm.value.newPassword;
    const confirmPassword = this.passwordForm.value.confirmPassword;
 
    // Password criteria
    const passwordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\[\]:;<>,.?~\\/-]).{8,}$/;
 
    if (newPassword !== confirmPassword || !passwordRegex.test(newPassword)) {
      this.passwordForm.control.setErrors({ invalidPassword: true });
    } else {
      const passwordToken = this.passwordToken; // Replace with your password token
      this.customerService.resetPassword(passwordToken, newPassword).subscribe(
        (response: any) => {
          
          
       
          if (response.status === 200) { // Corrected to response.status
            
            Swal.fire({
              icon: 'success',
              text: 'Password reset successfully',
              showConfirmButton: false,
              timer: 3000,
            });
          } else if (response.status === 401) {
            
            Swal.fire({
              icon: 'error',
              text: 'Link expired',
              showConfirmButton: false,
              timer: 3000,
            });
          }
          else if (response.status === 400) {
            
            Swal.fire({
              icon: 'error',
              text: 'New password cannot be same',
              showConfirmButton: false,
              timer: 3000,
            });
          }
          else if (response.status === 422) {
            
            Swal.fire({
              icon: 'error',
              text: 'Password not changed',
              showConfirmButton: false,
              timer: 3000,
            });
          }
          else if (response.status === 404) {
            
            Swal.fire({
              icon: 'error',
              text: 'Link is not valid ',
              showConfirmButton: false,
              timer: 3000,
            });
          }
           else {
            Swal.fire({
              icon: 'error',
              text: 'Something went wrong',
              showConfirmButton: false,
              timer: 3000,
            });
          }
        },
        error => {
          console.error('Error from server:', error);
          Swal.fire({
            icon: 'error',
            text: 'Something went wrong1',
            showConfirmButton: false,
            timer: 3000,
          });
        }
      );
    }
  }
}