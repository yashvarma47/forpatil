import { Component, EventEmitter, Input, Output } from '@angular/core';
 
@Component({
  selector: 'app-deny-confirmation',
  templateUrl: './deny-confirmation.component.html',
  styleUrls: ['./deny-confirmation.component.css']
})
export class DenyConfirmationComponent {
  @Input() isPopupOpen: boolean = false;
  @Output() isUpdateTicket: boolean= false;
 
  reason: string = '';
 
  @Output() submitReason: EventEmitter<string> = new EventEmitter<string>();
  @Output() cancel: EventEmitter<void> = new EventEmitter<void>();
 
  submit() {
    this.submitReason.emit(this.reason);
    this.isUpdateTicket=true;
  }
 
 
  // // No change needed in cancelPopup() method
cancelPopup() {
  
  
  this.isPopupOpen=false;
  this.cancel.emit();
}
}