import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SecurityGroupsPage } from './security-groups.page';

describe('SecurityGroupsPage', () => {
  let component: SecurityGroupsPage;
  let fixture: ComponentFixture<SecurityGroupsPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(SecurityGroupsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
