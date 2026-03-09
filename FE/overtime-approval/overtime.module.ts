import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { OvertimePageRoutingModule } from './overtime-routing.module';

import { OvertimePage } from './overtime.page';
import { GlobalComponentsModule } from 'src/app/components/components.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    OvertimePageRoutingModule,
    GlobalComponentsModule
  ],
  declarations: [OvertimePage]
})
export class OvertimePageModule { }
