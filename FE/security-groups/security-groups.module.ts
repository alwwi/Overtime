import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SecurityGroupsPageRoutingModule } from './security-groups-routing.module';

import { SecurityGroupsPage } from './security-groups.page';
import { GlobalComponentsModule } from 'src/app/components/components.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SecurityGroupsPageRoutingModule,
    GlobalComponentsModule
  ],
  declarations: [SecurityGroupsPage]
})
export class SecurityGroupsPageModule { }
