import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserDashboardComponent } from './user-dashboard.component';
import { AccountService } from 'src/app/Services/account.service';
import { TransactionService } from 'src/app/Services/transaction.service';
import { UserService } from 'src/app/Services/user.service';
import { of } from 'rxjs';

describe('UserDashboardComponent', () => {
  let component: UserDashboardComponent;
  let fixture: ComponentFixture<UserDashboardComponent>;
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
      getExpensesByCategory: jest.fn(),
      getYearlyExpenses: jest.fn(),
      getMonthlyExpenses: jest.fn(),
      getWeeklyExpenses: jest.fn(),
      getTodaysOrYesterdaysExpenses: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [UserDashboardComponent],
      providers: [
        { provide: AccountService, useValue: accountServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: TransactionService, useValue: transactionServiceSpy },
      ]
    }).compileComponents();

    accountServiceMock = TestBed.inject(AccountService) as jest.Mocked<AccountService>;
    userServiceMock = TestBed.inject(UserService) as jest.Mocked<UserService>;
    transactionServiceMock = TestBed.inject(TransactionService) as jest.Mocked<TransactionService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should call prepareDataForLineChart when onOptionSelect is called', () => {
    // Arrange
    const prepareDataForLineChartSpy = jest.spyOn(component, 'prepareDataForLineChart');
    const selectedOption = 'fuel'; // Choose any option for testing
  
    // Act
    component.onOptionSelect(selectedOption);
  
    // Assert
    expect(prepareDataForLineChartSpy).toHaveBeenCalled();
    expect(component.selectedOption).toEqual(selectedOption);
  });

  it('should update expense properties correctly when updatePieChart is called', () => {
    // Arrange
    const testData = {
      total_food_expense: 50,
      total_fuel_expense: 30,
      total_travel_expense: 20,
      total_bills_expense: 40,
      total_entertainment_expense: 10,
      total_loan_expense: 5,
      total_recharge_expense: 15,
      total_shopping_expense: 25,
      total_miscellaneous_expense: 35
    };
  
    // Act
    component.updatePieChart(testData);
  
    // Assert
    expect(component.foodExpense).toEqual(50);
    expect(component.fuelExpense).toEqual(30);
    expect(component.travelExpense).toEqual(20);
    expect(component.billsExpense).toEqual(40);
    expect(component.entertainmentExpense).toEqual(10);
    expect(component.loanExpense).toEqual(5);
    expect(component.rechargeExpense).toEqual(15);
    expect(component.shoppingExpense).toEqual(25);
    expect(component.miscellaneousExpense).toEqual(35);
  });

  it('should prepare data for line chart when prepareDataForLineChart is called', () => {
    // Arrange
    component.chooseAccNo = '123'; // Set necessary properties
    component.accountType = 'savings';
    component.selectedOption = 'food';
    const testData = {
      '2024-02-21': 50,
      '2024-02-22': 30,
      '2024-02-23': 20
      // Add more data as needed
    };
    transactionServiceMock.getExpensesByCategory.mockReturnValue(of({ status: 200, data: testData }));

    // Act
    component.prepareDataForLineChart();

    // Assert
    expect(component.dates.length).toBeGreaterThan(0);
    expect(component.expenses.length).toBeGreaterThan(0);
    // Add more assertions if needed
  });

  it('should get yearly expenses and update pie chart when yearbudget is called', () => {
    // Arrange
    const response = { data: { /* Yearly expense data */ }, status: 200 };
    transactionServiceMock.getYearlyExpenses.mockReturnValue(of(response));
    const updatePieChartSpy = jest.spyOn(component, 'updatePieChart');

    // Act
    component.yearbudget();

    // Assert
    expect(transactionServiceMock.getYearlyExpenses).toHaveBeenCalled();
    expect(updatePieChartSpy).toHaveBeenCalledWith(response.data);
  });

  // Add similar tests for other methods like monthbudget, weekbudget, yesterdaybudget, todaybudget, etc.

});
