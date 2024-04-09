import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CustomerService } from 'src/app/Services/customer.service';
import { CreateCustomerComponent } from './create-customer.component';
import { HttpClientModule } from '@angular/common/http'; // Import HttpClientModule

describe('CreateCustomerComponent', () => {
  let component: CreateCustomerComponent;
  let fixture: ComponentFixture<CreateCustomerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateCustomerComponent],
      imports: [ReactiveFormsModule, HttpClientModule], // Include HttpClientModule
      providers: [
        FormBuilder,
        MatSnackBar,
        CustomerService
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateCustomerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with required controls', () => {
    expect(component.newCustomerForm).toBeDefined();
    expect(component.newCustomerForm.get('firstName')).toBeDefined();
    expect(component.newCustomerForm.get('lastName')).toBeDefined();
    expect(component.newCustomerForm.get('street')).toBeDefined();
    expect(component.newCustomerForm.get('city')).toBeDefined();
    expect(component.newCustomerForm.get('state')).toBeDefined();
    expect(component.newCustomerForm.get('zipcode')).toBeDefined();
    expect(component.newCustomerForm.get('dateOfBirth')).toBeDefined();
    expect(component.newCustomerForm.get('gender')).toBeDefined();
    expect(component.newCustomerForm.get('email')).toBeDefined();
    expect(component.newCustomerForm.get('phoneNumber')).toBeDefined();
    expect(component.newCustomerForm.get('panNumber')).toBeDefined();
    expect(component.newCustomerForm.get('aadharNumber')).toBeDefined();
  });

  it('should limit input to 10 digits for phone number', () => {
    const event = { target: { value: '123456789012345' } };
    component.limitTo10Digits(event, 'phoneNumber');
    expect(component.newCustomerForm.get('phoneNumber')!.value).toBe('1234567890');
  });

  it('should limit input to 6 digits for zipcode', () => {
    const event = { target: { value: '1234567' } };
    component.limitTo6Digits(event, 'zipcode');
    expect(component.newCustomerForm.get('zipcode')!.value).toBe('123456');
  });

  // Add more test cases as needed
});
