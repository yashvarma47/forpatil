import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { RouterTestingModule } from '@angular/router/testing';
import { LoginService } from 'src/app/Services/login.service';
import { UserService } from 'src/app/Services/user.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let loginService: jest.Mocked<LoginService>;
  let userService: jest.Mocked<UserService>;
  let router: Router;

  beforeEach(async () => {
    const loginServiceMock = {
      validateUserId: jest.fn(),
      login: jest.fn()
    };

    const userServiceMock = {
      setUserId: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [RouterTestingModule],
      providers: [
        { provide: LoginService, useValue: loginServiceMock },
        { provide: UserService, useValue: userServiceMock }
      ]
    }).compileComponents();

    loginService = TestBed.inject(LoginService) as jest.Mocked<LoginService>;
    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
    router = TestBed.inject(Router);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('onUserIdInputChange', () => {
    it('should disable login if userId length is not 8', () => {
      component.userid = '123456';
      component.onUserIdInputChange();
      expect(component.isLoginDisabled).toBe(true);
    });

    it('should validate userId through login service', () => {
      const response = { status: 200 };
      loginService.validateUserId.mockReturnValue(of(response));
      component.userid = '12345678';
      component.onUserIdInputChange();
      expect(component.isUserIdValid).toBe(true);
      expect(component.isLoginDisabled).toBe(false);
    });

    it('should handle error when validating userId', () => {
      const error = new Error('Validation error');
      loginService.validateUserId.mockReturnValue(throwError(error));
      component.userid = '12345678';
      component.onUserIdInputChange();
      expect(component.isUserIdValid).toBe(false);
      expect(component.isLoginDisabled).toBe(true);
      expect(component.UserIDerrorMessage).toEqual('Error validating user ID. Please try again.');
    });
  });

  describe('login', () => {
    it('should disable login if userId length is not 8', () => {
      component.userid = '123456';
      component.login();
      expect(component.isLoginDisabled).toBe(true);
    });

    it('should perform login', () => {
      const response = { status: 200, data: { role: 'CUSTOMER' } };
      loginService.login.mockReturnValue(of(response));
      const navigateSpy = jest.spyOn(router, 'navigate');
      component.userid = '12345678';
      component.password = 'password';
      component.login();
      expect(navigateSpy).toHaveBeenCalledWith(['/user-dashboard']);
      expect(userService.setUserId).toHaveBeenCalledWith('12345678');
    });

    it('should handle login error', () => {
      const error = { status: 401, message: 'Unauthorized' };
      loginService.login.mockReturnValue(throwError(error));
      component.userid = '12345678';
      component.password = 'password';
      component.login();
      expect(component.loginerrorMessage).toEqual('Invalid Credentials!');
    });
  });
});
