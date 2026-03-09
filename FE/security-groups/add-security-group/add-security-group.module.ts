import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { AddSecurityGroupPageRoutingModule } from './add-security-group-routing.module';

import { AddSecurityGroupPage } from './add-security-group.page';
import { GlobalComponentsModule } from 'src/app/components/components.module';
import { AddSecurityGroupDetailComponent } from '../add-security-group-detail/add-security-group-detail.component';
import { FloatLabelModule } from 'primeng/floatlabel';
import { DropdownModule } from 'primeng/dropdown';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    AddSecurityGroupPageRoutingModule,
    GlobalComponentsModule,
    DropdownModule,
    FloatLabelModule
  ],
  declarations: [AddSecurityGroupPage, AddSecurityGroupDetailComponent]
})
export class AddSecurityGroupPageModule { }
