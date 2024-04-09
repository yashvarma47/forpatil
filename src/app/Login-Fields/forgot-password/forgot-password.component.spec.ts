import { TestBed, ComponentFixture } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ForgotPasswordComponent } from './forgot-password.component';

describe('ForgotPasswordComponent', () => {
  let component: ForgotPasswordComponent;
  let fixture: ComponentFixture<ForgotPasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ForgotPasswordComponent],
      imports: [HttpClientTestingModule] // Import HttpClientTestingModule here
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ForgotPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with default values', () => {
    expect(component.forgetPassForm.get('customerId')!.value).toEqual('');
    expect(component.forgetPassForm.get('panNumber')!.value).toEqual('');
    expect(component.forgetPassForm.get('phone')!.value).toEqual('');
    expect(component.resetLinkSent).toEqual(false);
    expect(component.isDobDisabled).toEqual(true);
  });

  it('should have a method to navigate back', () => {
    spyOn(component['router'], 'navigate');
    component.goBack();
    expect(component['router'].navigate).toHaveBeenCalled();
  });

  // You can add more test cases for other methods and scenarios as needed
});
