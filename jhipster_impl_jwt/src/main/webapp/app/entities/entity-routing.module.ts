import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'child',
        data: { pageTitle: 'Children' },
        loadChildren: () => import('./child/child.module').then(m => m.ChildModule),
      },
      {
        path: 'family',
        data: { pageTitle: 'Families' },
        loadChildren: () => import('./family/family.module').then(m => m.FamilyModule),
      },
      {
        path: 'grade-level',
        data: { pageTitle: 'GradeLevels' },
        loadChildren: () => import('./grade-level/grade-level.module').then(m => m.GradeLevelModule),
      },
      {
        path: 'classroom',
        data: { pageTitle: 'Classrooms' },
        loadChildren: () => import('./classroom/classroom.module').then(m => m.ClassroomModule),
      },
      {
        path: 'diet',
        data: { pageTitle: 'Diets' },
        loadChildren: () => import('./diet/diet.module').then(m => m.DietModule),
      },
      {
        path: 'child-to-diet',
        data: { pageTitle: 'ChildToDiets' },
        loadChildren: () => import('./child-to-diet/child-to-diet.module').then(m => m.ChildToDietModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
