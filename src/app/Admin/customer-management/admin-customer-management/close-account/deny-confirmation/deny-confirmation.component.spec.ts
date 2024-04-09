import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DenyConfirmationComponent } from './deny-confirmation.component';

describe('DenyConfirmationComponent', () => {
  let component: DenyConfirmationComponent;
  let fixture: ComponentFixture<DenyConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DenyConfirmationComponent]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DenyConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit the reason when submit is called', () => {
    const reason = 'Rejected due to insufficient documents';
    spyOn(component.submitReason, 'emit');
    component.reason = reason;
    component.submit();
    expect(component.submitReason.emit).toHaveBeenCalledWith(reason);
    expect(component.isUpdateTicket).toBeTruthy(); // Change to toBeTruthy
  });

  it('should emit cancel event when cancelPopup is called', () => {
    spyOn(component.cancel, 'emit');
    component.cancelPopup();
    expect(component.cancel.emit).toHaveBeenCalled();
    expect(component.isPopupOpen).toBeFalsy(); // Change to toBeFalsy
  });
});
