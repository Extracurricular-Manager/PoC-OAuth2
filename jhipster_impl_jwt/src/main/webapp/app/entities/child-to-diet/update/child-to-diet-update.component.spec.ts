jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ChildToDietService } from '../service/child-to-diet.service';
import { IChildToDiet, ChildToDiet } from '../child-to-diet.model';
import { IChild } from 'app/entities/child/child.model';
import { ChildService } from 'app/entities/child/service/child.service';
import { IDiet } from 'app/entities/diet/diet.model';
import { DietService } from 'app/entities/diet/service/diet.service';

import { ChildToDietUpdateComponent } from './child-to-diet-update.component';

describe('ChildToDiet Management Update Component', () => {
  let comp: ChildToDietUpdateComponent;
  let fixture: ComponentFixture<ChildToDietUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let childToDietService: ChildToDietService;
  let childService: ChildService;
  let dietService: DietService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ChildToDietUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(ChildToDietUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ChildToDietUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    childToDietService = TestBed.inject(ChildToDietService);
    childService = TestBed.inject(ChildService);
    dietService = TestBed.inject(DietService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Child query and add missing value', () => {
      const childToDiet: IChildToDiet = { id: 456 };
      const idChildren: IChild[] = [{ id: 29910 }];
      childToDiet.idChildren = idChildren;

      const childCollection: IChild[] = [{ id: 79348 }];
      jest.spyOn(childService, 'query').mockReturnValue(of(new HttpResponse({ body: childCollection })));
      const additionalChildren = [...idChildren];
      const expectedCollection: IChild[] = [...additionalChildren, ...childCollection];
      jest.spyOn(childService, 'addChildToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ childToDiet });
      comp.ngOnInit();

      expect(childService.query).toHaveBeenCalled();
      expect(childService.addChildToCollectionIfMissing).toHaveBeenCalledWith(childCollection, ...additionalChildren);
      expect(comp.childrenSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Diet query and add missing value', () => {
      const childToDiet: IChildToDiet = { id: 456 };
      const idDiets: IDiet[] = [{ id: 34088 }];
      childToDiet.idDiets = idDiets;

      const dietCollection: IDiet[] = [{ id: 59629 }];
      jest.spyOn(dietService, 'query').mockReturnValue(of(new HttpResponse({ body: dietCollection })));
      const additionalDiets = [...idDiets];
      const expectedCollection: IDiet[] = [...additionalDiets, ...dietCollection];
      jest.spyOn(dietService, 'addDietToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ childToDiet });
      comp.ngOnInit();

      expect(dietService.query).toHaveBeenCalled();
      expect(dietService.addDietToCollectionIfMissing).toHaveBeenCalledWith(dietCollection, ...additionalDiets);
      expect(comp.dietsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const childToDiet: IChildToDiet = { id: 456 };
      const idChildren: IChild = { id: 67598 };
      childToDiet.idChildren = [idChildren];
      const idDiets: IDiet = { id: 28604 };
      childToDiet.idDiets = [idDiets];

      activatedRoute.data = of({ childToDiet });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(childToDiet));
      expect(comp.childrenSharedCollection).toContain(idChildren);
      expect(comp.dietsSharedCollection).toContain(idDiets);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ChildToDiet>>();
      const childToDiet = { id: 123 };
      jest.spyOn(childToDietService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ childToDiet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: childToDiet }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(childToDietService.update).toHaveBeenCalledWith(childToDiet);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ChildToDiet>>();
      const childToDiet = new ChildToDiet();
      jest.spyOn(childToDietService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ childToDiet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: childToDiet }));
      saveSubject.complete();

      // THEN
      expect(childToDietService.create).toHaveBeenCalledWith(childToDiet);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ChildToDiet>>();
      const childToDiet = { id: 123 };
      jest.spyOn(childToDietService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ childToDiet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(childToDietService.update).toHaveBeenCalledWith(childToDiet);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackChildById', () => {
      it('Should return tracked Child primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackChildById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackDietById', () => {
      it('Should return tracked Diet primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackDietById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedChild', () => {
      it('Should return option if no Child is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedChild(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Child for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedChild(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Child is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedChild(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });

    describe('getSelectedDiet', () => {
      it('Should return option if no Diet is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedDiet(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Diet for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedDiet(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Diet is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedDiet(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
