import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ChildToDietComponent } from './list/child-to-diet.component';
import { ChildToDietDetailComponent } from './detail/child-to-diet-detail.component';
import { ChildToDietUpdateComponent } from './update/child-to-diet-update.component';
import { ChildToDietDeleteDialogComponent } from './delete/child-to-diet-delete-dialog.component';
import { ChildToDietRoutingModule } from './route/child-to-diet-routing.module';

@NgModule({
  imports: [SharedModule, ChildToDietRoutingModule],
  declarations: [ChildToDietComponent, ChildToDietDetailComponent, ChildToDietUpdateComponent, ChildToDietDeleteDialogComponent],
  entryComponents: [ChildToDietDeleteDialogComponent],
})
export class ChildToDietModule {}
