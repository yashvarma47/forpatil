import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SetupProfileComponent } from './setup-profile.component';
import { CustomerService } from 'src/app/Services/customer.service';
import { UserService } from 'src/app/Services/user.service';
import { of, throwError, BehaviorSubject } from 'rxjs';
import Swal from 'sweetalert2';

describe('SetupProfileComponent', () => {
  let component: SetupProfileComponent;
  let fixture: ComponentFixture<SetupProfileComponent>;
  let customerService: jest.Mocked<CustomerService>;
  let userService: jest.Mocked<UserService>;

  beforeEach(async () => {
    const customerServiceMock = {
      getCustomerProfileUpdate: jest.fn(),
      updateCustomerDetails: jest.fn()
    };

    const userServiceMock = {
      userId$: new BehaviorSubject<string | null>('123') // assuming initial user ID is '123'
    };

    await TestBed.configureTestingModule({
      declarations: [SetupProfileComponent],
      providers: [
        { provide: CustomerService, useValue: customerServiceMock },
        { provide: UserService, useValue: userServiceMock }
      ]
    }).compileComponents();

    customerService = TestBed.inject(CustomerService) as jest.Mocked<CustomerService>;
    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SetupProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize component', () => {
      expect(component.userId).toEqual(123); // Assuming user ID is set correctly
      expect(customerService.getCustomerProfileUpdate).toHaveBeenCalledWith(123);
    });
  });

  describe('onEditClick', () => {
    it('should set isEditMode to true', () => {
      component.onEditClick();
      expect(component.isEditMode).toBe(true);
    });
  });

  describe('onCancelClick', () => {
    it('should set isEditMode to false', () => {
      component.onCancelClick();
      expect(component.isEditMode).toBe(false);
    });
  });

  describe('loadCustomerDetails', () => {
    it('should load customer details', () => {
      const mockResponse = { data: { /* your mock data */ } };
      customerService.getCustomerProfileUpdate.mockReturnValue(of(mockResponse));
      component.loadCustomerDetails();
      expect(component.customerData).toEqual(mockResponse.data);
    });

    it('should handle error when loading customer details', () => {
      const error = new Error('Test error');
      customerService.getCustomerProfileUpdate.mockReturnValue(throwError(error));
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
      component.loadCustomerDetails();
      expect(consoleErrorSpy).toHaveBeenCalledWith('Error fetching customer details:', error);
    });
  });

  describe('onUpdateClick', () => {
    it('should update customer details', () => {
      const mockResponse = { status: 200 };
      customerService.updateCustomerDetails.mockReturnValue(of(mockResponse));
      const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();
      component.onUpdateClick();
      expect(swalFireSpy).toHaveBeenCalledWith({
        icon: 'success',
        text: 'Update successful!',
        showConfirmButton: false,
        timer: 3000,
      });
      expect(component.isEditMode).toBe(false);
    });

    it('should handle error when updating customer details', () => {
      const error = new Error('Test error');
      customerService.updateCustomerDetails.mockReturnValue(throwError(error));
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
      component.onUpdateClick();
      expect(consoleErrorSpy).toHaveBeenCalledWith('Update failed:', error);
    });
  });

  describe('convertFieldsToJson', () => {
    it('should convert fields to JSON', () => {
      // Mocking customerData
      component.customerData = {
        firstName: 'John',
        lastName: 'Doe',
        address: {
          street: '123 Main St',
          city: 'City',
          state: 'State',
          zipcode: '12345'
        },
        dateOfBirth: '01/01/2000',
        gender: 'Male',
        email: 'john@example.com',
        phoneNumber: '123-456-7890',
        panNumber: 'ABCDE1234F',
        aadharNumber: '1234 5678 9012'
      };
      const result = component.convertFieldsToJson();
      expect(result).toEqual({
        firstName: 'John',
        lastName: 'Doe',
        addressDTO: {
          street: '123 Main St',
          city: 'City',
          state: 'State',
          zipcode: '12345'
        },
        dateOfBirth: '01/01/2000',
        gender: 'Male',
        email: 'john@example.com',
        phoneNumber: '123-456-7890',
        panNumber: 'ABCDE1234F',
        aadharNumber: '1234 5678 9012'
      });
    });
  });
});
