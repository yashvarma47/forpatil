import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ChangePasswordComponent } from './change-password.component';
import { LoginService } from 'src/app/Services/login.service';
import { UserService } from 'src/app/Services/user.service';
import { of } from 'rxjs';
import Swal from 'sweetalert2';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // Import FormsModule


jest.mock('sweetalert2', () => ({
  fire: jest.fn()
}));

describe('ChangePasswordComponent', () => {
  let component: ChangePasswordComponent;
  let fixture: ComponentFixture<ChangePasswordComponent>;
  let loginService: LoginService;
  let userService: UserService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChangePasswordComponent],
      providers: [LoginService, UserService],
      imports: [HttpClientModule, FormsModule, ReactiveFormsModule] // Add FormsModule to imports
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangePasswordComponent);
    component = fixture.componentInstance;
    loginService = TestBed.inject(LoginService);
    userService = TestBed.inject(UserService);
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update password successfully', () => {
    jest.spyOn(loginService, 'changePassword').mockReturnValue(of({ status: 200 }));
    jest.spyOn(component as any, 'resetForm').mockImplementation(() => {});
    component.currentPassword = 'oldPassword';
    component.newPassword = 'newPassword';
    component.confirmPassword = 'newPassword';
    component.userId = 1;

    component.updatePassword();

    expect(loginService.changePassword).toHaveBeenCalledWith(1, 'oldPassword', 'newPassword');
    expect((component as any).resetForm).toHaveBeenCalled();
    expect(Swal.fire).toHaveBeenCalledWith({
      icon: 'success',
      text: 'Password changed successfully',
      showConfirmButton: false,
      timer: 3000,
    });
  });

  it('should handle password mismatch', () => {
    component.newPassword = 'newPassword';
    component.confirmPassword = 'differentPassword';

    component.updatePassword();

    expect(Swal.fire).toHaveBeenCalledWith({
      icon: 'error',
      text: 'Passwords do not match',
      showConfirmButton: false,
      timer: 3000,
    });
  });

  it('should handle invalid user ID', () => {
    component.currentPassword = 'oldPassword';
    component.newPassword = 'newPassword';
    component.confirmPassword = 'newPassword';
    component.userId = null;

    component.updatePassword();

    expect(Swal.fire).toHaveBeenCalledWith({
      icon: 'error',
      text: 'User ID not available.',
      showConfirmButton: false,
      timer: 3000,
    });
  });

  it('should handle incorrect old password', () => {
    jest.spyOn(loginService, 'changePassword').mockReturnValue(of({ status: 400 }));
    jest.spyOn(component as any, 'resetForm').mockImplementation(() => {});
    component.currentPassword = 'oldPassword';
    component.newPassword = 'newPassword';
    component.confirmPassword = 'newPassword';
    component.userId = 1;

    component.updatePassword();

    expect((component as any).resetForm).toHaveBeenCalled();
    expect(Swal.fire).toHaveBeenCalledWith({
      icon: 'error',
      text: 'Old password does not match',
      showConfirmButton: false,
      timer: 3000,
    });
  });

  it('should handle API error', () => {
    jest.spyOn(loginService, 'changePassword').mockReturnValue(of({ status: 500 }));
    component.currentPassword = 'oldPassword';
    component.newPassword = 'newPassword';
    component.confirmPassword = 'newPassword';
    component.userId = 1;

    component.updatePassword();

    expect(Swal.fire).toHaveBeenCalledWith({
      icon: 'error',
      text: 'Something went wrong',
      showConfirmButton: false,
      timer: 3000,
    });
  });

  it('should cancel update', () => {
    jest.spyOn(component as any, 'resetForm').mockImplementation(() => {});
    

    component.cancelUpdate();

    expect((component as any).resetForm).toHaveBeenCalled();
    expect(
  });

  it('should check if passwords match', () => {
    component.newPassword = 'newPassword';
    component.confirmPassword = 'newPassword';

    expect(component.passwordsDoNotMatch()).toBeFalsy();

    component.confirmPassword = 'differentPassword';

    expect(component.passwordsDoNotMatch()).toBeTruthy();
  });


  it('should check if control is touched', () => {
    const control = { touched: true } as any; // Corrected line
    const nullControl = null;

    expect(component.isTouched(control)).toBeTruthy();
    expect(component.isTouched(nullControl)).toBeFalsy();
  });
});
