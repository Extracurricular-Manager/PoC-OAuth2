jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ChildService } from '../service/child.service';
import { IChild, Child } from '../child.model';
import { IClassroom } from 'app/entities/classroom/classroom.model';
import { ClassroomService } from 'app/entities/classroom/service/classroom.service';
import { IFamily } from 'app/entities/family/family.model';
import { FamilyService } from 'app/entities/family/service/family.service';
import { IGradeLevel } from 'app/entities/grade-level/grade-level.model';
import { GradeLevelService } from 'app/entities/grade-level/service/grade-level.service';
import { IDiet } from 'app/entities/diet/diet.model';
import { DietService } from 'app/entities/diet/service/diet.service';

import { ChildUpdateComponent } from './child-update.component';

describe('Child Management Update Component', () => {
  let comp: ChildUpdateComponent;
  let fixture: ComponentFixture<ChildUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let childService: ChildService;
  let classroomService: ClassroomService;
  let familyService: FamilyService;
  let gradeLevelService: GradeLevelService;
  let dietService: DietService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ChildUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(ChildUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ChildUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    childService = TestBed.inject(ChildService);
    classroomService = TestBed.inject(ClassroomService);
    familyService = TestBed.inject(FamilyService);
    gradeLevelService = TestBed.inject(GradeLevelService);
    dietService = TestBed.inject(DietService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Classroom query and add missing value', () => {
      const child: IChild = { id: 456 };
      const classroom: IClassroom = { id: 6325 };
      child.classroom = classroom;

      const classroomCollection: IClassroom[] = [{ id: 28826 }];
      jest.spyOn(classroomService, 'query').mockReturnValue(of(new HttpResponse({ body: classroomCollection })));
      const additionalClassrooms = [classroom];
      const expectedCollection: IClassroom[] = [...additionalClassrooms, ...classroomCollection];
      jest.spyOn(classroomService, 'addClassroomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ child });
      comp.ngOnInit();

      expect(classroomService.query).toHaveBeenCalled();
      expect(classroomService.addClassroomToCollectionIfMissing).toHaveBeenCalledWith(classroomCollection, ...additionalClassrooms);
      expect(comp.classroomsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Family query and add missing value', () => {
      const child: IChild = { id: 456 };
      const adelphie: IFamily = { id: 38838 };
      child.adelphie = adelphie;

      const familyCollection: IFamily[] = [{ id: 43836 }];
      jest.spyOn(familyService, 'query').mockReturnValue(of(new HttpResponse({ body: familyCollection })));
      const additionalFamilies = [adelphie];
      const expectedCollection: IFamily[] = [...additionalFamilies, ...familyCollection];
      jest.spyOn(familyService, 'addFamilyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ child });
      comp.ngOnInit();

      expect(familyService.query).toHaveBeenCalled();
      expect(familyService.addFamilyToCollectionIfMissing).toHaveBeenCalledWith(familyCollection, ...additionalFamilies);
      expect(comp.familiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call GradeLevel query and add missing value', () => {
      const child: IChild = { id: 456 };
      const gradeLevel: IGradeLevel = { id: '3a686c12-0c43-4f3c-a5ef-96909d38840b' };
      child.gradeLevel = gradeLevel;

      const gradeLevelCollection: IGradeLevel[] = [{ id: '75ca52a3-5dc8-49d5-a51d-41d85089a2bc' }];
      jest.spyOn(gradeLevelService, 'query').mockReturnValue(of(new HttpResponse({ body: gradeLevelCollection })));
      const additionalGradeLevels = [gradeLevel];
      const expectedCollection: IGradeLevel[] = [...additionalGradeLevels, ...gradeLevelCollection];
      jest.spyOn(gradeLevelService, 'addGradeLevelToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ child });
      comp.ngOnInit();

      expect(gradeLevelService.query).toHaveBeenCalled();
      expect(gradeLevelService.addGradeLevelToCollectionIfMissing).toHaveBeenCalledWith(gradeLevelCollection, ...additionalGradeLevels);
      expect(comp.gradeLevelsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Diet query and add missing value', () => {
      const child: IChild = { id: 456 };
      const diets: IDiet[] = [{ id: 94608 }];
      child.diets = diets;

      const dietCollection: IDiet[] = [{ id: 27187 }];
      jest.spyOn(dietService, 'query').mockReturnValue(of(new HttpResponse({ body: dietCollection })));
      const additionalDiets = [...diets];
      const expectedCollection: IDiet[] = [...additionalDiets, ...dietCollection];
      jest.spyOn(dietService, 'addDietToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ child });
      comp.ngOnInit();

      expect(dietService.query).toHaveBeenCalled();
      expect(dietService.addDietToCollectionIfMissing).toHaveBeenCalledWith(dietCollection, ...additionalDiets);
      expect(comp.dietsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const child: IChild = { id: 456 };
      const classroom: IClassroom = { id: 35705 };
      child.classroom = classroom;
      const adelphie: IFamily = { id: 77198 };
      child.adelphie = adelphie;
      const gradeLevel: IGradeLevel = { id: '2d0877d2-5ba1-43cd-9465-5004f8bbb733' };
      child.gradeLevel = gradeLevel;
      const diets: IDiet = { id: 15914 };
      child.diets = [diets];

      activatedRoute.data = of({ child });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(child));
      expect(comp.classroomsSharedCollection).toContain(classroom);
      expect(comp.familiesSharedCollection).toContain(adelphie);
      expect(comp.gradeLevelsSharedCollection).toContain(gradeLevel);
      expect(comp.dietsSharedCollection).toContain(diets);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Child>>();
      const child = { id: 123 };
      jest.spyOn(childService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ child });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: child }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(childService.update).toHaveBeenCalledWith(child);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Child>>();
      const child = new Child();
      jest.spyOn(childService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ child });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: child }));
      saveSubject.complete();

      // THEN
      expect(childService.create).toHaveBeenCalledWith(child);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Child>>();
      const child = { id: 123 };
      jest.spyOn(childService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ child });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(childService.update).toHaveBeenCalledWith(child);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackClassroomById', () => {
      it('Should return tracked Classroom primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackClassroomById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackFamilyById', () => {
      it('Should return tracked Family primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackFamilyById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackGradeLevelById', () => {
      it('Should return tracked GradeLevel primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackGradeLevelById(0, entity);
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
