import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IChildToDiet } from '../child-to-diet.model';
import { ChildToDietService } from '../service/child-to-diet.service';
import { ChildToDietDeleteDialogComponent } from '../delete/child-to-diet-delete-dialog.component';

@Component({
  selector: 'jhi-child-to-diet',
  templateUrl: './child-to-diet.component.html',
})
export class ChildToDietComponent implements OnInit {
  childToDiets?: IChildToDiet[];
  isLoading = false;

  constructor(protected childToDietService: ChildToDietService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.childToDietService.query().subscribe(
      (res: HttpResponse<IChildToDiet[]>) => {
        this.isLoading = false;
        this.childToDiets = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IChildToDiet): number {
    return item.id!;
  }

  delete(childToDiet: IChildToDiet): void {
    const modalRef = this.modalService.open(ChildToDietDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.childToDiet = childToDiet;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
