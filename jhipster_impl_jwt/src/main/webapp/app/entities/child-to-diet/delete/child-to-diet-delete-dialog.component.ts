import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IChildToDiet } from '../child-to-diet.model';
import { ChildToDietService } from '../service/child-to-diet.service';

@Component({
  templateUrl: './child-to-diet-delete-dialog.component.html',
})
export class ChildToDietDeleteDialogComponent {
  childToDiet?: IChildToDiet;

  constructor(protected childToDietService: ChildToDietService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.childToDietService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
