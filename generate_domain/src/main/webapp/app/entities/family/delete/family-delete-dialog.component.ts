import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFamily } from '../family.model';
import { FamilyService } from '../service/family.service';

@Component({
  templateUrl: './family-delete-dialog.component.html',
})
export class FamilyDeleteDialogComponent {
  family?: IFamily;

  constructor(protected familyService: FamilyService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.familyService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
