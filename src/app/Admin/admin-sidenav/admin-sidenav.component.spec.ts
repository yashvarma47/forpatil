import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AdminSidenavComponent } from './admin-sidenav.component';

describe('AdminSidenavComponent', () => {
  let component: AdminSidenavComponent;
  let fixture: ComponentFixture<AdminSidenavComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminSidenavComponent],
      imports: [RouterTestingModule, BrowserAnimationsModule]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminSidenavComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle registration dropdown', () => {
    component.showregistrationDropdown = false;
    component.toggleRegistrationDropdown();
    expect(component.showregistrationDropdown).toBeTruthy();
    component.toggleRegistrationDropdown();
    expect(component.showregistrationDropdown).toBeFalsy();
  });

  it('should toggle customer management dropdown', () => {
    component.showcustomermanagementDropdown = false;
    component.toggleCustomerManagementDropdown();
    expect(component.showcustomermanagementDropdown).toBeTruthy();
    component.toggleCustomerManagementDropdown();
    expect(component.showcustomermanagementDropdown).toBeFalsy();
  });

  it('should navigate to create-customer when redirectToCreateCustomer is called', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToCreateCustomer();
    expect(navigateSpy).toHaveBeenCalledWith(['create-customer']);
  });

  it('should navigate to open-account when redirectToOpenAccount is called', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToOpenAccount();
    expect(navigateSpy).toHaveBeenCalledWith(['open-account']);
  });

  it('should navigate to block-account when redirectToBlockAccount is called', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToBlockAccount();
    expect(navigateSpy).toHaveBeenCalledWith(['block-account']);
  });

  it('should navigate to close-account when redirectToCloseAccount is called', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToCloseAccount();
    expect(navigateSpy).toHaveBeenCalledWith(['close-account']);
  });

  it('should navigate to delete-account when redirectToDeleteCustomer is called', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToDeleteCustomer();
    expect(navigateSpy).toHaveBeenCalledWith(['delete-account']);
  });
});
