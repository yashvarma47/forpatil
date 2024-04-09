import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminHomeComponent } from './admin-home.component';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CustomerService } from 'src/app/Services/customer.service';
import { AccountService } from 'src/app/Services/account.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('AdminHomeComponent', () => {
  let component: AdminHomeComponent;
  let fixture: ComponentFixture<AdminHomeComponent>;
  let customerService: CustomerService;
  let accountService: AccountService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminHomeComponent],
      imports: [RouterTestingModule, FormsModule, HttpClientModule],
      providers: [
        CustomerService,
        AccountService,
        { provide: Router, useClass: class { navigateByUrl = jest.fn(); } }
      ]
    }).compileComponents();

    customerService = TestBed.inject(CustomerService);
    accountService = TestBed.inject(AccountService);
    router = TestBed.inject(Router);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load customer data on customer button click', () => {
    const mockCustomerData: any[] = []; // Mock customer data here
    jest.spyOn(customerService, 'getAllCustomer').mockReturnValue(of(mockCustomerData));
    component.onCustomerBtnClick();
    expect(component.showCustomerDetails).toBeTruthy();
    expect(component.showAccountDetails).toBeFalsy();
    expect(customerService.getAllCustomer).toHaveBeenCalled();
  });

  it('should load account data on account button click', () => {
    const mockAccountData: any[] = []; // Mock account data here
    jest.spyOn(accountService, 'getAllAccounts').mockReturnValue(of(mockAccountData));
    component.onAccountBtnClick();
    expect(component.showCustomerDetails).toBeFalsy();
    expect(component.showAccountDetails).toBeTruthy();
    expect(accountService.getAllAccounts).toHaveBeenCalled();
  });

  it('should navigate to create customer page on create customer button click', () => {
    const navigateByUrlMock = jest.spyOn(router, 'navigateByUrl');
    component.onCreateCustomerClick();
    expect(navigateByUrlMock).toHaveBeenCalledWith('/create-customer');
  });

  it('should navigate to open account page on create account button click', () => {
    const navigateByUrlMock = jest.spyOn(router, 'navigateByUrl');
    component.onCreateAccountClick();
    expect(navigateByUrlMock).toHaveBeenCalledWith('/open-account');
  });

  // Add more test cases as needed to cover other functionalities
});
