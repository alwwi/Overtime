import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SecurityGroupsPage } from './security-groups.page';

const routes: Routes = [
  {
    path: '',
    component: SecurityGroupsPage
  },
  {
    path: 'add-security-group',
    loadChildren: () => import('./add-security-group/add-security-group.module').then(m => m.AddSecurityGroupPageModule)
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SecurityGroupsPageRoutingModule { }
