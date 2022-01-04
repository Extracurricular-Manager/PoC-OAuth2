import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IChild } from '../child.model';

@Component({
  selector: 'jhi-child-detail',
  templateUrl: './child-detail.component.html',
})
export class ChildDetailComponent implements OnInit {
  child: IChild | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ child }) => {
      this.child = child;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
