import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AddBeneficiaryComponent } from './add-beneficiary.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TransactionService } from 'src/app/Services/transaction.service';
import { UserService } from 'src/app/Services/user.service';
import { of, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { FormsModule } from '@angular/forms'; // Import FormsModule

describe('AddBeneficiaryComponent', () => {
  let component: AddBeneficiaryComponent;
  let fixture: ComponentFixture<AddBeneficiaryComponent>;
  let formBuilder: FormBuilder;
  let transactionService: jest.Mocked<TransactionService>;
  let userService: jest.Mocked<UserService>;

  beforeEach(async () => {
    formBuilder = new FormBuilder();

    const transactionServiceMock = {
      addBeneficiary: jest.fn()
    };

    const userServiceMock = {
      userId$: of('123') // mock user id
    };

    await TestBed.configureTestingModule({
      declarations: [AddBeneficiaryComponent],
      imports: [ReactiveFormsModule, FormsModule], // Add FormsModule here
      providers: [
        { provide: FormBuilder, useValue: formBuilder },
        { provide: TransactionService, useValue: transactionServiceMock },
        { provide: UserService, useValue: userServiceMock }
      ]
    }).compileComponents();

    transactionService = TestBed.inject(TransactionService) as jest.Mocked<TransactionService>;
    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddBeneficiaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize form and subscribe to userId$', () => {
      expect(component.beneficiaryForm).toBeTruthy();
      expect(component.id).toEqual(123);
    });
  });

  describe('onSubmit', () => {
    it('should submit form and handle success response', () => {
        const formValue = { reenteredAccountNumber: 12345678901, name: 'Test Name', email: 'test@test.com' };
        component.beneficiaryAccountNumber = 12345678901;
        component.reenteredAccountNumber = 12345678901;
        component.name = 'Test Name';
        component.email = 'test@test.com';
      
        const response = { status: 200 };
        transactionService.addBeneficiary.mockReturnValue(of(response));
        const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();
      
        component.onSubmit(component.beneficiaryForm);
      
        expect(transactionService.addBeneficiary).toHaveBeenCalledWith(formValue, 123); // Check arguments here
        expect(swalFireSpy).toHaveBeenCalledWith('success', 'Beneficiary added successfully', 'success');
        expect(component['clearInputs']).toHaveBeenCalled();
      });
      
  
      it('should handle error response', () => {
        const formValue = { reenteredAccountNumber: 12345678901, name: 'Test Name', email: 'test@test.com' };
        component.beneficiaryAccountNumber = 12345678901;
        component.reenteredAccountNumber = 12345678901;
        component.name = 'Test Name';
        component.email = 'test@test.com';
      
        const error = { error: { status: 400 } };
        transactionService.addBeneficiary.mockReturnValue(throwError(error));
        const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();
      
        component.onSubmit(component.beneficiaryForm);
      
        expect(swalFireSpy).toHaveBeenCalledWith('error', 'Beneficiary already exist', 'error'); // Check arguments here
        expect(component['clearInputs']).not.toHaveBeenCalled();
      });
      
  
      it('should handle invalid form', () => {
        const swalFireSpy = jest.spyOn(Swal, 'fire').mockImplementation();
      
        component.onSubmit({ valid: false });
      
        expect(swalFireSpy).toHaveBeenCalledWith('error', 'Form is invalid.', 'error'); // Check arguments here
        expect(transactionService.addBeneficiary).not.toHaveBeenCalled();
      });
      
  });
  

  describe('onCancel', () => {
    it('should clear inputs', () => {
      // Set initial values for inputs
      component.beneficiaryAccountNumber = 12345678901;
      component.reenteredAccountNumber = 12345678901;
      component.name = 'Test Name';
      component.email = 'test@test.com';
      
  
      // Call onCancel method
      component.onCancel();
  
      // Assert that inputs are cleared
      expect(component.beneficiaryAccountNumber).toBeUndefined();
      expect(component.reenteredAccountNumber).toBeUndefined();
      expect(component.name).toEqual('');
      expect(component.email).toEqual('');
    });
  });
  
  

  describe('limitTo11Digits', () => {
    it('should limit input to 11 digits', () => {
      const event = { target: { value: '123456789012345' } };
      component.limitTo11Digits(event);
      expect(event.target.value).toEqual('12345678901');
    });
  });
});
