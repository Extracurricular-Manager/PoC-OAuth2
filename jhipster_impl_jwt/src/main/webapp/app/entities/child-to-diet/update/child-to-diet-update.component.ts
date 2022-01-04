import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IChildToDiet, ChildToDiet } from '../child-to-diet.model';
import { ChildToDietService } from '../service/child-to-diet.service';
import { IChild } from 'app/entities/child/child.model';
import { ChildService } from 'app/entities/child/service/child.service';
import { IDiet } from 'app/entities/diet/diet.model';
import { DietService } from 'app/entities/diet/service/diet.service';

@Component({
  selector: 'jhi-child-to-diet-update',
  templateUrl: './child-to-diet-update.component.html',
})
export class ChildToDietUpdateComponent implements OnInit {
  isSaving = false;

  childrenSharedCollection: IChild[] = [];
  dietsSharedCollection: IDiet[] = [];

  editForm = this.fb.group({
    id: [],
    idChild: [],
    idDiet: [],
    idChildren: [],
    idDiets: [],
  });

  constructor(
    protected childToDietService: ChildToDietService,
    protected childService: ChildService,
    protected dietService: DietService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ childToDiet }) => {
      this.updateForm(childToDiet);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const childToDiet = this.createFromForm();
    if (childToDiet.id !== undefined) {
      this.subscribeToSaveResponse(this.childToDietService.update(childToDiet));
    } else {
      this.subscribeToSaveResponse(this.childToDietService.create(childToDiet));
    }
  }

  trackChildById(index: number, item: IChild): number {
    return item.id!;
  }

  trackDietById(index: number, item: IDiet): number {
    return item.id!;
  }

  getSelectedChild(option: IChild, selectedVals?: IChild[]): IChild {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChildToDiet>>): void {
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

  protected updateForm(childToDiet: IChildToDiet): void {
    this.editForm.patchValue({
      id: childToDiet.id,
      idChild: childToDiet.idChild,
      idDiet: childToDiet.idDiet,
      idChildren: childToDiet.idChildren,
      idDiets: childToDiet.idDiets,
    });

    this.childrenSharedCollection = this.childService.addChildToCollectionIfMissing(
      this.childrenSharedCollection,
      ...(childToDiet.idChildren ?? [])
    );
    this.dietsSharedCollection = this.dietService.addDietToCollectionIfMissing(this.dietsSharedCollection, ...(childToDiet.idDiets ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.childService
      .query()
      .pipe(map((res: HttpResponse<IChild[]>) => res.body ?? []))
      .pipe(
        map((children: IChild[]) =>
          this.childService.addChildToCollectionIfMissing(children, ...(this.editForm.get('idChildren')!.value ?? []))
        )
      )
      .subscribe((children: IChild[]) => (this.childrenSharedCollection = children));

    this.dietService
      .query()
      .pipe(map((res: HttpResponse<IDiet[]>) => res.body ?? []))
      .pipe(map((diets: IDiet[]) => this.dietService.addDietToCollectionIfMissing(diets, ...(this.editForm.get('idDiets')!.value ?? []))))
      .subscribe((diets: IDiet[]) => (this.dietsSharedCollection = diets));
  }

  protected createFromForm(): IChildToDiet {
    return {
      ...new ChildToDiet(),
      id: this.editForm.get(['id'])!.value,
      idChild: this.editForm.get(['idChild'])!.value,
      idDiet: this.editForm.get(['idDiet'])!.value,
      idChildren: this.editForm.get(['idChildren'])!.value,
      idDiets: this.editForm.get(['idDiets'])!.value,
    };
  }
}
