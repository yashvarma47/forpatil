import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TransferWithinMyAccountComponent } from './transfer-within-my-account.component';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import { TransactionService } from 'src/app/Services/transaction.service';
import { FormBuilder } from '@angular/forms';
import Swal from 'sweetalert2';
import { of } from 'rxjs';
import * as rxjs from 'rxjs';
import { HttpClientModule } from '@angular/common/http';

describe('TransferWithinMyAccountComponent', () => {
    let component: TransferWithinMyAccountComponent;
    let fixture: ComponentFixture<TransferWithinMyAccountComponent>;
    let accountService: AccountService;
    let userService: UserService;
    let transactionService: TransactionService;
    let fb: FormBuilder;
  
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [TransferWithinMyAccountComponent],
        providers: [
          AccountService,
          UserService,
          TransactionService,
          FormBuilder
        ],
        imports: [HttpClientModule] // Include HttpClientModule here
      }).compileComponents();
    });
  
    beforeEach(() => {
        fixture = TestBed.createComponent(TransferWithinMyAccountComponent);
        component = fixture.componentInstance;
        accountService = TestBed.inject(AccountService);
        userService = TestBed.inject(UserService);
        transactionService = TestBed.inject(TransactionService);
        fb = TestBed.inject(FormBuilder);
      
        // Mock the necessary dependencies
        jest.spyOn(localStorage, 'getItem').mockReturnValue('');
        jest.spyOn(localStorage, 'setItem');
        jest.spyOn(userService, 'userId$', 'get').mockReturnValue(of('1'));
        jest.spyOn(accountService, 'getCustomerDetails').mockReturnValue(of({
        data: {},
        accounts: [{ accountnumber: '123' }]
        }));


      
        fixture.detectChanges();
      });
      
  
    it('should create', () => {
      expect(component).toBeTruthy();
    });

  it('should initialize transferForm', () => {
    expect(component.transferForm).toBeDefined();
  });

  it('should update UI on initialization', () => {
    expect(component.dropdownOptionsFrom.length).toBe(1);
    expect(component.dropdownOptionsTo.length).toBe(0);
  });

  it('should update "To Account" dropdown when "From Account" changes', () => {
    component.selectedFromAccount = '123';
    component.onFromAccountChange();
    expect(component.dropdownOptionsTo.length).toBe(0); // Assuming the selectedFromAccount doesn't match any other account
  });

  it('should reset form on cancel button click', () => {
    const resetSpy = spyOn(component.transferForm, 'reset');
    component.cancelbtn(new Event('click'));
    expect(resetSpy).toHaveBeenCalled();
  });

  it('should handle transfer button click with valid form', () => {
    const validForm = {
      fromAccount: '123',
      toAccount: '456',
      currencyAmount: 'INR',
      transferAmount: 100,
      transactionNote: 'Test transfer',
      budgetTag: 'food',
      scheduleTransfer: ''
    };
    component.transferForm.setValue(validForm);

    const withinSelfAccountSpy = spyOn(transactionService, 'withinSelfAccount').and.returnValue(of({ status: 200 }));
    const swalFireSpy = spyOn(Swal, 'fire');

    component.transferbtn(new Event('click'));

    expect(withinSelfAccountSpy).toHaveBeenCalled();
    expect(swalFireSpy).toHaveBeenCalledWith({
      icon: 'success',
      title: 'Transaction Successfull!',
      showConfirmButton: false,
      timer: 3000,
    });
  });

  it('should handle transfer button click with invalid form', () => {
    component.transferForm.setErrors({ invalid: true });

    const withinSelfAccountSpy = spyOn(transactionService, 'withinSelfAccount');
    const swalFireSpy = spyOn(Swal, 'fire');

    component.transferbtn(new Event('click'));

    expect(withinSelfAccountSpy).not.toHaveBeenCalled();
    expect(swalFireSpy).toHaveBeenCalledWith({
      icon: 'error',
      title: 'Please Enter Required Detais',
      showConfirmButton: false,
      timer: 3000,
    });
  });
});
