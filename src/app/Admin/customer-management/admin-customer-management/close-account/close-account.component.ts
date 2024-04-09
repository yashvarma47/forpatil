import { Component, OnInit } from '@angular/core';
import { AccountService } from 'src/app/Services/account.service';
import { TransactionService } from 'src/app/Services/transaction.service';
import Swal from 'sweetalert2';
 
@Component({
  selector: 'app-close-account',
  templateUrl: './close-account.component.html',
  styleUrls: ['./close-account.component.css']
})
export class CloseAccountComponent implements OnInit {
 
  availableBalance = ""; // Assign a default value for demonstration
  accountNumber: any;
  isPopupOpen: boolean = false;
  ticketIds: number[] = [];
  accountStatus: string = "";
  recipientAccountNumber = "";
  ticketId = 0;
  ticketDetails: any = {
    ticketId: 0,
    ticketType: '',
    customerId: "",
    accountNumber: 0,
    accountType: '',
    recipientAccountNumber: "",
    ticketStatus: '',
    transferFund: '',
    feedback: ''
  };
  isUpdateTicket: boolean = false;
 
  constructor(private accountService: AccountService, private transactionService: TransactionService) { }
 
  ngOnInit(): void {
    this.fetchTicketIds();
  }
 
  getAccountDetails(accNo: number) {
    this.accountService.getAccountDetails(accNo).subscribe(
      (response: any) => {
        if (response) {
          this.accountStatus = response.accountstatus;
          this.availableBalance = response.totalbalance;
        }
      }
    );
  }
  onSelectTicketId(event: any) {
    const selectedValue = (event.target as HTMLSelectElement).value;
    if (selectedValue) {
      const ticketId = parseInt(selectedValue, 10); // Convert the selected value to a number
      // Call the service method to fetch ticket details based on the selected ticket ID
      this.accountService.getTicketDetails(ticketId).subscribe({
        next: (response: any) => {
          if (response && response.status === 200) {
            this.ticketDetails = response.data;
            this.accountNumber = this.ticketDetails.accountNumber;
            this.getAccountDetails(parseInt(this.accountNumber));
            this.recipientAccountNumber = this.ticketDetails.recipientAccountNumber;
          }
        },
        error: (error: any) => {
          console.error('Error fetching ticket details:', error);
        }
      });
    }
  }
 
  transferFunds(): boolean {
    const transferRequestDTO = {
      fromAccountNumber : parseInt(this.accountNumber),
      toAccountNumber : parseInt(this.recipientAccountNumber),
      transferAmount : this.availableBalance,
      transactionNote : "Transfer remaing balance of account number " + this.accountNumber
    };
 
    this.transactionService.withinSelfAccount(transferRequestDTO).subscribe(
      (response: any) => {
        if (response && response.status === 200) {
          
          this.closeAccount();
          return true;
        } else {
          return false;
        }
      },
      (error: any) => {
        console.error('Error transferring funds:', error);
      }
    );
    return false;
  }
 
  closeAccount() {
    
    this.accountService.closeAccount(this.accountNumber).subscribe(
      (response: any) => {
        // Check if the response is the success message
        if (response.statusCode === 200) {
          this.updateTicket("CLOSED");
          Swal.fire({
            icon: 'success',
            title: 'Account closed successfully. Email has been sent to the registered email address',
            showConfirmButton: false,
            timer: 3000,
          });
        } else if (response.emailStatus=== 'Failed to send email'){
          Swal.fire({
            icon: 'error',
            title: 'Account closed successfully. Failed to send email to the registered email address',
            showConfirmButton: false,
            timer: 3000,
          });
        }
      },
      (error: any) => {
        // Handle error
        console.error('Error closing account:', error);
      }
    );
  }
 
  transferFundsAndCloseAccount(): void {
    // Check if fund transfer is successful
    if (this.transferFunds()) {
      
      this.closeAccount();
    }
  }
 
  isBalanceGreaterThanZero(): boolean {
    return parseFloat(this.availableBalance) > 0;
  }
 
  openPopup() {
    this.isPopupOpen = true;
  }
 
  closePopup() {
    
 
    this.isPopupOpen = false;
  }
 
  fetchTicketIds() {
    this.accountService.fetchTicketIds().subscribe(
      (response: any) => {
        if (response && response.status === 200) {
          this.ticketIds = response.data;
        }
      },
      (error) => {
        console.error('Error fetching tickets:', error);
      }
    );
  }
 
  // Handle the submission of the reason here
  handleReasonSubmission(reason: string) {
    
    // Handle the submission of the reason here
    
    this.ticketDetails.feedback = "Account Closure Rejected. " + reason;
    this.updateTicket("REJECTED");
    // Close the popup
    this.closePopup();
    
    
  }
 
  updateTicket(status: string) {
 
    const ticketDetail: any = {
      ticketId: this.ticketDetails.ticketId,
      ticketType: this.ticketDetails.ticketType,
      customerId: parseInt(this.ticketDetails.customerId),
      accountNumber: this.ticketDetails.accountNumber,
      accountType: this.ticketDetails.accountType,
      recipientAccountNumber: parseInt(this.ticketDetails.recipientAccountNumber),
      ticketStatus: status,
      transferFund: this.ticketDetails.transferFund,
      feedback: this.ticketDetails.feedback
    };
    this.accountService.updateTicket(ticketDetail, this.ticketDetails.ticketId).subscribe(
      (response: any) => {
        if (response.status === 200) {
          
          Swal.fire({
            icon: 'success',
            title: 'Ticket updated successfully',
            showConfirmButton: false,
            timer: 3000,
          });
        }
        else {
          Swal.fire({
            icon: 'error',
            title: 'Failed to update Ticket!',
            showConfirmButton: false,
            timer: 3000,
          });
        }
      },
      (error) => {
        console.error('Error updating ticket!', error);
 
      }
    );
  }
}