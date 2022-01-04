import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IDiet, Diet } from '../diet.model';
import { DietService } from '../service/diet.service';

@Component({
  selector: 'jhi-diet-update',
  templateUrl: './diet-update.component.html',
})
export class DietUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    description: [],
  });

  constructor(protected dietService: DietService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ diet }) => {
      this.updateForm(diet);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const diet = this.createFromForm();
    if (diet.id !== undefined) {
      this.subscribeToSaveResponse(this.dietService.update(diet));
    } else {
      this.subscribeToSaveResponse(this.dietService.create(diet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDiet>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(diet: IDiet): void {
    this.editForm.patchValue({
      id: diet.id,
      name: diet.name,
      description: diet.description,
    });
  }

  protected createFromForm(): IDiet {
    return {
      ...new Diet(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
