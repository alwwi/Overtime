import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OvertimePage } from './overtime.page';

describe('OvertimePage', () => {
  let component: OvertimePage;
  let fixture: ComponentFixture<OvertimePage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(OvertimePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
