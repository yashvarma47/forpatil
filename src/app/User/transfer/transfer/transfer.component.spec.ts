import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TransferComponent } from './transfer.component';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('TransferComponent', () => {
    let component: TransferComponent;
    let fixture: ComponentFixture<TransferComponent>;
    let router: Router;
  
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [TransferComponent],
        imports: [RouterTestingModule]
      })
      .compileComponents();
    });
  
    beforeEach(() => {
      fixture = TestBed.createComponent(TransferComponent);
      component = fixture.componentInstance;
      router = TestBed.inject(Router);
      fixture.detectChanges();
    });
  
    it('should navigate to selfAccTransfer when "Transfer Within My Account" is clicked', () => {
      const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
      component.onItemClick('Transfer Within My Account');
      expect(navigateSpy).toHaveBeenCalledWith(['selfAccTransfer']);
    });

  it('should navigate to transferWithinBank when "Transfer Within Bank" is clicked', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    component.onItemClick('Transfer Within Bank');
    expect(navigateSpy).toHaveBeenCalledWith(['/transferWithinBank']);
  });

  it('should navigate to external-transfer when "External Transfer" is clicked', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    component.onItemClick('External Transfer');
    expect(navigateSpy).toHaveBeenCalledWith(['/external-transfer']);
  });
  

  it('should navigate to add-beneficiary when "Add Beneficiary" is clicked', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    component.onItemClick('Add Beneficiary');
    expect(navigateSpy).toHaveBeenCalledWith(['add-benficiary']);
  });
  
});
