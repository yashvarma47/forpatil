import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UnblockpopupComponent } from './unblockpopup.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from 'src/app/Services/account.service';
import { of, throwError, BehaviorSubject } from 'rxjs';
import Swal from 'sweetalert2';

describe('UnblockpopupComponent', () => {
  let component: UnblockpopupComponent;
  let fixture: ComponentFixture<UnblockpopupComponent>;
  let formBuilder: FormBuilder;
  let accountService: jest.Mocked<AccountService>;
  let activatedRoute: { params: BehaviorSubject<any> };

  beforeEach(async () => {
    formBuilder = new FormBuilder();

    activatedRoute = {
      params: new BehaviorSubject<any>({ accountNumber: '123456789012', accountStatus: 'BLOCKED' })
    };

    const accountServiceMock = {
      unblockAccount: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [UnblockpopupComponent],
      providers: [
        { provide: FormBuilder, useValue: formBuilder },
        { provide: AccountService, useValue: accountServiceMock },
        { provide: ActivatedRoute, useValue: activatedRoute }
      ]
    }).compileComponents();

    accountService = TestBed.inject(AccountService) as jest.Mocked<AccountService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UnblockpopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize form and load customer details', () => {
      expect(component.OpenBlockForm).toBeInstanceOf(FormGroup);
      expect(component.OpenBlockForm.get('accountNumber')!.value).toEqual('123456789012');
      expect(component.OpenBlockForm.get('blockType')!.value).toEqual('');
      expect(component.OpenBlockForm.get('reason')!.value).toEqual('');
      expect(accountService.unblockAccount).toHaveBeenCalledWith('123456789012', '', '');
    });
  });

  describe('limitTo11Digits', () => {
    it('should limit input to 11 digits', () => {
      const event = { target: { value: '123456789012345' } };
      component.limitTo11Digits(event);
      expect(event.target.value).toEqual('12345678901');
    });

    it('should update form control value', () => {
      const event = { target: { value: '12345678901' } };
      component.limitTo11Digits(event);
      expect(component.OpenBlockForm.get('customerId')!.value).toEqual('12345678901');
    });
  });

  describe('submitbtn', () => {
    it('should submit form and handle success response', () => {
      const response = { statusCode: 200, statusMessage: 'Success' };
      accountService.unblockAccount.mockReturnValue(of(response));
      const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();
      component.OpenBlockForm.patchValue({ reason: 'Test reason' });
      component.submitbtn(new Event('submit'));
      expect(accountService.unblockAccount).toHaveBeenCalledWith('123456789012', '', 'Test reason');
      expect(swalFireSpy).toHaveBeenCalledWith('Success', 'Success', 'success');
      expect(component.OpenBlockForm.value).toEqual({ accountNumber: '123456789012', blockType: '', reason: '' });
      expect(component.OpenBlockForm.disabled).toBe(true);
    });

    it('should handle warning response', () => {
      const response = { statusCode: 302, statusMessage: 'Warning' };
      accountService.unblockAccount.mockReturnValue(of(response));
      const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();
      component.submitbtn(new Event('submit'));
      expect(swalFireSpy).toHaveBeenCalledWith('Warning', 'Warning', 'warning');
    });

    it('should handle error response', () => {
      const response = { statusCode: 400, statusMessage: 'Error' };
      accountService.unblockAccount.mockReturnValue(of(response));
      const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();
      component.submitbtn(new Event('submit'));
      expect(swalFireSpy).toHaveBeenCalledWith('error', 'Error', 'error');
    });

    it('should handle other errors', () => {
      const error = new Error('Test error');
      accountService.unblockAccount.mockReturnValue(throwError(error));
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
      const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();
      component.submitbtn(new Event('submit'));
      expect(consoleErrorSpy).toHaveBeenCalledWith('HTTP Error:', error);
      expect(swalFireSpy).toHaveBeenCalledWith('Error', 'Something went wrong. Please try again later.', 'error');
    });

    it('should handle invalid form', () => {
      const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();
      component.submitbtn(new Event('submit'));
      expect(swalFireSpy).toHaveBeenCalledWith('Validation Error', 'Please fill in all required fields.', 'error');
    });
  });
});
