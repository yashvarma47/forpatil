import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TransactionSummaryComponent } from './transaction-summary.component';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import { TransactionService } from 'src/app/Services/transaction.service';
import { DatePipe } from '@angular/common';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { of } from 'rxjs';

describe('TransactionSummaryComponent', () => {
  let component: TransactionSummaryComponent;
  let fixture: ComponentFixture<TransactionSummaryComponent>;
  let accountServiceMock: jest.Mocked<AccountService>;
  let userServiceMock: jest.Mocked<UserService>;
  let transactionServiceMock: jest.Mocked<TransactionService>;

  beforeEach(async () => {
    const accountServiceSpy = {
      getCustomerDetails: jest.fn()
    };
    const userServiceSpy = {
      userId$: of('12345')
    };
    const transactionServiceSpy = {
      getTransactionBetweenTwoDates: jest.fn(),
      getLatestTransactions: jest.fn(),
      getMonthlyTransactions: jest.fn(),
      getYearlyTransactions: jest.fn(),
      getQuarterlyTransactions: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [TransactionSummaryComponent],
      providers: [
        { provide: AccountService, useValue: accountServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: TransactionService, useValue: transactionServiceSpy },
        DatePipe,
        FormBuilder
      ]
    }).compileComponents();

    accountServiceMock = TestBed.inject(AccountService) as jest.Mocked<AccountService>;
    userServiceMock = TestBed.inject(UserService) as jest.Mocked<UserService>;
    transactionServiceMock = TestBed.inject(TransactionService) as jest.Mocked<TransactionService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update UI on successful customer details retrieval', () => {
    const response = {
      statusCode: 200,
      data: { /* mocked customer data */ },
      accounts: [ /* mocked customer accounts */ ]
    };
    accountServiceMock.getCustomerDetails.mockReturnValue(of(response));

    component.ngOnInit();

    expect(component.customerDetails).toEqual(response.data);
    expect(component.customerAccounts).toEqual(response.accounts);
  });

  it('should filter transactions by recent period', () => {
    const chooseAccNo = '123';
    const chooseAcc = 'savings';
    component.chooseAccNo = chooseAccNo;
    component.chooseAcc = chooseAcc;
  
    const responseData: any[] = [ /* mocked transaction data */ ];
    transactionServiceMock.getLatestTransactions.mockReturnValue(of({ status: 200, data: responseData }));
  
    component.filterRecentTransactions();
  
    expect(transactionServiceMock.getLatestTransactions).toHaveBeenCalledWith(parseInt(chooseAccNo), chooseAcc);
    expect(component.statementData).toEqual(responseData);
    expect(component.dataSource).toEqual(responseData); // Use toEqual instead of toBe
  });

  it('should return the current date in the format YYYY-MM-DD', () => {
    // Mock the current date to a specific value
    const mockDate = new Date('2022-06-15T12:00:00Z');
    jest.spyOn(global, 'Date').mockImplementation(() => mockDate);
  
    // Call the method
    const currentDate = component.getCurrentDate();
  
    // Assert the result
    expect(currentDate).toEqual('2022-06-15');
  });
  
  it('should return the current date for different date', () => {
    // Mock the current date to a specific value
    const mockDate = new Date('2023-03-22T08:00:00Z');
    jest.spyOn(global, 'Date').mockImplementation(() => mockDate);
  
    // Call the method
    const currentDate = component.getCurrentDate();
  
    // Assert the result
    expect(currentDate).toEqual('2023-03-22');
  });
  
  it('should call filterTransactions when onEndDateSelected is called', () => {
    // Arrange
    const filterTransactionsSpy = jest.spyOn(component, 'filterTransactions');
    
    // Act
    component.onEndDateSelected();
    
    // Assert
    expect(filterTransactionsSpy).toHaveBeenCalled();
  });
  
  it('should call filterRecentTransactions when transactionFilter is "Recent"', () => {
    // Arrange
    const filterRecentTransactionsSpy = jest.spyOn(component, 'filterRecentTransactions');
    component.transactionFilter = 'Recent';
    
    // Act
    component.onFilterChange();
    
    // Assert
    expect(filterRecentTransactionsSpy).toHaveBeenCalled();
  });
  
  it('should call filterMonthlyTransactions when transactionFilter is "Monthly"', () => {
    // Arrange
    const filterMonthlyTransactionsSpy = jest.spyOn(component, 'filterMonthlyTransactions');
    component.transactionFilter = 'Monthly';
    
    // Act
    component.onFilterChange();
    
    // Assert
    expect(filterMonthlyTransactionsSpy).toHaveBeenCalled();
  });
  
  it('should call filterYearlyTransactions when transactionFilter is "Yearly"', () => {
    // Arrange
    const filterYearlyTransactionsSpy = jest.spyOn(component, 'filterYearlyTransactions');
    component.transactionFilter = 'Yearly';
    
    // Act
    component.onFilterChange();
    
    // Assert
    expect(filterYearlyTransactionsSpy).toHaveBeenCalled();
  });
  
  it('should call filterQuarterlyTransactions when transactionFilter is "Quarterly"', () => {
    // Arrange
    const filterQuarterlyTransactionsSpy = jest.spyOn(component, 'filterQuarterlyTransactions');
    component.transactionFilter = 'Quarterly';
    
    // Act
    component.onFilterChange();
    
    // Assert
    expect(filterQuarterlyTransactionsSpy).toHaveBeenCalled();
  });
  

  

  // Add more test cases as needed

});
