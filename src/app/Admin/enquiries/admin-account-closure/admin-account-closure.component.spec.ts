import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminAccountClosureComponent } from './admin-account-closure.component';

describe('AdminAccountClosureComponent', () => {
  let component: AdminAccountClosureComponent;
  let fixture: ComponentFixture<AdminAccountClosureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminAccountClosureComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminAccountClosureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
