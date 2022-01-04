jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { DietService } from '../service/diet.service';
import { IDiet, Diet } from '../diet.model';

import { DietUpdateComponent } from './diet-update.component';

describe('Diet Management Update Component', () => {
  let comp: DietUpdateComponent;
  let fixture: ComponentFixture<DietUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dietService: DietService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [DietUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(DietUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DietUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dietService = TestBed.inject(DietService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const diet: IDiet = { id: 456 };

      activatedRoute.data = of({ diet });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(diet));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Diet>>();
      const diet = { id: 123 };
      jest.spyOn(dietService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: diet }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(dietService.update).toHaveBeenCalledWith(diet);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Diet>>();
      const diet = new Diet();
      jest.spyOn(dietService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: diet }));
      saveSubject.complete();

      // THEN
      expect(dietService.create).toHaveBeenCalledWith(diet);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Diet>>();
      const diet = { id: 123 };
      jest.spyOn(dietService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dietService.update).toHaveBeenCalledWith(diet);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
