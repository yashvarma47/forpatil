import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AccountClosureComponent } from './account-closure.component';
import { AccountService } from 'src/app/Services/account.service';
import { UserService } from 'src/app/Services/user.service';
import { of } from 'rxjs';
import Swal from 'sweetalert2';

describe('AccountClosureComponent', () => {
  let component: AccountClosureComponent;
  let fixture: ComponentFixture<AccountClosureComponent>;
  let accountServiceMock: jest.Mocked<AccountService>;
  let userServiceMock: jest.Mocked<UserService>;

  beforeEach(async () => {
    const accountServiceSpy = {
      getCustomerDetails: jest.fn(),
      createTicket: jest.fn()
    };
    const userServiceSpy = {
      userId$: of('12345')
    };

    await TestBed.configureTestingModule({
      declarations: [ AccountClosureComponent ],
      providers: [
        { provide: AccountService, useValue: accountServiceSpy },
        { provide: UserService, useValue: userServiceSpy }
      ]
    })
    .compileComponents();

    accountServiceMock = TestBed.inject(AccountService) as jest.Mocked<AccountService>;
    userServiceMock = TestBed.inject(UserService) as jest.Mocked<UserService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountClosureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update UI on successful customer details retrieval', () => {
    const response = {
      statusCode: 200,
      data: { /* mocked customer data */ },
      accounts: [ /* mocked customer accounts */ ]
    };
    accountServiceMock.getCustomerDetails.mockReturnValue(of(response));

    component.ngOnInit();

    expect(component.customerDetails).toEqual(response.data);
    expect(component.customerAccounts).toEqual(response.accounts);
  });

  it('should handle form submission', () => {
    const mockFormEvent = new MouseEvent('submit');
    const mockTicketDTO = {
      ticketType: 'CLOSURE',
      customerId: '12345',
      accountNumber: 'mockAccountNumber',
      accountType: 'mockAccountType',
      recipientAccountNumber: 0,
      ticketStatus: 'OPEN',
      transferFund: 'Withdraw as Cash/Cheque',
      feedback: 'mockFeedback'
    };

    accountServiceMock.createTicket.mockReturnValue(of({ status: 201, data: { ticketId: 'mockTicketId' } }));
    jest.spyOn(Swal, 'fire').mockImplementation();

    component.chooseAccNo = 'mockAccountNumber';
    component.chooseAcc = 'mockAccountType';
    component.selectOptionFunds = 'Withdraw as Cash/Cheque';
    component.feedback = 'mockFeedback';

    component.submitbtn(mockFormEvent);

    expect(accountServiceMock.createTicket).toHaveBeenCalledWith(mockTicketDTO);
    expect(Swal.fire).toHaveBeenCalledWith({
      icon: 'success',
      title: 'Ticket with ticketId mockTicketId created successfully!',
      showConfirmButton: false,
      timer: 3000
    });
  });


});
