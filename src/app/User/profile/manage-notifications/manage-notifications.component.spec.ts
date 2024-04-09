import { TestBed, ComponentFixture } from '@angular/core/testing';
import { ManageNotificationsComponent } from './manage-notifications.component';
import { CustomerService } from 'src/app/Services/customer.service';
import { UserService } from 'src/app/Services/user.service';
import { of } from 'rxjs';

describe('ManageNotificationsComponent', () => {
  let component: ManageNotificationsComponent;
  let fixture: ComponentFixture<ManageNotificationsComponent>;
  let mockCustomerService: jest.Mocked<CustomerService>;
  let mockUserService: jest.Mocked<UserService>;

  beforeEach(async () => {
    const customerServiceSpy = {
      getNotifications: jest.fn().mockReturnValue(of({ status: 200, listData: [] })),
      updateNotifications: jest.fn().mockReturnValue(of({ status: 200 }))
    };

    const userServiceSpy = {
      userId$: of('10')
    };

    await TestBed.configureTestingModule({
      declarations: [ManageNotificationsComponent],
      providers: [
        { provide: CustomerService, useValue: customerServiceSpy },
        { provide: UserService, useValue: userServiceSpy }
      ]
    }).compileComponents();

    mockCustomerService = TestBed.inject(CustomerService) as jest.Mocked<CustomerService>;
    mockUserService = TestBed.inject(UserService) as jest.Mocked<UserService>;

    fixture = TestBed.createComponent(ManageNotificationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getNotifications on ngOnInit', async () => {
    const mockResponse = {
      status: 200,
      listData: [
        {
          accountBalanceUpdates: 'Email',
          accountBlockAlerts: 'Email',
          lowBalanceAlerts: 'Email',
          securityAlerts: 'Email',
          txnConfirmation: 'Email'
        }
      ]
    };
    mockCustomerService.getNotifications.mockReturnValue(of(mockResponse));

    await component.ngOnInit();

    expect(mockCustomerService.getNotifications).toHaveBeenCalled();
    expect(component.accountBalanceUpdatesChecked).toBe(true);
    expect(component.accountBlockAlertsChecked).toBe(true);
    expect(component.lowBalanceAlertChecked).toBe(true);
    expect(component.securityAlertsChecked).toBe(true);
    expect(component.transactionConfirmationChecked).toBe(true);
  });

it('should call updateNotifications when onCheckClick is called', () => {
  // Set the checkbox state to true before calling onCheckClick
  component.transactionConfirmationChecked = true;
  
  const notificationType = 'Transaction_Confirmation';
  const mockResponse = { status: 200 };
  mockCustomerService.updateNotifications.mockReturnValue(of(mockResponse));

  component.onCheckClick(notificationType);

  expect(mockCustomerService.updateNotifications).toHaveBeenCalledWith(
    component.customerId,
    'Txn_Confirmation',
    true
  );
});

  it('should update checkbox state based on notificationType', () => {
    component['transactionConfirmationChecked'] = true;
    const result = component['getCheckboxState']('Transaction_Confirmation'); // Accessing private method
    expect(result).toBe(true);
  });

  it('should set typeofnotification to "Txn_Confirmation" for "Transaction_Confirmation"', () => {
    component.transactionConfirmationChecked = true;
    component.onCheckClick('Transaction_Confirmation');
    expect(component.typeofnotification).toEqual('Txn_Confirmation');
  });
  
  it('should set typeofnotification to "Account_Balance_updates" for "Account_Balance_Updates"', () => {
    component.accountBalanceUpdatesChecked = true;
    component.onCheckClick('Account_Balance_Updates');
    expect(component.typeofnotification).toEqual('Account_Balance_updates');
  });
  
  it('should set typeofnotification to "Low_balance_alerts" for "Low_balance_alert"', () => {
    component.lowBalanceAlertChecked = true;
    component.onCheckClick('Low_balance_alert');
    expect(component.typeofnotification).toEqual('Low_balance_alerts');
  });
  
  it('should set typeofnotification to "Security_alerts" for "Security_Alerts"', () => {
    component.securityAlertsChecked = true;
    component.onCheckClick('Security_Alerts');
    expect(component.typeofnotification).toEqual('Security_alerts');
  });
  
  it('should set typeofnotification to "Account_block_alerts" for "Account_block_alerts"', () => {
    component.accountBlockAlertsChecked = true;
    component.onCheckClick('Account_block_alerts');
    expect(component.typeofnotification).toEqual('Account_block_alerts');
  });
  
  it('should log an error for invalid notification type', () => {
    const consoleErrorSpy = jest.spyOn(console, 'error');
    component.onCheckClick('Invalid_Notification_Type');
    expect(consoleErrorSpy).toHaveBeenCalledWith('Invalid notification type:', 'Invalid_Notification_Type');
  });
  


});
