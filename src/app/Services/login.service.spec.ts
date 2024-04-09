import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LoginService } from './login.service';

describe('LoginService', () => {
  let service: LoginService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LoginService]
    });
    service = TestBed.inject(LoginService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should validate user ID', () => {
    const loginRequest = { /* Mock login request data */ };
    const mockResponse = { /* Mock response */ };

    service.validateUserId(loginRequest).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('http://localhost:1010/api/retailbanking/login/userId');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should login user', () => {
    const loginRequest = { /* Mock login request data */ };
    const mockResponse = { /* Mock response */ };

    service.login(loginRequest).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('http://localhost:1010/api/retailbanking/login/loginUser');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should delete customer', () => {
    const customerId = 123;
    const mockResponse = { /* Mock response */ };

    service.deleteCustomer(customerId).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`http://localhost:1010/api/retailbanking/login/customer/delete-customer/${customerId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should change password', () => {
    const customerId = 123;
    const oldPassword = 'oldPassword';
    const newPassword = 'newPassword';
    const mockResponse = { /* Mock response */ };

    service.changePassword(customerId, oldPassword, newPassword).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`http://localhost:1010/api/retailbanking/login/change-password/${customerId}?oldPassword=${oldPassword}&newPassword=${newPassword}`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});
