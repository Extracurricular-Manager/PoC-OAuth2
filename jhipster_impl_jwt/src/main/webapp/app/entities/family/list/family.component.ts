import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IFamily } from '../family.model';
import { FamilyService } from '../service/family.service';
import { FamilyDeleteDialogComponent } from '../delete/family-delete-dialog.component';

@Component({
  selector: 'jhi-family',
  templateUrl: './family.component.html',
})
export class FamilyComponent implements OnInit {
  families?: IFamily[];
  isLoading = false;

  constructor(protected familyService: FamilyService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.familyService.query().subscribe(
      (res: HttpResponse<IFamily[]>) => {
        this.isLoading = false;
        this.families = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IFamily): number {
    return item.id!;
  }

  delete(family: IFamily): void {
    const modalRef = this.modalService.open(FamilyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.family = family;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
