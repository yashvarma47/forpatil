import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TransactionService } from './transaction.service';

describe('TransactionService', () => {
  let service: TransactionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TransactionService]
    });
    service = TestBed.inject(TransactionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a transaction', () => {
    const mockTransaction = { /* mock transaction data */ };
    service.createTransaction(mockTransaction).subscribe(response => {
      expect(response).toBeTruthy();
      // Add additional expectations as needed
    });

    const req = httpMock.expectOne('http://localhost:1013/api/retailbanking/transactions/createTransaction');
    expect(req.request.method).toBe('POST');
    req.flush({ /* mock response data */ });
  });

  it('should transfer within self accounts', () => {
    const mockTransaction = { /* mock transaction data */ };
    service.withinSelfAccount(mockTransaction).subscribe(response => {
      expect(response).toBeTruthy();
      // Add additional expectations as needed
    });
  
    const req = httpMock.expectOne('http://localhost:1013/api/retailbanking/transactions/transferWithinSelfAccounts');
    expect(req.request.method).toBe('POST');
    req.flush({ /* mock response data */ });
  });
  
  it('should transfer within my bank accounts', () => {
    const mockTransaction = { /* mock transaction data */ };
    service.withinMyBank(mockTransaction).subscribe(response => {
      expect(response).toBeTruthy();
      // Add additional expectations as needed
    });
  
    const req = httpMock.expectOne('http://localhost:1013/api/retailbanking/transactions/transferWithinAccounts');
    expect(req.request.method).toBe('POST');
    req.flush({ /* mock response data */ });
  });
  
  it('should get beneficiary by user ID', () => {
    const userId = 123;
    service.getBeneficiary(userId).subscribe(response => {
      expect(response).toBeTruthy();
      // Add additional expectations as needed
    });
  
    const req = httpMock.expectOne(`http://localhost:1013/api/retailbanking/transactions/benificiary/${userId}`);
    expect(req.request.method).toBe('GET');
    req.flush({ /* mock response data */ });
  });
  
  it('should add beneficiary', () => {
    const mockBenResponse = { /* mock beneficiary response data */ };
    const userId = 123;
    service.addBeneficiary(mockBenResponse, userId).subscribe(response => {
      expect(response).toBeTruthy();
      // Add additional expectations as needed
    });
  
    const req = httpMock.expectOne(`http://localhost:1013/api/retailbanking/transactions/benificiary/add/${userId}`);
    expect(req.request.method).toBe('POST');
    req.flush({ /* mock response data */ });
  });


  it('should retrieve yearly expenses', () => {
    const accountNumber = 123;
    const accountType = 'savings';
    const year = 2023;
    const mockResponse = { /* Mock response */ };

    service.getYearlyExpenses(accountNumber, accountType, year).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const url = `http://localhost:1013/api/retailbanking/transactions/expense/${accountNumber}/${accountType}/yearly/${year}`;
    const req = httpMock.expectOne(url);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should retrieve monthly expenses', () => {
    const accountNumber = 456;
    const accountType = 'checking';
    const month = 5;
    const mockResponse = { /* Mock response */ };

    service.getMonthlyExpenses(accountNumber, accountType, month).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const url = `http://localhost:1013/api/retailbanking/transactions/expense/${accountNumber}/${accountType}/monthly/${month}`;
    const req = httpMock.expectOne(url);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  
  // Add more test cases for other methods in a similar fashion
  
  
  
  

});

