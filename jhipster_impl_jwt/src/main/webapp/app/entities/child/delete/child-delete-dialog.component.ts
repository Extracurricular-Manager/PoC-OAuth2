import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IChild } from '../child.model';
import { ChildService } from '../service/child.service';

@Component({
  templateUrl: './child-delete-dialog.component.html',
})
export class ChildDeleteDialogComponent {
  child?: IChild;

  constructor(protected childService: ChildService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.childService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
