import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IChild, Child } from '../child.model';
import { ChildService } from '../service/child.service';
import { IClassroom } from 'app/entities/classroom/classroom.model';
import { ClassroomService } from 'app/entities/classroom/service/classroom.service';
import { IFamily } from 'app/entities/family/family.model';
import { FamilyService } from 'app/entities/family/service/family.service';
import { IGradeLevel } from 'app/entities/grade-level/grade-level.model';
import { GradeLevelService } from 'app/entities/grade-level/service/grade-level.service';
import { IDiet } from 'app/entities/diet/diet.model';
import { DietService } from 'app/entities/diet/service/diet.service';

@Component({
  selector: 'jhi-child-update',
  templateUrl: './child-update.component.html',
})
export class ChildUpdateComponent implements OnInit {
  isSaving = false;

  classroomsSharedCollection: IClassroom[] = [];
  familiesSharedCollection: IFamily[] = [];
  gradeLevelsSharedCollection: IGradeLevel[] = [];
  dietsSharedCollection: IDiet[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    surname: [],
    birthday: [],
    gradeLevel: [],
    classroom: [],
    adelphie: [],
    diet: [],
    classroom: [],
    adelphie: [],
    gradeLevel: [],
    diets: [],
  });

  constructor(
    protected childService: ChildService,
    protected classroomService: ClassroomService,
    protected familyService: FamilyService,
    protected gradeLevelService: GradeLevelService,
    protected dietService: DietService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ child }) => {
      if (child.id === undefined) {
        const today = dayjs().startOf('day');
        child.birthday = today;
      }

      this.updateForm(child);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const child = this.createFromForm();
    if (child.id !== undefined) {
      this.subscribeToSaveResponse(this.childService.update(child));
    } else {
      this.subscribeToSaveResponse(this.childService.create(child));
    }
  }

  trackClassroomById(index: number, item: IClassroom): number {
    return item.id!;
  }

  trackFamilyById(index: number, item: IFamily): number {
    return item.id!;
  }

  trackGradeLevelById(index: number, item: IGradeLevel): string {
    return item.id!;
  }

  trackDietById(index: number, item: IDiet): number {
    return item.id!;
  }

  getSelectedDiet(option: IDiet, selectedVals?: IDiet[]): IDiet {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChild>>): void {
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

  protected updateForm(child: IChild): void {
    this.editForm.patchValue({
      id: child.id,
      name: child.name,
      surname: child.surname,
      birthday: child.birthday ? child.birthday.format(DATE_TIME_FORMAT) : null,
      gradeLevel: child.gradeLevel,
      classroom: child.classroom,
      adelphie: child.adelphie,
      diet: child.diet,
      classroom: child.classroom,
      adelphie: child.adelphie,
      gradeLevel: child.gradeLevel,
      diets: child.diets,
    });

    this.classroomsSharedCollection = this.classroomService.addClassroomToCollectionIfMissing(
      this.classroomsSharedCollection,
      child.classroom
    );
    this.familiesSharedCollection = this.familyService.addFamilyToCollectionIfMissing(this.familiesSharedCollection, child.adelphie);
    this.gradeLevelsSharedCollection = this.gradeLevelService.addGradeLevelToCollectionIfMissing(
      this.gradeLevelsSharedCollection,
      child.gradeLevel
    );
    this.dietsSharedCollection = this.dietService.addDietToCollectionIfMissing(this.dietsSharedCollection, ...(child.diets ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.classroomService
      .query()
      .pipe(map((res: HttpResponse<IClassroom[]>) => res.body ?? []))
      .pipe(
        map((classrooms: IClassroom[]) =>
          this.classroomService.addClassroomToCollectionIfMissing(classrooms, this.editForm.get('classroom')!.value)
        )
      )
      .subscribe((classrooms: IClassroom[]) => (this.classroomsSharedCollection = classrooms));

    this.familyService
      .query()
      .pipe(map((res: HttpResponse<IFamily[]>) => res.body ?? []))
      .pipe(map((families: IFamily[]) => this.familyService.addFamilyToCollectionIfMissing(families, this.editForm.get('adelphie')!.value)))
      .subscribe((families: IFamily[]) => (this.familiesSharedCollection = families));

    this.gradeLevelService
      .query()
      .pipe(map((res: HttpResponse<IGradeLevel[]>) => res.body ?? []))
      .pipe(
        map((gradeLevels: IGradeLevel[]) =>
          this.gradeLevelService.addGradeLevelToCollectionIfMissing(gradeLevels, this.editForm.get('gradeLevel')!.value)
        )
      )
      .subscribe((gradeLevels: IGradeLevel[]) => (this.gradeLevelsSharedCollection = gradeLevels));

    this.dietService
      .query()
      .pipe(map((res: HttpResponse<IDiet[]>) => res.body ?? []))
      .pipe(map((diets: IDiet[]) => this.dietService.addDietToCollectionIfMissing(diets, ...(this.editForm.get('diets')!.value ?? []))))
      .subscribe((diets: IDiet[]) => (this.dietsSharedCollection = diets));
  }

  protected createFromForm(): IChild {
    return {
      ...new Child(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      surname: this.editForm.get(['surname'])!.value,
      birthday: this.editForm.get(['birthday'])!.value ? dayjs(this.editForm.get(['birthday'])!.value, DATE_TIME_FORMAT) : undefined,
      gradeLevel: this.editForm.get(['gradeLevel'])!.value,
      classroom: this.editForm.get(['classroom'])!.value,
      adelphie: this.editForm.get(['adelphie'])!.value,
      diet: this.editForm.get(['diet'])!.value,
      classroom: this.editForm.get(['classroom'])!.value,
      adelphie: this.editForm.get(['adelphie'])!.value,
      gradeLevel: this.editForm.get(['gradeLevel'])!.value,
      diets: this.editForm.get(['diets'])!.value,
    };
  }
}
