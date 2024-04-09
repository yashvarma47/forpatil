import { Component, OnInit } from '@angular/core';
import { CustomerService } from 'src/app/Services/customer.service';
import { UserService } from 'src/app/Services/user.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-manage-notifications',
  templateUrl: './manage-notifications.component.html',
  styleUrls: ['./manage-notifications.component.css']
})
export class ManageNotificationsComponent implements OnInit {
  // userId: number = 12345678;
  typeofnotification: string = "";
  isChecked: boolean = false;
  NotificationData: any = {};
  transactionConfirmationChecked: boolean = false;
  accountBalanceUpdatesChecked: boolean = false;
  lowBalanceAlertChecked: boolean = false;
  securityAlertsChecked: boolean = false;
  accountBlockAlertsChecked: boolean = false;
  customerId: number = 0;

  constructor(private customerService: CustomerService, private userService: UserService) { }

  ngOnInit() {
    // Retrieve userId from localStorage with a default value of an empty string
    const storedUserId = localStorage.getItem('userId') ?? '';

    // Subscribe to the userId$ observable to get the user ID dynamically
    this.userService.userId$.subscribe(userId => {
      // Convert the user ID to a number before passing it to the service
      const numericUserId = parseInt(userId || storedUserId, 10); // Use storedUserId if userId is not present
      this.customerId = numericUserId;
      // Save userId to localStorage for future reference
      localStorage.setItem('userId', numericUserId.toString());
      this.customerService.getNotifications(numericUserId)
        .subscribe(
          (response) => {
            if (response.status === 200) {

              
              this.NotificationData = response.listData;
              if (this.NotificationData[0].accountBalanceUpdates === 'Email') {
                this.accountBalanceUpdatesChecked = true;
                

              } else {
                this.accountBalanceUpdatesChecked = false;
              }

              if (this.NotificationData[0].accountBlockAlerts === 'Email') {
                this.accountBlockAlertsChecked = true;
              } else {
                this.accountBlockAlertsChecked = false;
              }

              if (this.NotificationData[0].lowBalanceAlerts === 'Email') {
                this.lowBalanceAlertChecked = true;
              } else {
                this.lowBalanceAlertChecked = false;
              }

              if (this.NotificationData[0].securityAlerts === 'Email') {
                this.securityAlertsChecked = true;
              } else {
                this.securityAlertsChecked = false;
              }

              if (this.NotificationData[0].txnConfirmation === 'Email') {
                this.transactionConfirmationChecked = true;
              } else {
                this.transactionConfirmationChecked = false;
              }
            }

            // 
            //   this.accountBalanceUpdatesChecked,
            //   this.lowBalanceAlertChecked,
            //   this.securityAlertsChecked,
            //   this.accountBlockAlertsChecked);


          },
          error => {
            console.error('Error fetching notifications:', error);
        }
        );
    })
  }
 
  onCheckClick(notificationType: string): void {
      // Assuming you have the form values in the customerData object
      // Check if userId is available
      if(this.customerId === null) {
      console.error('User ID not available.');
      return;
    }

    switch (notificationType) {
      case "Transaction_Confirmation":
        this.typeofnotification = "Txn_Confirmation";
        break;
      case "Account_Balance_Updates":
        this.typeofnotification = "Account_Balance_updates";
        break;
      case "Low_balance_alert":
        this.typeofnotification = "Low_balance_alerts";
        break;
      case "Security_Alerts":
        this.typeofnotification = "Security_alerts";
        break;
      case "Account_block_alerts":
        this.typeofnotification = "Account_block_alerts";
        break;
      default:
        console.error('Invalid notification type:', notificationType);
        return;
    }

    this.customerService.updateNotifications(this.customerId, this.typeofnotification, this.getCheckboxState(notificationType))
      .subscribe(
        (response) => {
          if (response.status === 200) {
            // Swal.fire({
            //   icon: 'success',
            //   text: 'Update successful!',
            //   showConfirmButton: false,
            //   timer: 3000,
            // });

            


          }
        },
        error => {
          // 
          console.error('Update failed:', error);
        }
      );
  }
  private getCheckboxState(notificationType: string): boolean {
    switch (notificationType) {
      case "Transaction_Confirmation":
        return this.transactionConfirmationChecked;
      case "Account_Balance_Updates":
        return this.accountBalanceUpdatesChecked;
      case "Low_balance_alert":
        return this.lowBalanceAlertChecked;
      case "Security_Alerts":
        return this.securityAlertsChecked;
      case "Account_block_alerts":
        return this.accountBlockAlertsChecked;
      default:
        return false;
    }
  }
}