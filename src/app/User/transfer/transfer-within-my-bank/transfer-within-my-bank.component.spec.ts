import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TransferWithinMyBankComponent } from './transfer-within-my-bank.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TransactionService } from 'src/app/Services/transaction.service';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import { of } from 'rxjs';
import Swal from 'sweetalert2'; // Import Swal directly

describe('TransferWithinMyBankComponent', () => {
  let component: TransferWithinMyBankComponent;
  let fixture: ComponentFixture<TransferWithinMyBankComponent>;
  let transactionServiceMock: jest.Mocked<TransactionService>;
  let accountServiceMock: jest.Mocked<AccountService>;
  let userServiceMock: jest.Mocked<UserService>;

  beforeEach(async () => {
    const transactionServiceSpyObj = {
      withinMyBank: jest.fn()
    };
    const accountServiceSpyObj = {
      getCustomerDetails: jest.fn()
    };
    const userServiceSpyObj = {
      userId$: of('12345')
    };

    await TestBed.configureTestingModule({
      declarations: [ TransferWithinMyBankComponent ],
      imports: [ ReactiveFormsModule ],
      providers: [
        FormBuilder,
        { provide: TransactionService, useValue: transactionServiceSpyObj },
        { provide: AccountService, useValue: accountServiceSpyObj },
        { provide: UserService, useValue: userServiceSpyObj }
      ]
    })
    .compileComponents();

    transactionServiceMock = TestBed.inject(TransactionService) as jest.Mocked<TransactionService>;
    accountServiceMock = TestBed.inject(AccountService) as jest.Mocked<AccountService>;
    userServiceMock = TestBed.inject(UserService) as jest.Mocked<UserService>;

    // Mock Swal.fire before creating the component
    jest.spyOn(Swal, 'fire').mockImplementation();
  });

  afterEach(() => {
    jest.clearAllMocks(); // Clear all mocks after each test
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransferWithinMyBankComponent);
    component = fixture.componentInstance;
  
    // Initialize BenDetails with mock data
    component.BenDetails = [{
      ben_name: 'Test Beneficiary',
      benaccountnumber: '987654'
    }];
  
    fixture.detectChanges();
  });
  

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form on ngOnInit', () => {
    const formControls = component.transferForm.controls;
    expect(formControls.fromAccount).toBeDefined();
    expect(formControls.currencyAmount).toBeDefined();
    expect(formControls.transferAmount).toBeDefined();
    expect(formControls.transactionNote).toBeDefined();
    expect(formControls.budgetTag).toBeDefined();
    expect(formControls.scheduleTransfer).toBeDefined();
  });

  it('should update UI on successful customer details retrieval', () => {
    const response = {
      statusCode: 200,
      data: { /* mocked customer data */ },
      accounts: [ /* mocked customer accounts */ ]
    };
  
    // Configure accountServiceMock to return the expected response
    accountServiceMock.getCustomerDetails.mockReturnValue(of(response));
  
    // Trigger ngOnInit to retrieve customer details
    component.ngOnInit();
  
    // Assert that the component's properties are correctly updated
    expect(component.customerDetails).toEqual(response.data);
    expect(component.customerAccounts).toEqual(response.accounts);
  });
  
  

  it('should handle transfer button click', () => {
    const mockFormValue = {
      fromAccount: '123456',
      currencyAmount: 'INR',
      transferAmount: 100,
      transactionNote: 'Test note',
      budgetTag: 'food',
      scheduleTransfer: ''
    };
    component.transferForm.setValue(mockFormValue);
    const selectedBeneficiary = { ben_name: 'Test Beneficiary', benaccountnumber: '987654' };
    component.BenDetails = [selectedBeneficiary];

    const mockTransactionResponse = { status: 200 };
    transactionServiceMock.withinMyBank.mockReturnValue(of(mockTransactionResponse));

    const consoleLogSpy = jest.spyOn(console, 'log');

    component.transferbtn(new MouseEvent('click'));

    expect(consoleLogSpy).toHaveBeenCalledWith('Transfer button clicked');
    expect(consoleLogSpy).toHaveBeenCalledWith('Form validity:', true);
    expect(consoleLogSpy).toHaveBeenCalledWith('fromAccount validity:', true);
    expect(consoleLogSpy).toHaveBeenCalledWith('currencyAmount validity:', true);
    expect(consoleLogSpy).toHaveBeenCalledWith('transferAmount validity:', true);
    expect(transactionServiceMock.withinMyBank).toHaveBeenCalledWith(expect.objectContaining({
      fromAccountNumber: mockFormValue.fromAccount,
      beneficiaryName: [{ beneficiaryAccountNumber: selectedBeneficiary.benaccountnumber }],
      transferAmount: mockFormValue.transferAmount,
      transactionNote: mockFormValue.transactionNote,
      transactionCategory: mockFormValue.budgetTag
    }));
    expect(Swal.fire).toHaveBeenCalledWith(expect.objectContaining({
      icon: 'success',
      title: 'Transaction Successfull!',
      showConfirmButton: false,
      timer: 3000
    }));
  });
});
