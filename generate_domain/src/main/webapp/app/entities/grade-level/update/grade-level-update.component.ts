import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IGradeLevel, GradeLevel } from '../grade-level.model';
import { GradeLevelService } from '../service/grade-level.service';

@Component({
  selector: 'jhi-grade-level-update',
  templateUrl: './grade-level-update.component.html',
})
export class GradeLevelUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
  });

  constructor(protected gradeLevelService: GradeLevelService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gradeLevel }) => {
      this.updateForm(gradeLevel);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const gradeLevel = this.createFromForm();
    if (gradeLevel.id !== undefined) {
      this.subscribeToSaveResponse(this.gradeLevelService.update(gradeLevel));
    } else {
      this.subscribeToSaveResponse(this.gradeLevelService.create(gradeLevel));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGradeLevel>>): void {
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

  protected updateForm(gradeLevel: IGradeLevel): void {
    this.editForm.patchValue({
      id: gradeLevel.id,
    });
  }

  protected createFromForm(): IGradeLevel {
    return {
      ...new GradeLevel(),
      id: this.editForm.get(['id'])!.value,
    };
  }
}
