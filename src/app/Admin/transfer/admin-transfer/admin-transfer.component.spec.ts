import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminTransferComponent } from './admin-transfer.component';
import { TransactionService } from 'src/app/Services/transaction.service';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { of, throwError } from 'rxjs';

describe('AdminTransferComponent', () => {
  let component: AdminTransferComponent;
  let fixture: ComponentFixture<AdminTransferComponent>;
  let transactionService: TransactionService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminTransferComponent],
      imports: [FormsModule],
      providers: [
        {
          provide: TransactionService,
          useValue: {
            createTransaction: jest.fn()
          }
        }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminTransferComponent);
    component = fixture.componentInstance;
    transactionService = TestBed.inject(TransactionService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should limit customer ID input to 8 digits', () => {
    const event = { target: { value: '1234567890' } };
    component.limitTo8Digits(event as any);
    expect(event.target.value).toBe('12345678');
  });

  it('should limit account number input to 11 digits', () => {
    const event = { target: { value: '1234567890123' } };
    component.limitTo11Digits(event as any);
    expect(event.target.value).toBe('12345678901');
  });

  it('should submit transaction form', () => {
    component.customerId = 12345678;
    component.account_number = 12345678901;
    component.transactionType = 'credit';
    component.amount = 100;
    component.narratives = 'Test transaction';

    const createTransactionSpy = jest.spyOn(transactionService, 'createTransaction').mockReturnValue(of({ status: 200 }));

    const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();

    component.onSubmit();

    expect(createTransactionSpy).toHaveBeenCalledWith({
      customerId: 12345678,
      accountNumber: 12345678901,
      transactionAmount: 100,
      transactionType: 'credit',
      narratives: 'Test transaction'
    });

    expect(swalFireSpy).toHaveBeenCalledWith({
      icon: 'success',
      title: 'Transaction successful!',
      showConfirmButton: false,
      timer: 3000,
    });
  });

});
