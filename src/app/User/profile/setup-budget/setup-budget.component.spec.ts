import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { SetupBudgetComponent, BudgetSetupDTO } from './setup-budget.component';
import { of, throwError } from 'rxjs';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import { TransactionService } from 'src/app/Services/transaction.service';
import Swal from 'sweetalert2';
import { HttpClientModule } from '@angular/common/http';

jest.mock('sweetalert2', () => ({
  fire: jest.fn().mockResolvedValue({}) // Mocking Swal.fire to return a resolved Promise
}));

describe('SetupBudgetComponent', () => {
  let component: SetupBudgetComponent;
  let fixture: ComponentFixture<SetupBudgetComponent>;
  let accountService: AccountService;
  let userService: UserService;
  let transactionService: TransactionService;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SetupBudgetComponent],
      imports: [ReactiveFormsModule, HttpClientModule], // Import HttpClientModule
      providers: [AccountService, UserService, TransactionService, FormBuilder]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SetupBudgetComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    userService = TestBed.inject(UserService);
    transactionService = TestBed.inject(TransactionService);
    formBuilder = TestBed.inject(FormBuilder);
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form and fetch data on ngOnInit', () => {
    const mockData = {
      data: {
        // mock data object
      },
      accounts: [
        // mock account data
      ]
    };

    const accountServiceSpy = jest.spyOn(accountService, 'getCustomerDetails').mockReturnValue(of(mockData));
    const transactionServiceSpy = jest.spyOn(transactionService, 'getBudgetLimit').mockReturnValue(of({ data: {} }));

    component.ngOnInit();

    expect(accountServiceSpy).toHaveBeenCalled();
    expect(transactionServiceSpy).toHaveBeenCalled();
    expect(component.amountForm).toBeTruthy(); // Ensure form initialization
    // Add more expectations as needed
  });

  it('should toggle edit mode', () => {
    expect(component.editMode).toBeFalsy(); // Initially, editMode should be false

    component.toggleEditMode(); // Toggle edit mode

    expect(component.editMode).toBeTruthy(); // After toggling, editMode should be true

    component.toggleEditMode(); // Toggle edit mode again

    expect(component.editMode).toBeFalsy(); // After toggling again, editMode should be false
  });

  it('should submit form successfully', () => {
    // Set up form data
    const formData: BudgetSetupDTO = {
      month: 1,
      budgetThreshold: {
        food: 100,
        fuel: 200,
        travel: 300,
        bills: 400,
        entertainment: 500,
        loan: 600,
        recharge: 700,
        shopping: 800,
        miscellaneous: 900
      }
    };

    // Set up form controls
    component.amountForm = formBuilder.group({
      selectedAccount: ['123', []], // Set any initial values and validators here
      amounts: formBuilder.array([]) // Set any initial values and validators here
    });

    // Set up spy for transactionService.updateBudgetLimit method
    jest.spyOn(transactionService, 'updateBudgetLimit').mockReturnValue(of({ status: 200 }));

    // Set component properties
    component.selectedFromAccount = '123';
    component.budgetSetupDTO = formData;

    // Call the method to submit the form
    component.onSubmit();

    // Expect that transactionService.updateBudgetLimit method was called with the correct arguments
    expect(transactionService.updateBudgetLimit).toHaveBeenCalledWith(123, formData);

    // Add more expectations based on the behavior you expect from the component
  });

  it('should handle invalid form submission', () => {
    // Create a spy on the console.error method
    const consoleSpy = jest.spyOn(console, 'error');

    // Simulate an invalid form submission by submitting an empty form
    component.onSubmit();

    // Expect that console.error is called with the expected message
    expect(consoleSpy).toHaveBeenCalledWith('Form is invalid. Cannot submit.');

    // Add more expectations based on the behavior you expect from the component
  });

  it('should handle API error', () => {
    // Mocking the response from transactionService.getBudgetLimit
    jest.spyOn(transactionService, 'getBudgetLimit').mockReturnValue(throwError({ status: 500 }));

    // Mocking the value change event
    const mockEvent = { target: { value: 'selectedAccountId' } };

    // Triggering the account selection change
    component.onAccountSelectionChange(mockEvent);

    // Expectations
    expect(transactionService.getBudgetLimit).toHaveBeenCalledWith('selectedAccountId');
    // Add expectations for handling the API error (e.g., showing an error message)
  });

  it('should handle account selection change', () => {
    // Mocking the response from transactionService.getBudgetLimit
    const mockResponse = {
      status: 200,
      data: {
        food_threshold: 100,
        fuel_threshold: 200,
        travel_threshold: 300,
        bills_threshold: 400,
        entertainment_threshold: 500,
        loan_threshold: 600,
        recharge_threshold: 700,
        shopping_threshold: 800,
        miscellaneous_threshold: 900
      }
    };
    jest.spyOn(transactionService, 'getBudgetLimit').mockReturnValue(of(mockResponse));

    // Mocking the value change event
    const mockEvent = { target: { value: 'selectedAccountId' } };

    // Triggering the account selection change
    component.onAccountSelectionChange(mockEvent);

    // Expectations
    expect(transactionService.getBudgetLimit).toHaveBeenCalledWith('selectedAccountId');
    expect(component.categories).toEqual([
      'food', 'fuel', 'travel', 'bills', 'entertainment', 'loan', 'recharge', 'shopping', 'miscellaneous'
    ]);
    expect(component.amounts).toEqual([
      100, 200, 300, 400, 500, 600, 700, 800, 900
    ]);
  });
});
