import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AccountDetailsComponent } from './account-details.component';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import { of } from 'rxjs';
import { spyOn } from 'jest-mock';
import { fakeAsync, tick } from '@angular/core/testing';


describe('AccountDetailsComponent', () => {
  let component: AccountDetailsComponent;
  let fixture: ComponentFixture<AccountDetailsComponent>;
  let accountService: AccountService;
  let userService: UserService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AccountDetailsComponent],
      providers: [
        { provide: AccountService, useValue: { getCustomerDetails: () => of({}) } },
        { provide: UserService, useValue: { userId$: of('1') } }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountDetailsComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    userService = TestBed.inject(UserService);
    spyOn(component, 'updateUI');
    spyOn(component, 'onAccountTypeChange');
    spyOn(component, 'onAccountNumberChange');
    spyOn(component, 'getSelectedAccount');
    spyOn(component, 'updateAccountDetails');
  });
  
  
  
  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

// Inside the test case
it('should call necessary methods on component initialization', fakeAsync(() => {
    const getCustomerDetailsSpy = jest.spyOn(accountService, 'getCustomerDetails').mockReturnValue(of({
      data: {},
      accounts: [{ accounttype: 'Savings', accountnumber: '123', totalbalance: 1000, accountstatus: 'Active', primaryCard: '1234', spendingLimit: 500 }]
    }));
  
    // Trigger ngOnInit
    component.ngOnInit();
  
    // Simulate observable completion
    tick();
  
    // Expectations
    expect(getCustomerDetailsSpy).toHaveBeenCalled();
    expect(component.updateUI).toHaveBeenCalled();
    expect(component.onAccountTypeChange).toHaveBeenCalled();
    expect(component.onAccountNumberChange).toHaveBeenCalled();
  }));

  it('should update UI on account type change', () => {
    component.chooseAcc = 'Savings';
    component.onAccountTypeChange();
    expect(component.dropdownOptionschooseAccNo.length).toBeGreaterThan(0);
});


it('should update UI on account number change', () => {
    // Define a sample selectedAccount object with the required properties
    const selectedAccount = {
        totalbalance: 1000, // Ensure this property is set
        accountstatus: 'Active', // Ensure this property is set
        primaryCard: '1234', // Ensure this property is set
        spendingLimit: 500 // Ensure this property is set
    };

    // Spy on getSelectedAccount method to return the sample selectedAccount
    jest.spyOn(component, 'getSelectedAccount').mockReturnValue(selectedAccount);

    // Set the chooseAccNo to trigger the update
    component.chooseAccNo = '123';

    // Trigger the onAccountNumberChange method
    component.onAccountNumberChange();

    // Expectations
    expect(component.customerDetails.accountBalance).toBe(1000);
    expect(component.customerDetails.status).toBe('Active');
});


it('should get selected account correctly', () => {
    // Mock customerAccounts with sample data
    component.customerAccounts = [
        { accounttype: 'Savings', accountnumber: '123', totalbalance: 1000, accountstatus: 'Active', primaryCard: '1234', spendingLimit: 500 }
    ];

    // Call getSelectedAccount with the account number '123'
    const selectedAccount = component.getSelectedAccount('123');    

    // Expect that the selected account matches the expected object
    expect(selectedAccount).toEqual({ accounttype: 'Savings', accountnumber: '123', totalbalance: 1000, accountstatus: 'Active', primaryCard: '1234', spendingLimit: 500 });
});


  it('should update account details correctly', () => {
    const selectedAccount = { accounttype: 'Savings', accountnumber: '123', totalbalance: 1000, accountstatus: 'Active', primaryCard: '1234', spendingLimit: 500 };
    component.updateAccountDetails(selectedAccount);
    expect(component.customerDetails.accountBalance).toBe(1000);
    expect(component.customerDetails.status).toBe('Active');
  });
});
