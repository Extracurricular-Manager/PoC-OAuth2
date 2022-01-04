import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GradeLevelComponent } from '../list/grade-level.component';
import { GradeLevelDetailComponent } from '../detail/grade-level-detail.component';
import { GradeLevelUpdateComponent } from '../update/grade-level-update.component';
import { GradeLevelRoutingResolveService } from './grade-level-routing-resolve.service';

const gradeLevelRoute: Routes = [
  {
    path: '',
    component: GradeLevelComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GradeLevelDetailComponent,
    resolve: {
      gradeLevel: GradeLevelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GradeLevelUpdateComponent,
    resolve: {
      gradeLevel: GradeLevelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GradeLevelUpdateComponent,
    resolve: {
      gradeLevel: GradeLevelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(gradeLevelRoute)],
  exports: [RouterModule],
})
export class GradeLevelRoutingModule {}
