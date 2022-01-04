import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ChildComponent } from '../list/child.component';
import { ChildDetailComponent } from '../detail/child-detail.component';
import { ChildUpdateComponent } from '../update/child-update.component';
import { ChildRoutingResolveService } from './child-routing-resolve.service';

const childRoute: Routes = [
  {
    path: '',
    component: ChildComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ChildDetailComponent,
    resolve: {
      child: ChildRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ChildUpdateComponent,
    resolve: {
      child: ChildRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ChildUpdateComponent,
    resolve: {
      child: ChildRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(childRoute)],
  exports: [RouterModule],
})
export class ChildRoutingModule {}
