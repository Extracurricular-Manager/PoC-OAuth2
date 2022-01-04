import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IChildToDiet } from '../child-to-diet.model';

@Component({
  selector: 'jhi-child-to-diet-detail',
  templateUrl: './child-to-diet-detail.component.html',
})
export class ChildToDietDetailComponent implements OnInit {
  childToDiet: IChildToDiet | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ childToDiet }) => {
      this.childToDiet = childToDiet;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
