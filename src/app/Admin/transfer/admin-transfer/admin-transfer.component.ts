import { Component, Input } from '@angular/core';
import { TransactionService } from 'src/app/Services/transaction.service';
import Swal from 'sweetalert2';
import { state } from '@angular/animations';
 
@Component({
  selector: 'app-admin-transfer',
  templateUrl: './admin-transfer.component.html',
  styleUrls: ['./admin-transfer.component.css']
})
export class AdminTransferComponent {
  @Input() customerId: number | null = null;
  @Input() account_number: number | null = null;
  transactionType: string = 'Credit';
  amount: number | null = null;
  date: string = this.getCurrentDateFormatted();
  narratives: string = '';
 
  constructor(private transactionService: TransactionService) {}

  isValidAmount(): boolean {
    if (this.amount === null || this.amount === 0) return false;
   
    const amountString: string = this.amount.toString();
    return /^\d+(\.\d{1,2})?$/.test(amountString);
}

  limitTo8Digits(event: any) {
    const input = event.target;
    let value = input.value;
 
    value = value.replace(/\D/g, '');
 
    if (value.length > 8) {
      value = value.slice(0, 8);
    }
 
    input.value = value;
  }
  limitTo11Digits(event: any) {
    const input = event.target;
    let value = input.value;
 
    value = value.replace(/\D/g, '');
 
    if (value.length > 11) {
      value = value.slice(0, 11);
    }
 
    input.value = value;
  }
  onSubmit() {
    if (this.amount !== null && this.amount < 0) {
      Swal.fire({
        icon: 'error',
        title: 'Invalid Amount',
        text: 'Amount should not be less than 0.',
        showConfirmButton: false,
        timer: 3000,
      });
      return;
    }
 
    const transactionRequest = {
      customerId: this.customerId,
      accountNumber: this.account_number,
      transactionAmount: this.amount,
      transactionType: this.transactionType,
      narratives: this.narratives
    };
 
    
    this.transactionService.createTransaction(transactionRequest).subscribe(
      
      (response: any) => {
        
 
        if (response.status === 200) {
          Swal.fire({
            icon: 'success',
            title: 'Transaction successful!',
            showConfirmButton: true,
            // timer: 3000,
          });
        } 
        else {
          Swal.fire({
            icon: 'error',
            title: 'SomeThing Want Wrong!',
            showConfirmButton: true,
            // timer: 3000,
          });
          
          
        }
      },
      (error) => {
        // console.error('Error creating transaction:', error);
        if(error.error.faultCode === "ERR-RBA-1001"){
          Swal.fire({
            icon: 'error',
            title: 'Insufficient Balance',
            showConfirmButton: true,
            // timer: 3000,
          });
        } else if(error.error.faultCode === "ERR-RBA-100C&DB"){
          Swal.fire({
            icon: 'error',
            title: 'Transaction Failed!\nAccount is blocked for all type of transactions (CREDIT/DEBIT)!',
            showConfirmButton: true,
            // timer: 3000,
          });
        } else if(error.error.faultCode === "ERR-RBA-100CB"){
          Swal.fire({
            icon: 'error',
            title: 'Transaction Failed!\nAccount is blocked for all CREDIT transactions!',
            showConfirmButton: true,
            // timer: 3000,
          });
        }else if(error.error.faultCode === "ERR-RBA-100DB"){
          Swal.fire({
            icon: 'error',
            title: 'Transaction Failed!\nAccount is blocked for all DEBIT transactions!',
            showConfirmButton: true,
            // timer: 3000,
          });
        }else if (error && error.error && error.error.details) {
          error.error.details.forEach((detail: { field: string, message: string }) => {
              if (detail.field === "transactionAmount" && detail.message === "must be greater than 0") {
                  Swal.fire({
                      icon: 'error',
                      title: 'Transaction Failed!\nAmount must be greater than 0',
                      showConfirmButton: true,
                      // timer: 3000,
                  });
              }
          });
      }else{
          Swal.fire({
            icon: 'error',
            title: 'Verify Your Details!',
            showConfirmButton: true,
            // timer: 3000,
          });
          console.error("error");
        }
      }
    );
  }
 
 
  getCurrentDateFormatted(): string {
    const currentDate = new Date();
    const day = currentDate.getDate();
    const month = currentDate.getMonth() + 1;
    const year = currentDate.getFullYear();
    return `${day}/${month}/${year}`;
  }
}