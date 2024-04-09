import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
 
 
@Component({
  selector: 'app-admin-sidenav',
  templateUrl: './admin-sidenav.component.html',
  styleUrls: ['./admin-sidenav.component.css'],
  animations: [
    trigger('scaleDown', [
      state('void', style({ transform: 'scaleY(0)', opacity: 0 })),
      transition(':enter, :leave', [animate('0.3s ease-in-out')]),
    ]),
  ],
})
export class AdminSidenavComponent {
  // Flag to toggle the dropdown
  showDropdown = false;
 
  showAccountDropdown: boolean = false;
  showProfileDropdown: boolean = false;
  showregistrationDropdown: boolean = false;
  showcustomermanagementDropdown: boolean = false;
 
 
  constructor(private router: Router) {}
 
  toggleRegistrationDropdown(){
    this.showregistrationDropdown = !this.showregistrationDropdown;
  }
 
  toggleCustomerManagementDropdown() {
    this.showcustomermanagementDropdown = !this.showcustomermanagementDropdown;
  }
 
  toggleProfileDropdown() {
    this.showProfileDropdown = !this.showProfileDropdown;
  }
 
 
  // Redirect methods for each option
  redirectToCreateCustomer(){
    this.router.navigate(['create-customer']); // Adjust the path accordingly
  }
 
  redirectToOpenAccount(){
    this.router.navigate(['open-account']); // Adjust the path accordingly
  }
 
  redirectToBlockAccount() {
    this.router.navigate(['unblock-account']); // Adjust the path accordingly
  }
 
  redirectToCloseAccount(){
     this.router.navigate(['close-account']);  // Adjust the path accordingly
  }
 
  redirectToDeleteCustomer(){
      this.router.navigate(['delete-account']); // Adjust the path accordingly
  }
 
}
 
