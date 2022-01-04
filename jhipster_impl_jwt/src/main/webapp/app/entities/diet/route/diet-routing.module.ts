import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DietComponent } from '../list/diet.component';
import { DietDetailComponent } from '../detail/diet-detail.component';
import { DietUpdateComponent } from '../update/diet-update.component';
import { DietRoutingResolveService } from './diet-routing-resolve.service';

const dietRoute: Routes = [
  {
    path: '',
    component: DietComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DietDetailComponent,
    resolve: {
      diet: DietRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DietUpdateComponent,
    resolve: {
      diet: DietRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DietUpdateComponent,
    resolve: {
      diet: DietRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(dietRoute)],
  exports: [RouterModule],
})
export class DietRoutingModule {}
