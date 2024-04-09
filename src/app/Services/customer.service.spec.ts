import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CustomerService } from './customer.service';

describe('CustomerService', () => {
  let service: CustomerService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CustomerService]
    });
    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call registerCustomer API', () => {
    const customerRequest = { /* Some test data */ };
    service.registerCustomer(customerRequest).subscribe();
    const req = httpMock.expectOne('http://localhost:1011/api/retailbanking/customer/customer/register');
    expect(req.request.method).toBe('POST');
  });

  it('should call getCustomerDetails API', () => {
    const userId = 123;
    service.getCustomerDetails(userId).subscribe();
    const req = httpMock.expectOne(`http://localhost:1011/api/retailbanking/customer/customer/${userId}`);
    expect(req.request.method).toBe('GET');
  });

  it('should call getAllCustomer API', () => {
    service.getAllCustomer().subscribe();
    const req = httpMock.expectOne('http://localhost:1011/api/retailbanking/customer/allCustomer');
    expect(req.request.method).toBe('GET');
  });

  it('should call getCustomerProfileUpdate API', () => {
    const userId = 456;
    service.getCustomerProfileUpdate(userId).subscribe();
    const req = httpMock.expectOne(`http://localhost:1011/api/retailbanking/customer/customers/${userId}`);
    expect(req.request.method).toBe('GET');
  });

  it('should call updateCustomerDetails API', () => {
    const customerId = 789;
    const updatedData = { /* Some test data */ };
    service.updateCustomerDetails(customerId, updatedData).subscribe();
    const req = httpMock.expectOne(`http://localhost:1011/api/retailbanking/customer/customer/update/${customerId}`);
    expect(req.request.method).toBe('PUT');
  });

  it('should call forgotUserId API', () => {
    const forgotIdDTO = { /* Some test data */ };
    service.forgotUserId(forgotIdDTO).subscribe();
    const req = httpMock.expectOne('http://localhost:1011/api/retailbanking/customer/forgot-userid');
    expect(req.request.method).toBe('POST');
  });

  it('should call updateNotifications API', () => {
    const customerId = 123;
    const typeofnotification = 'Security_alerts';
    const isChecked = true;
    service.updateNotifications(customerId, typeofnotification, isChecked).subscribe();
    const req = httpMock.expectOne(`http://localhost:1011/api/retailbanking/customer/updateNotification/Email/${typeofnotification}/${customerId}`);
    expect(req.request.method).toBe('GET');
  });

  it('should call getNotifications API', () => {
    const customerId = 123;
    service.getNotifications(customerId).subscribe();
    const req = httpMock.expectOne(`http://localhost:1011/api/retailbanking/customer/getNotificationData/${customerId}/0`);
    expect(req.request.method).toBe('GET');
  });

  it('should send forgot password request', () => {
    const forgotPasswordDTO = { /* Mock forgot password DTO */ };
    const mockResponse = { /* Mock response */ };

    service.forgotPassword(forgotPasswordDTO).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('http://localhost:1011/api/retailbanking/customer/forgot-password');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(forgotPasswordDTO);
    req.flush(mockResponse);
  });

  it('should send reset password request', () => {
    const passwordToken = 'token123';
    const newPassword = 'newPassword123';
    const mockResponse = { /* Mock response */ };

    service.resetPassword(passwordToken, newPassword).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`http://localhost:1011/api/retailbanking/customer/reset-password/${passwordToken}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newPassword);
    req.flush(mockResponse);
  });

  it('should validate token', () => {
    const token = 'token123';
    const mockResponse = { /* Mock response */ };

    service.validateToken(token).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`http://localhost:1011/api/retailbanking/customer/validate/${token}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });
});
