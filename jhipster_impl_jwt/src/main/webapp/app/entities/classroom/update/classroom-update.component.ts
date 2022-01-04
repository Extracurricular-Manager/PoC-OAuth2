import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IClassroom, Classroom } from '../classroom.model';
import { ClassroomService } from '../service/classroom.service';

@Component({
  selector: 'jhi-classroom-update',
  templateUrl: './classroom-update.component.html',
})
export class ClassroomUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    professor: [],
  });

  constructor(protected classroomService: ClassroomService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ classroom }) => {
      this.updateForm(classroom);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const classroom = this.createFromForm();
    if (classroom.id !== undefined) {
      this.subscribeToSaveResponse(this.classroomService.update(classroom));
    } else {
      this.subscribeToSaveResponse(this.classroomService.create(classroom));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClassroom>>): void {
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

  protected updateForm(classroom: IClassroom): void {
    this.editForm.patchValue({
      id: classroom.id,
      name: classroom.name,
      professor: classroom.professor,
    });
  }

  protected createFromForm(): IClassroom {
    return {
      ...new Classroom(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      professor: this.editForm.get(['professor'])!.value,
    };
  }
}
