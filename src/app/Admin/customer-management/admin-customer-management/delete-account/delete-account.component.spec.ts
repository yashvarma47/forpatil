import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { DeleteAccountComponent } from './delete-account.component';
import { LoginService } from 'src/app/Services/login.service';
import { of } from 'rxjs';

// Mocking Swal
jest.mock('sweetalert2', () => ({
  fire: jest.fn()
}));

describe('DeleteAccountComponent', () => {
  let component: DeleteAccountComponent;
  let fixture: ComponentFixture<DeleteAccountComponent>;
  let loginService: LoginService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeleteAccountComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        {
          provide: LoginService,
          useValue: {
            deleteCustomer: jest.fn()
          }
        }
      ]
    }).compileComponents();

    loginService = TestBed.inject(LoginService);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should limit input to 8 digits', () => {
    const event = {
      target: {
        value: '123456789'
      }
    } as any;

    component.limitTo8Digits(event);

    expect(component.deleteForm.get('customerId')?.value).toBe('12345678');
  });

  it('should delete customer', () => {
    component.deleteForm.patchValue({ customerId: '12345678', reason: 'test reason' });

    jest.spyOn(loginService, 'deleteCustomer').mockReturnValue(of({ status: 200, message: 'Customer deleted successfully' }));
    
    component.deleteCustomer();

    expect(loginService.deleteCustomer).toHaveBeenCalledWith('12345678');
    expect(require('sweetalert2').fire).toHaveBeenCalledWith('Success', 'Customer deleted successfully', 'success');
  });

  it('should handle error on delete customer', () => {
    component.deleteForm.patchValue({ customerId: '12345678', reason: 'test reason' });

    jest.spyOn(loginService, 'deleteCustomer').mockReturnValue(of({ status: 500 }));
    
    component.deleteCustomer();

    expect(component.errorMessage).toBe("The customer cannot be deleted as there are accounts associated with their profile. To proceed, please close the associated accounts first.");
  });
});
