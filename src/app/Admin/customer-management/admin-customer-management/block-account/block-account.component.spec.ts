import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BlockAccountComponent } from './block-account.component';
import { AccountService } from 'src/app/Services/account.service';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { of, throwError } from 'rxjs';

describe('BlockAccountComponent', () => {
  let component: BlockAccountComponent;
  let fixture: ComponentFixture<BlockAccountComponent>;
  let accountService: AccountService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BlockAccountComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        {
          provide: AccountService,
          useValue: {
            blockAccount: jest.fn()
          }
        }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BlockAccountComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should limit account number input to 11 digits', () => {
    const event = { target: { value: '1234567890123' } };
    component.limitTo11Digits(event as any);
    expect(event.target.value).toBe('12345678901');
  });

  it('should submit block account form', () => {
    const mockFormValue = {
      accountNumber: '123456789012',
      blockType: 'CREDIT',
      reason: 'Test reason'
    };

    component.OpenBlockForm.setValue(mockFormValue);

    const blockAccountSpy = jest.spyOn(accountService, 'blockAccount').mockReturnValue(of({ statusCode: 200, statusMessage: 'Success' }));
    const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();

    component.submitbtn(new Event('click'));

    expect(blockAccountSpy).toHaveBeenCalledWith('123456789012', 'CREDIT', 'Test reason');
    expect(swalFireSpy).toHaveBeenCalledWith('Success', 'Success', 'success');
  });

  // Add more test cases as needed
});
