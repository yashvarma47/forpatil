import { Component, OnInit } from '@angular/core';
import { CustomerService } from 'src/app/Services/customer.service';
import { UserService } from 'src/app/Services/user.service';
import Swal from 'sweetalert2';
 
@Component({
  selector: 'app-setup-profile',
  templateUrl: './setup-profile.component.html',
  styleUrls: ['./setup-profile.component.css']
})
export class SetupProfileComponent implements OnInit {
  isEditMode = false;
  customerData: any = {};
  userId: number | null = null;
 
  constructor(private customerService: CustomerService, private userService: UserService) { }
 
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
    this.loadCustomerDetails();
  }
 
  onEditClick() {
    // this.isEditMode = true;
    // 
 
    this.isEditMode = !this.isEditMode; // Toggle isEditMode variable
    
  }
 
  onCancelClick() {
    this.isEditMode = false;
    // Reset form values if needed
  }
 
  loadCustomerDetails() {
    // Check if userId is available
    if (this.userId === null) {
      console.error('User ID not available.');
      return;
    }
    this.customerService.getCustomerProfileUpdate(this.userId)
      .subscribe(
        response => {
          
          this.customerData = response.data; // Assign the received data to customerData
          
         
        },
        error => {
          console.error('Error fetching customer details:', error);
        }
      );
  }
 
  customerDataRequest: any = {
    firstName: '',
    lastName: '',
    addressDTO: {
      street: '',
      city: '',
      state: '',
      zipcode: ''
    },
    dateOfBirth: '',
    gender: '',
    email: '',
    phoneNumber: '',
    panNumber: '',
    aadharNumber: ''
  };
 
  convertFieldsToJson(): any {
    return {
      firstName: this.customerData.firstName,
      lastName: this.customerData.lastName,
      addressDTO: {
        street: this.customerData.address.street,
        city: this.customerData.address.city,
        state: this.customerData.address.state,
        zipcode: this.customerData.address.zipcode
      },
      dateOfBirth: this.customerData.dateOfBirth, // Add logic to convert date if needed
      gender: this.customerData.gender,
      email: this.customerData.email,
      phoneNumber: this.customerData.phoneNumber,
      panNumber: this.customerData.panNumber, // Replace with actual property
      aadharNumber: this.customerData.aadharNumber // Replace with actual property
    };
  }
 
 
  onUpdateClick() {
    // Assuming you have the form values in the customerData object
    // Check if userId is available
    if (this.userId === null) {
      console.error('User ID not available.');
      return;
    }
    const jsonData = this.convertFieldsToJson();
    
    this.customerService.updateCustomerDetails(this.userId, jsonData)
      .subscribe(
        (response) => {
          if(response.status === 200) {
            Swal.fire({
              icon: 'success',
              text: 'Update successful!',
              showConfirmButton: false,
              timer: 3000,
            });
            
            this.isEditMode = false;
            // Optionally, fetch updated data or reset form values
            
          }
        },
        error => {
          
          console.error('Update failed:', error);
        }
      );
  }
  limitTo10Digits(event: any) {
    const input = event.target;
    let value = input.value;
 
    value = value.replace(/\D/g, '');
 
    if (value.length > 10) {
      value = value.slice(0, 9);
    }
 
    input.value = value;
 
  }
}