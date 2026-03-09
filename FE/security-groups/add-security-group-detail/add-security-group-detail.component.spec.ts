import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { AddSecurityGroupDetailComponent } from './add-security-group-detail.component';

describe('AddSecurityGroupDetailComponent', () => {
  let component: AddSecurityGroupDetailComponent;
  let fixture: ComponentFixture<AddSecurityGroupDetailComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ AddSecurityGroupDetailComponent ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(AddSecurityGroupDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
