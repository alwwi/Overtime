import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AddSecurityGroupPage } from './add-security-group.page';

const routes: Routes = [
  {
    path: '',
    component: AddSecurityGroupPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AddSecurityGroupPageRoutingModule {}
