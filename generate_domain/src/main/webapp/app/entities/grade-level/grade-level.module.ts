import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GradeLevelComponent } from './list/grade-level.component';
import { GradeLevelDetailComponent } from './detail/grade-level-detail.component';
import { GradeLevelUpdateComponent } from './update/grade-level-update.component';
import { GradeLevelDeleteDialogComponent } from './delete/grade-level-delete-dialog.component';
import { GradeLevelRoutingModule } from './route/grade-level-routing.module';

@NgModule({
  imports: [SharedModule, GradeLevelRoutingModule],
  declarations: [GradeLevelComponent, GradeLevelDetailComponent, GradeLevelUpdateComponent, GradeLevelDeleteDialogComponent],
  entryComponents: [GradeLevelDeleteDialogComponent],
})
export class GradeLevelModule {}
