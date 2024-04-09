import { TestBed } from '@angular/core/testing';
import { CloseAccountComponent } from './close-account.component';
import { AccountService } from 'src/app/Services/account.service';
import { TransactionService } from 'src/app/Services/transaction.service';
import { of } from 'rxjs';

describe('CloseAccountComponent', () => {
  let component: CloseAccountComponent;
  let mockAccountService: jest.Mocked<AccountService>;
  let mockTransactionService: jest.Mocked<TransactionService>;
  beforeEach(async () => {
    const accountServiceSpy = {
        getAccountDetails: jest.fn(),
        getTicketDetails: jest.fn(),
        closeAccount: jest.fn(),
        fetchTicketIds: jest.fn().mockReturnValue(of({ status: 200, data: [1, 2, 3] })), // Mocking fetchTicketIds
        updateTicket: jest.fn()
      };
      

    const transactionServiceSpy = {
      withinSelfAccount: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [CloseAccountComponent],
      providers: [
        { provide: AccountService, useValue: accountServiceSpy },
        { provide: TransactionService, useValue: transactionServiceSpy }
      ]
    }).compileComponents();

    mockAccountService = TestBed.inject(AccountService) as jest.Mocked<AccountService>;
    mockTransactionService = TestBed.inject(TransactionService) as jest.Mocked<TransactionService>;
    component = new CloseAccountComponent(mockAccountService, mockTransactionService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getAccountDetails on ngOnInit', async () => {
    const mockResponse = { accountstatus: 'active', totalbalance: '1000' };
    mockAccountService.getAccountDetails.mockReturnValue(of(mockResponse));
  
    // Call ngOnInit
    component.ngOnInit();
  
    // Wait for any asynchronous operations to complete
    await new Promise(resolve => setTimeout(resolve));
  
    // Assert that getAccountDetails was called
    expect(mockAccountService.getAccountDetails).toHaveBeenCalled();
  
    // Assert other expectations
    expect(component.accountStatus).toEqual(mockResponse.accountstatus);
    expect(component.availableBalance).toEqual(mockResponse.totalbalance);
  });
  
  

  it('should call getTicketDetails and update ticket details on selecting a ticket', async () => {
    const mockTicketId = 123;
    const mockTicketDetails = { accountNumber: 456, recipientAccountNumber: 789 }; // Ensure mockTicketDetails is properly defined
    mockAccountService.getTicketDetails.mockReturnValue(of({ status: 200, data: mockTicketDetails }));
  
    const mockEvent = { target: { value: mockTicketId } };
    component.onSelectTicketId(mockEvent);
  
    expect(mockAccountService.getTicketDetails).toHaveBeenCalled();
    expect(component.accountNumber).toEqual(mockTicketDetails.accountNumber);
    expect(component.recipientAccountNumber).toEqual(mockTicketDetails.recipientAccountNumber);
  });
  
  it('should transfer funds and close account when transfer is successful', async () => {
    const mockResponse = { status: 200 };
    mockTransactionService.withinSelfAccount.mockReturnValue(of(mockResponse));
    
    // Spy on the closeAccount method
    const closeAccountSpy = jest.spyOn(component, 'closeAccount');
    
    // Call the method that triggers transfer and close
    component.accountNumber = 123;
    component.recipientAccountNumber = "456";
    component.availableBalance = '1000';
    
    // Call the method and wait for it to complete
    await component.transferFundsAndCloseAccount();
    
    // Assert that the methods were called
    expect(mockTransactionService.withinSelfAccount).toHaveBeenCalled();
    expect(closeAccountSpy).toHaveBeenCalled(); // Use the spy here instead of component.closeAccount
  });
  
  
  

  it('should not close account if transfer fails', () => {
    const mockResponse = { status: 500 };
    mockTransactionService.withinSelfAccount.mockReturnValue(of(mockResponse));

    jest.spyOn(component, 'closeAccount');
    component.transferFundsAndCloseAccount();

    expect(component.closeAccount).not.toHaveBeenCalled();
  });

  it('should update ticket status on closing account', () => {
    const mockResponse = { statusCode: 200 };
    mockAccountService.closeAccount.mockReturnValue(of(mockResponse));

    jest.spyOn(component, 'updateTicket');
    component.accountNumber = 123;

    component.closeAccount();

    expect(mockAccountService.closeAccount).toHaveBeenCalled();
    expect(component.updateTicket).toHaveBeenCalled();
  });
});
