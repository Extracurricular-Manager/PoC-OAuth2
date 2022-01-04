jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { GradeLevelService } from '../service/grade-level.service';
import { IGradeLevel, GradeLevel } from '../grade-level.model';

import { GradeLevelUpdateComponent } from './grade-level-update.component';

describe('GradeLevel Management Update Component', () => {
  let comp: GradeLevelUpdateComponent;
  let fixture: ComponentFixture<GradeLevelUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gradeLevelService: GradeLevelService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [GradeLevelUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(GradeLevelUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GradeLevelUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gradeLevelService = TestBed.inject(GradeLevelService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const gradeLevel: IGradeLevel = { id: 'CBA' };

      activatedRoute.data = of({ gradeLevel });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(gradeLevel));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GradeLevel>>();
      const gradeLevel = { id: 'ABC' };
      jest.spyOn(gradeLevelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gradeLevel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gradeLevel }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(gradeLevelService.update).toHaveBeenCalledWith(gradeLevel);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GradeLevel>>();
      const gradeLevel = new GradeLevel();
      jest.spyOn(gradeLevelService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gradeLevel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gradeLevel }));
      saveSubject.complete();

      // THEN
      expect(gradeLevelService.create).toHaveBeenCalledWith(gradeLevel);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GradeLevel>>();
      const gradeLevel = { id: 'ABC' };
      jest.spyOn(gradeLevelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gradeLevel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gradeLevelService.update).toHaveBeenCalledWith(gradeLevel);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
