import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGradeLevel } from '../grade-level.model';
import { GradeLevelService } from '../service/grade-level.service';
import { GradeLevelDeleteDialogComponent } from '../delete/grade-level-delete-dialog.component';

@Component({
  selector: 'jhi-grade-level',
  templateUrl: './grade-level.component.html',
})
export class GradeLevelComponent implements OnInit {
  gradeLevels?: IGradeLevel[];
  isLoading = false;

  constructor(protected gradeLevelService: GradeLevelService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.gradeLevelService.query().subscribe(
      (res: HttpResponse<IGradeLevel[]>) => {
        this.isLoading = false;
        this.gradeLevels = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IGradeLevel): string {
    return item.id!;
  }

  delete(gradeLevel: IGradeLevel): void {
    const modalRef = this.modalService.open(GradeLevelDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.gradeLevel = gradeLevel;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
