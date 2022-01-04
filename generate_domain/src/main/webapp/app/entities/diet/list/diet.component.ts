import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IDiet } from '../diet.model';
import { DietService } from '../service/diet.service';
import { DietDeleteDialogComponent } from '../delete/diet-delete-dialog.component';

@Component({
  selector: 'jhi-diet',
  templateUrl: './diet.component.html',
})
export class DietComponent implements OnInit {
  diets?: IDiet[];
  isLoading = false;

  constructor(protected dietService: DietService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.dietService.query().subscribe(
      (res: HttpResponse<IDiet[]>) => {
        this.isLoading = false;
        this.diets = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IDiet): number {
    return item.id!;
  }

  delete(diet: IDiet): void {
    const modalRef = this.modalService.open(DietDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.diet = diet;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
