import { TestBed, ComponentFixture, tick, fakeAsync } from '@angular/core/testing';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CustomerService } from 'src/app/Services/customer.service';
import Swal from 'sweetalert2';
import { of, throwError } from 'rxjs';
import { HttpClientModule } from '@angular/common/http';

import { ForgotUseridComponent, ForgotUserIdDTO } from './forgot-userid.component'; // Import ForgotUserIdComponent and ForgotUserIdDTO

describe('ForgotUseridComponent', () => {
  let component: ForgotUseridComponent;
  let fixture: ComponentFixture<ForgotUseridComponent>;
  let customerService: CustomerService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ForgotUseridComponent],
      imports: [ReactiveFormsModule, HttpClientModule], // Add HttpClientModule to the imports array
      providers: [FormBuilder, CustomerService, { provide: Router, useValue: { navigate: jest.fn() } }]
    }).compileComponents();
  
    fixture = TestBed.createComponent(ForgotUseridComponent);
    component = fixture.componentInstance;
    customerService = TestBed.inject(CustomerService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call customerService.forgotUserId and show success message on successful submission', fakeAsync(() => {
    const dto: ForgotUserIdDTO = {
      dateOfBirth: new Date(),
      pan: 'ABCDE1234F',
      aadhar: '123456789012',
      phoneNumber: '1234567890'
    };
    const mockResponse = { status: 200 };
    customerService.forgotUserId = jest.fn().mockReturnValue(of(mockResponse));
    const swalFireSpy = jest.spyOn(Swal, 'fire');

    component.forgotUserIdForm.setValue(dto);
    component.onSubmit();
    tick();

    expect(customerService.forgotUserId).toHaveBeenCalledWith(dto);
    expect(swalFireSpy).toHaveBeenCalledWith({
      icon: 'success',
      text: 'UserId is sent to your registered email',
      showConfirmButton: false,
      timer: 3000
    });
    expect(component.forgotUserIdForm.reset).toHaveBeenCalled();
    expect(component.resetLinkSent).toBe(true);
  }));

  it('should handle error response from customerService.forgotUserId', fakeAsync(() => {
    const error = new Error('Failed to send reset link');
    customerService.forgotUserId = jest.fn().mockReturnValue(throwError(error));
    const consoleErrorSpy = jest.spyOn(console, 'error');

    component.onSubmit();
    tick();

    expect(customerService.forgotUserId).toHaveBeenCalled();
    expect(consoleErrorSpy).toHaveBeenCalledWith('Error submitting forgot user ID:', error);
  }));
});
