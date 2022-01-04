import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDiet } from '../diet.model';

@Component({
  selector: 'jhi-diet-detail',
  templateUrl: './diet-detail.component.html',
})
export class DietDetailComponent implements OnInit {
  diet: IDiet | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ diet }) => {
      this.diet = diet;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
