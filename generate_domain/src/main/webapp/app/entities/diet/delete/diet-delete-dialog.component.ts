import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDiet } from '../diet.model';
import { DietService } from '../service/diet.service';

@Component({
  templateUrl: './diet-delete-dialog.component.html',
})
export class DietDeleteDialogComponent {
  diet?: IDiet;

  constructor(protected dietService: DietService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.dietService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
