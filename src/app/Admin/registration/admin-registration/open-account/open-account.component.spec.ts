import { ComponentFixture, TestBed, tick, fakeAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { OpenAccountComponent } from './open-account.component';
import { AccountService } from 'src/app/Services/account.service';
import { LoginService } from 'src/app/Services/login.service';
import { UserService } from 'src/app/Services/user.service';
import { Router } from '@angular/router';
import { of, throwError, BehaviorSubject } from 'rxjs';
import Swal from 'sweetalert2';

describe('OpenAccountComponent', () => {
  let component: OpenAccountComponent;
  let fixture: ComponentFixture<OpenAccountComponent>;
  let mockAccountService: Partial<AccountService>;
  let mockLoginService: Partial<LoginService>;
  let mockRouter: Partial<Router>;
  let mockSwalFire: jest.Mock;

  beforeEach(async () => {
    mockAccountService = {
      createAccount: jest.fn(() => of({})),
    };

    mockLoginService = {
      validateUserId: jest.fn(() => of({ status: 200 })),
    };

    mockRouter = {
      navigate: jest.fn(),
    };

    mockSwalFire = jest.fn();

    jest.mock('sweetalert2', () => ({
      __esModule: true,
      default: {
        fire: mockSwalFire,
      },
    }));

    await TestBed.configureTestingModule({
      declarations: [OpenAccountComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: AccountService, useValue: mockAccountService },
        { provide: LoginService, useValue: mockLoginService },
        { provide: Router, useValue: mockRouter },
        UserService,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OpenAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to create-customer on navigateTo', () => {
    component.navigateTo();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['create-customer']);
  });

  it('should disable login on disableLogin', () => {
    component.isUserIdValid = true;

        // Call the private method
        (component as any).disableLogin();

    // After calling disableLogin, isUserIdValid should be false
    expect(component.isUserIdValid).toBeFalsy();
  });

  it('should clear UserIDerrorMessage when userid length is not 8', () => {
    // Set up initial conditions
    component.UserIDerrorMessage = 'Previous error message';
    component.userid = '123456'; // Set userid with length not equal to 8

    // Call the method under test
    component.onUserIdInputChange();

    // Assertion
    expect(component.UserIDerrorMessage).toBe(''); // UserIDerrorMessage should be cleared
  });

  it('should validate user ID input and call login service on onUserIdInputChange', fakeAsync(() => {
    component.userid = '12345678';
    const mockResponse = { status: 200 };
    mockLoginService.validateUserId = jest.fn(() => of(mockResponse));

    component.onUserIdInputChange();

    tick(); // Simulate passage of time for observable to complete

    expect(component.isUserIdValid).toBeTruthy();
    expect(component.UserIDerrorMessage).toEqual('');
    expect(mockLoginService.validateUserId).toHaveBeenCalledWith({ userId: '12345678' });
  }));

  it('should handle error while validating user ID on onUserIdInputChange', fakeAsync(() => {
    component.userid = '12345678';
    const mockError = new Error('Validation Error');
    mockLoginService.validateUserId = jest.fn(() => throwError(mockError));

    component.onUserIdInputChange();

    tick(); // Simulate passage of time for observable to complete

    expect(component.isUserIdValid).toBeFalsy();
    expect(component.UserIDerrorMessage).toEqual('Error validating user ID. Please try again.');
    expect(console.error).toHaveBeenCalledWith('Error validating user ID:', mockError);
  }));

  it('should limit input to 8 digits on limitTo8Digits', () => {
    const event = { target: { value: '1234567890' }};
    component.limitTo8Digits(event as any);
    expect(event.target.value).toEqual('12345678');
  });

  it('should not limit input if it has 8 digits or less on limitTo8Digits', () => {
    const event = { target: { value: '12345678' }};
    component.limitTo8Digits(event as any);
    expect(event.target.value).toEqual('12345678');
  });

  it('should set customerIdControl correctly', () => {
    const control = component.customerIdControl;
    expect(control?.value).toEqual('');
    component.OpenAccountForm.get('customerId')?.setValue('12345678');
    expect(control?.value).toEqual('12345678');
  });

  it('should call createAccount on submitbtn when userId is valid', () => {
    // Set up initial conditions
    component.isUserIdValid = true;
    component.userid = '12345678';
    component.ChooseAcc = 'SAVINGS';
  
    // Create a new event
    const event = new Event('submit');
  
    // Call the method under test
    component.submitbtn(event);
  
    // Assertion
    expect(mockAccountService.createAccount).toHaveBeenCalledWith(12345678, 'SAVINGS');
  });
  
  it('should not call createAccount on submitbtn when userId is invalid', () => {
    // Set up initial conditions
    component.isUserIdValid = false;
    component.userid = '12345678';
    component.ChooseAcc = 'SAVINGS';
  
    // Create a new event
    const event = new Event('submit');
  
    // Call the method under test
    component.submitbtn(event);
  
    // Assertion
    expect(mockAccountService.createAccount).not.toHaveBeenCalled();
  });
  
  it('should display success message when account creation is successful', () => {
    // Set up initial conditions
    component.isUserIdValid = true;
    component.userid = '12345678';
    component.ChooseAcc = 'SAVINGS';
  
    // Mock createAccount to return an observable with a success response
    const createAccountSpy = jest.spyOn(mockAccountService, 'createAccount');
    createAccountSpy.mockReturnValueOnce(of({}));
  
    // Create a new event
    const event = new Event('submit');
  
    // Call the method under test
    component.submitbtn(event);
  
    // Assertion
    expect(mockSwalFire).toHaveBeenCalledWith({
      icon: 'success',
      title: 'Account Created Successfully!',
      showConfirmButton: false,
      timer: 1500,
    });
  });
  
  it('should display error message when account creation fails', () => {
    // Set up initial conditions
    component.isUserIdValid = true;
    component.userid = '12345678';
    component.ChooseAcc = 'SAVINGS';
  
    // Mock createAccount to return an observable that throws an error
    const createAccountSpy = jest.spyOn(mockAccountService, 'createAccount');
    createAccountSpy.mockReturnValueOnce(throwError('error'));
  
    // Create a new event
    const event = new Event('submit');
  
    // Call the method under test
    component.submitbtn(event);
  
    // Assertion
    expect(mockSwalFire).toHaveBeenCalledWith({
      icon: 'error',
      title: 'Account Created Failed!',
      showConfirmButton: false,
      timer: 1500,
    });
  });
});
