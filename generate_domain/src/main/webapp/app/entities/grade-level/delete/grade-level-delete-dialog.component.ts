import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGradeLevel } from '../grade-level.model';
import { GradeLevelService } from '../service/grade-level.service';

@Component({
  templateUrl: './grade-level-delete-dialog.component.html',
})
export class GradeLevelDeleteDialogComponent {
  gradeLevel?: IGradeLevel;

  constructor(protected gradeLevelService: GradeLevelService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.gradeLevelService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
