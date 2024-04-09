import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { SidenavComponent } from './sidenav.component';

describe('SidenavComponent', () => {
  let component: SidenavComponent;
  let fixture: ComponentFixture<SidenavComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SidenavComponent],
      imports: [
        RouterTestingModule.withRoutes([]),
        MatExpansionModule,
        MatIconModule,
        MatListModule
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SidenavComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle account dropdown', () => {
    expect(component.showAccountDropdown).toBeFalsy();
    component.toggleAccountDropdown();
    expect(component.showAccountDropdown).toBeTruthy();
  });

  it('should toggle profile dropdown', () => {
    expect(component.showProfileDropdown).toBeFalsy();
    component.toggleProfileDropdown();
    expect(component.showProfileDropdown).toBeTruthy();
  });

  it('should navigate to account details', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToComponent1();
    expect(navigateSpy).toHaveBeenCalledWith(['accountdetails']);
  });

  it('should navigate to view account balance', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToComponent2();
    expect(navigateSpy).toHaveBeenCalledWith(['view-account-balance']);
  });

  it('should navigate to account closure', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToComponent3();
    expect(navigateSpy).toHaveBeenCalledWith(['account-closure']);
  });

  it('should navigate to setup profile', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToComponent4();
    expect(navigateSpy).toHaveBeenCalledWith(['setup-profile']);
  });

  it('should navigate to change password', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToComponent5();
    expect(navigateSpy).toHaveBeenCalledWith(['change-password']);
  });

  it('should navigate to setup budget', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToComponent6();
    expect(navigateSpy).toHaveBeenCalledWith(['setup-budget']);
  });

  it('should navigate to manage notifications', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.redirectToComponent7();
    expect(navigateSpy).toHaveBeenCalledWith(['manage-notifications']);
  });
});
