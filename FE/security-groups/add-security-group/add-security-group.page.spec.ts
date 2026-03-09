import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AddSecurityGroupPage } from './add-security-group.page';

describe('AddSecurityGroupPage', () => {
  let component: AddSecurityGroupPage;
  let fixture: ComponentFixture<AddSecurityGroupPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(AddSecurityGroupPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
