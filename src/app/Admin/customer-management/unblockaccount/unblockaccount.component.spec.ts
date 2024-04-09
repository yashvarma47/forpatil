import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { Observable, of, throwError } from 'rxjs';
import { Router } from '@angular/router';

import { UnblockaccountComponent } from './unblockaccount.component';
import { AccountService } from 'src/app/Services/account.service';
import { CustomerService } from 'src/app/Services/customer.service';

describe('UnblockaccountComponent', () => {
  let component: UnblockaccountComponent;
  let accountService: AccountService;
  let customerService: CustomerService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UnblockaccountComponent],
      imports: [RouterTestingModule, HttpClientTestingModule, FormsModule],
      providers: [
        AccountService,
        CustomerService,
        { provide: Router, useClass: class { navigateByUrl = jest.fn(); } } // Mocking Router
      ],
    }).compileComponents();

    accountService = TestBed.inject(AccountService);
    customerService = TestBed.inject(CustomerService);
    router = TestBed.inject(Router); // Injecting Router
  });

  beforeEach(() => {
    const fixture = TestBed.createComponent(UnblockaccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch blocked accounts on initialization', () => {
    const blockedAccounts = [{ /* Sample blocked account data */ }];
    jest.spyOn(accountService, 'getAllBlockedAccounts').mockReturnValue(of({ status: 200, data: blockedAccounts }));

    component.ngOnInit();

    expect(accountService.getAllBlockedAccounts).toHaveBeenCalled();
    expect(component.accounts).toEqual(blockedAccounts);
  });

  it('should fetch blocked accounts by customer ID', () => {
    const customerId = '12345678';
    const blockedAccounts = [{ /* Sample blocked account data */ }];
    jest.spyOn(accountService, 'getBlockedAccountsByCustomerId').mockReturnValue(of({ status: 200, data: blockedAccounts }));

    component.selectedOption = 'Customer ID';
    component.customerSearchInput = customerId;
    component.onSearchChange();

    expect(accountService.getBlockedAccountsByCustomerId).toHaveBeenCalledWith(customerId);
    expect(component.accounts).toEqual(blockedAccounts);
  });

  it('should fetch blocked accounts by account number', () => {
    const accountNumber = '12345678901';
    const blockedAccount = { /* Sample blocked account data */ };
    jest.spyOn(accountService, 'getBlockedAccountsByAccountNumber').mockReturnValue(of({ status: 200, accountDetails: blockedAccount }));

    component.selectedOption = 'Account Number';
    component.customerSearchInput = accountNumber;
    component.onSearchChange();

    expect(accountService.getBlockedAccountsByAccountNumber).toHaveBeenCalledWith(accountNumber);
    expect(component.accounts).toEqual([blockedAccount]);
  });

  it('should reset input field and update view on dropdown options change', () => {
    component.selectedOption = 'Customer ID';
    component.customerSearchInput = '12345678';
    const consoleSpy = jest.spyOn(console, 'log');

    component.onDropdownoptions();

    expect(component.customerSearchInput).toBe('');
    expect(consoleSpy).toHaveBeenCalledWith('Selected option:', 'Customer ID');
  });

  it('should fetch blocked accounts by customer ID when selectedOption is Customer ID and customerSearchInput length is 8', () => {
    const customerId = '12345678';
    const blockedAccounts = [{ /* Sample blocked account data */ }];
    const mockResponse = { status: 200, data: blockedAccounts };
    jest.spyOn(accountService, 'getBlockedAccountsByCustomerId').mockReturnValue(of(mockResponse));
    component.selectedOption = 'Customer ID';
    component.customerSearchInput = customerId;

    component.onSearchChange();

    expect(accountService.getBlockedAccountsByCustomerId).toHaveBeenCalledWith(customerId);
    expect(component.accounts).toEqual(blockedAccounts);
  });

  it('should fetch blocked accounts by account number when selectedOption is Account Number and customerSearchInput length is 11', () => {
    const accountNumber = '12345678901';
    const blockedAccount = { /* Sample blocked account data */ };
    const mockResponse = { status: 200, accountDetails: blockedAccount };
    jest.spyOn(accountService, 'getBlockedAccountsByAccountNumber').mockReturnValue(of(mockResponse));
    component.selectedOption = 'Account Number';
    component.customerSearchInput = accountNumber;

    component.onSearchChange();

    expect(accountService.getBlockedAccountsByAccountNumber).toHaveBeenCalledWith(accountNumber);
    expect(component.accounts).toEqual([blockedAccount]);
  });

  it('should fetch all blocked accounts when neither Customer ID nor Account Number criteria is met', () => {
    const blockedAccounts = [{ /* Sample blocked account data */ }];
    const mockResponse = { status: 200, data: blockedAccounts };
    jest.spyOn(accountService, 'getAllBlockedAccounts').mockReturnValue(of(mockResponse));
    component.selectedOption = 'Invalid Option';
    component.customerSearchInput = ''; // Resetting input field

    component.onSearchChange();

    expect(accountService.getAllBlockedAccounts).toHaveBeenCalled();
    expect(component.accounts).toEqual(blockedAccounts);
  });

  it('should fetch blocked accounts successfully', () => {
    const blockedAccounts = [{ /* Sample blocked account data */ }];
    const mockResponse = { status: 200, data: blockedAccounts };
    jest.spyOn(accountService, 'getAllBlockedAccounts').mockReturnValue(of(mockResponse));

    component.getBlockedAccounts();

    expect(accountService.getAllBlockedAccounts).toHaveBeenCalled();
    expect(component.accounts).toEqual(blockedAccounts);
  });

  it('should handle API error while fetching blocked accounts', () => {
    const mockError = 'Error fetching blocked accounts';
    jest.spyOn(accountService, 'getAllBlockedAccounts').mockReturnValue(throwError(mockError));

    component.getBlockedAccounts();

    expect(accountService.getAllBlockedAccounts).toHaveBeenCalled();
    expect(console.error).toHaveBeenCalledWith('API Error:', mockError);
  });

  it('should set showCustomerDetails to true and showAccountDetails to false when onCustomerBtnClick is called', () => {
    component.onCustomerBtnClick();

    expect(component.showCustomerDetails).toBe(true);
    expect(component.showAccountDetails).toBe(false);
  });

  it('should set showCustomerDetails to false and showAccountDetails to true when onAccountBtnClick is called', () => {
    component.onAccountBtnClick();

    expect(component.showCustomerDetails).toBe(false);
    expect(component.showAccountDetails).toBe(true);
  });

  it('should navigate to "/block-account" when onBlockAccountClick is called', () => {
    component.onBlockAccountClick();

    expect(router.navigateByUrl).toHaveBeenCalledWith('/block-account');
  });

  it('should navigate to "/unblockpopup" with accountNumber and accountStatus when onUnblockAccount is called', () => {
    const account = { accountnumber: '123', accountstatus: 'BLOCKED' };
    component.onUnblockAccount(account);

    expect(router.navigate).toHaveBeenCalledWith(['/unblockpopup', { accountNumber: account.accountnumber, accountStatus: account.accountstatus }]);
  });

  it('should return "green" for status "ACTIVE"', () => {
    const color = component.getAccountStatusColor('ACTIVE');
    expect(color).toBe('green');
  });

  it('should return "red" for status "BLOCKED"', () => {
    const color = component.getAccountStatusColor('BLOCKED');
    expect(color).toBe('red');
  });

  it('should return "orange" for status "CREDITBLOCKED"', () => {
    const color = component.getAccountStatusColor('CREDITBLOCKED');
    expect(color).toBe('orange');
  });

  it('should return "orange" for status "DEBITBLOCKED"', () => {
    const color = component.getAccountStatusColor('DEBITBLOCKED');
    expect(color).toBe('orange');
  });

  it('should return empty string for unknown status', () => {
    const color = component.getAccountStatusColor('UNKNOWN_STATUS');
    expect(color).toBe('');
  });
  // Add more test cases to cover other methods and scenarios as needed
});
