import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ViewAccountBalanceComponent } from './view-account-balance.component';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import { BehaviorSubject, of } from 'rxjs';

describe('ViewAccountBalanceComponent', () => {
  let component: ViewAccountBalanceComponent;
  let fixture: ComponentFixture<ViewAccountBalanceComponent>;
  let mockAccountService: AccountService;
  let mockUserService: UserService;

  beforeEach(async () => {
    mockAccountService = {
      getCustomerDetails: jest.fn(() => of({ statusCode: 200, data: {}, accounts: [] }))
    } as unknown as AccountService;

    mockUserService = {
      userId$: new BehaviorSubject('123')
    } as unknown as UserService;

    await TestBed.configureTestingModule({
      declarations: [ViewAccountBalanceComponent],
      providers: [
        { provide: AccountService, useValue: mockAccountService },
        { provide: UserService, useValue: mockUserService }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewAccountBalanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getCustomerDetails on ngOnInit', () => {
    expect(mockAccountService.getCustomerDetails).toHaveBeenCalled();
  });

  it('should set showBalance to true on onViewClick', () => {
    component.onViewClick();
    expect(component.showBalance).toBe(true);
  });

  // Add more test cases as needed for other methods in the component
});
