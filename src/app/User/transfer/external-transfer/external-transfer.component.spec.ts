import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExternalTransferComponent } from './external-transfer.component';

describe('ExternalTransferComponent', () => {
  let component: ExternalTransferComponent;
  let fixture: ComponentFixture<ExternalTransferComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ExternalTransferComponent]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExternalTransferComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should render the component content', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('p').textContent).toContain('external-transfer works!');
  });
});
