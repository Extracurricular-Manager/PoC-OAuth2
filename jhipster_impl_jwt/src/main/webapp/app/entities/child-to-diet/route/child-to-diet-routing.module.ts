import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ChildToDietComponent } from '../list/child-to-diet.component';
import { ChildToDietDetailComponent } from '../detail/child-to-diet-detail.component';
import { ChildToDietUpdateComponent } from '../update/child-to-diet-update.component';
import { ChildToDietRoutingResolveService } from './child-to-diet-routing-resolve.service';

const childToDietRoute: Routes = [
  {
    path: '',
    component: ChildToDietComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ChildToDietDetailComponent,
    resolve: {
      childToDiet: ChildToDietRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ChildToDietUpdateComponent,
    resolve: {
      childToDiet: ChildToDietRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ChildToDietUpdateComponent,
    resolve: {
      childToDiet: ChildToDietRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(childToDietRoute)],
  exports: [RouterModule],
})
export class ChildToDietRoutingModule {}
