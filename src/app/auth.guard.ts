import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from './Services/AutheticationService/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
 
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
 
  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}
 
  canActivate(): boolean {
    if (this.authService.isLoggedInUser()) {
      this.router.navigateByUrl('/user-dashboard');
      return true;

    } else {
      this.snackBar.open('Please log in', 'OK');
      
      return false;
    }
  }
}