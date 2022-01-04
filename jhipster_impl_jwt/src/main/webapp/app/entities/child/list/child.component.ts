import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IChild } from '../child.model';
import { ChildService } from '../service/child.service';
import { ChildDeleteDialogComponent } from '../delete/child-delete-dialog.component';

@Component({
  selector: 'jhi-child',
  templateUrl: './child.component.html',
})
export class ChildComponent implements OnInit {
  children?: IChild[];
  isLoading = false;

  constructor(protected childService: ChildService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.childService.query().subscribe(
      (res: HttpResponse<IChild[]>) => {
        this.isLoading = false;
        this.children = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IChild): number {
    return item.id!;
  }

  delete(child: IChild): void {
    const modalRef = this.modalService.open(ChildDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.child = child;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
