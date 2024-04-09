import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AccountService } from './account.service';

describe('AccountService', () => {
  let service: AccountService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AccountService]
    });
    service = TestBed.inject(AccountService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call getCustomerDetails API', () => {
    const userId = 123;
    service.getCustomerDetails(userId).subscribe();
    const req = httpMock.expectOne(`http://localhost:1012/api/retailbanking/accounts/details/${userId}`);
    expect(req.request.method).toBe('GET');
  });

  it('should call createAccount API', () => {
    const userId = 456;
    const accountType = 'savings';
    service.createAccount(userId, accountType).subscribe();
    const req = httpMock.expectOne(`http://localhost:1012/api/retailbanking/accounts/create/${userId}?accountType=${accountType}`);
    expect(req.request.method).toBe('POST');
  });

  it('should call blockAccount API', () => {
    const accountNo = 789;
    const status = 'blocked';
    const reason = 'fraud';
    service.blockAccount(accountNo, status, reason).subscribe();
    const req = httpMock.expectOne(`http://localhost:1012/api/retailbanking/accounts/updateAccountStatus/${accountNo}/${status}/${reason}`);
    expect(req.request.method).toBe('GET');
  });

  it('should call getAllAccounts API', () => {
    service.getAllAccounts().subscribe();
    const req = httpMock.expectOne(`http://localhost:1012/api/retailbanking/accounts/allAccounts`);
    expect(req.request.method).toBe('GET');
  });

  it('should call getAccountDetails API', () => {
    const accountNumber = 111;
    service.getAccountDetails(accountNumber).subscribe();
    const req = httpMock.expectOne(`http://localhost:1012/api/retailbanking/accounts/fetchAccountDetails/${accountNumber}`);
    expect(req.request.method).toBe('GET');
  });

  it('should call createTicket API', () => {
    const ticketDTO = { /* Some test data */ };
    service.createTicket(ticketDTO).subscribe();
    const req = httpMock.expectOne(`http://localhost:1012/api/retailbanking/accounts/ticket/create`);
    expect(req.request.method).toBe('POST');
  });

  it('should call fetchTicketIds API', () => {
    service.fetchTicketIds().subscribe();
    const req = httpMock.expectOne(`http://localhost:1012/api/retailbanking/accounts/tickets`);
    expect(req.request.method).toBe('GET');
  });

  it('should call updateTicket API', () => {
    const ticketDTO = { /* Some test data */ };
    const ticketId = 222;
    service.updateTicket(ticketDTO, ticketId).subscribe();
    const req = httpMock.expectOne(`http://localhost:1012/api/retailbanking/accounts/ticket/update/${ticketId}`);
    expect(req.request.method).toBe('PUT');
  });

  it('should call getTicketDetails API', () => {
    const ticketId = 333;
    service.getTicketDetails(ticketId).subscribe();
    const req = httpMock.expectOne(`http://localhost:1012/api/retailbanking/accounts/getTicket/${ticketId}`);
    expect(req.request.method).toBe('GET');
  });

  it('should call closeAccount API', () => {
    const accountNumber = 444;
    service.closeAccount(accountNumber).subscribe();
    const req = httpMock.expectOne(`http://localhost:1012/api/retailbanking/accounts//close/${accountNumber}`);
    expect(req.request.method).toBe('GET');
  });
});
