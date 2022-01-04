import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ChildComponent } from './list/child.component';
import { ChildDetailComponent } from './detail/child-detail.component';
import { ChildUpdateComponent } from './update/child-update.component';
import { ChildDeleteDialogComponent } from './delete/child-delete-dialog.component';
import { ChildRoutingModule } from './route/child-routing.module';

@NgModule({
  imports: [SharedModule, ChildRoutingModule],
  declarations: [ChildComponent, ChildDetailComponent, ChildUpdateComponent, ChildDeleteDialogComponent],
  entryComponents: [ChildDeleteDialogComponent],
})
export class ChildModule {}
