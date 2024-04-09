import { ComponentFixture, TestBed, tick, fakeAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { ResetPasswordComponent } from './reset-password.component';
import { CustomerService } from 'src/app/Services/customer.service';
import { ReactiveFormsModule } from '@angular/forms';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;
  let customerService: CustomerService;
  let activatedRoute: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ResetPasswordComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: ActivatedRoute, useValue: { queryParams: of({ token: 'testToken' }) } },
        { provide: CustomerService, useValue: { validateToken: jest.fn(), resetPassword: jest.fn() } }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    customerService = TestBed.inject(CustomerService);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call validateToken and set isTokenValid to true if token is valid', fakeAsync(() => {
    const mockResponse = { status: 200 };
    customerService.validateToken = jest.fn().mockReturnValue(of(mockResponse));

    component.ngOnInit();
    tick();

    expect(customerService.validateToken).toHaveBeenCalledWith('testToken');
    expect(component.isTokenValid).toBe(true);
  }));

  it('should display error message and call Swal.fire if token is expired', fakeAsync(() => {
    const mockResponse = { status: 401 };
    customerService.validateToken = jest.fn().mockReturnValue(of(mockResponse));
    const swalFireSpy = jest.spyOn(Swal, 'fire');

    component.ngOnInit();
    tick();

    expect(swalFireSpy).toHaveBeenCalledWith({
      icon: 'error',
      text: 'Link expired',
      showConfirmButton: false,
      timer: 40000,
    });
  }));

  it('should display error message and call Swal.fire if token is not valid', fakeAsync(() => {
    const mockResponse = { status: 404 };
    customerService.validateToken = jest.fn().mockReturnValue(of(mockResponse));
    const swalFireSpy = jest.spyOn(Swal, 'fire');

    component.ngOnInit();
    tick();

    expect(swalFireSpy).toHaveBeenCalledWith({
      icon: 'error',
      text: 'Link is not valid',
      showConfirmButton: false,
      timer: 40000,
    });
  }));

  it('should call resetPassword and display success message if password is reset successfully', fakeAsync(() => {
    const mockResponse = { status: 200 };
    customerService.resetPassword = jest.fn().mockReturnValue(of(mockResponse));
    const swalFireSpy = jest.spyOn(Swal, 'fire');

    component.passwordForm = { value: { newPassword: 'newPass', confirmPassword: 'newPass' } };
    component.validatePassword();
    tick();

    expect(customerService.resetPassword).toHaveBeenCalledWith('testToken', 'newPass');
    expect(swalFireSpy).toHaveBeenCalledWith({
      icon: 'success',
      text: 'Password reset successfully',
      showConfirmButton: false,
      timer: 3000,
    });
  }));

  // Add more test cases to cover other scenarios as needed

});
