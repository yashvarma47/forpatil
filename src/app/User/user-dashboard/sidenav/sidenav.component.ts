import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})
export class SidenavComponent  {
  // Flag to toggle the dropdown
  showDropdown = false;

  showAccountDropdown: boolean = false;
showProfileDropdown: boolean = false;


  constructor(private router: Router) {}

  // Toggle the dropdown
  // toggleDropdown() {
  //   this.showDropdown = !this.showDropdown;
  // }
  toggleAccountDropdown() {
    this.showAccountDropdown = !this.showAccountDropdown;
  }
  
  toggleProfileDropdown() {
    this.showProfileDropdown = !this.showProfileDropdown;
  }
  

  // Redirect methods for each option
  redirectToComponent1() {
    this.router.navigate(['accountdetails']); // Adjust the path accordingly
  }

  redirectToComponent2() {
    this.router.navigate(['view-account-balance']); // Adjust the path accordingly
  }

  redirectToComponent3() {
    this.router.navigate(['account-closure']); // Adjust the path accordingly
  }

  redirectToComponent4() {
    this.router.navigate(['setup-profile']); // Adjust the path accordingly
  }
  redirectToComponent5() {
    this.router.navigate(['change-password']); // Adjust the path accordingly
  }
  redirectToComponent6() {
    this.router.navigate(['setup-budget']); // Adjust the path accordingly
  }
  redirectToComponent7() {
    this.router.navigate(['manage-notifications']); // Adjust the path accordingly
  }

}
