import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DietComponent } from './list/diet.component';
import { DietDetailComponent } from './detail/diet-detail.component';
import { DietUpdateComponent } from './update/diet-update.component';
import { DietDeleteDialogComponent } from './delete/diet-delete-dialog.component';
import { DietRoutingModule } from './route/diet-routing.module';

@NgModule({
  imports: [SharedModule, DietRoutingModule],
  declarations: [DietComponent, DietDetailComponent, DietUpdateComponent, DietDeleteDialogComponent],
  entryComponents: [DietDeleteDialogComponent],
})
export class DietModule {}
