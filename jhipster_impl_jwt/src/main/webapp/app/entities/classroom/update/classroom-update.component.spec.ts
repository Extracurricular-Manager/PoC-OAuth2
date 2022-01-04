jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ClassroomService } from '../service/classroom.service';
import { IClassroom, Classroom } from '../classroom.model';

import { ClassroomUpdateComponent } from './classroom-update.component';

describe('Classroom Management Update Component', () => {
  let comp: ClassroomUpdateComponent;
  let fixture: ComponentFixture<ClassroomUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let classroomService: ClassroomService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ClassroomUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(ClassroomUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClassroomUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    classroomService = TestBed.inject(ClassroomService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const classroom: IClassroom = { id: 456 };

      activatedRoute.data = of({ classroom });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(classroom));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Classroom>>();
      const classroom = { id: 123 };
      jest.spyOn(classroomService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classroom });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: classroom }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(classroomService.update).toHaveBeenCalledWith(classroom);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Classroom>>();
      const classroom = new Classroom();
      jest.spyOn(classroomService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classroom });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: classroom }));
      saveSubject.complete();

      // THEN
      expect(classroomService.create).toHaveBeenCalledWith(classroom);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Classroom>>();
      const classroom = { id: 123 };
      jest.spyOn(classroomService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ classroom });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(classroomService.update).toHaveBeenCalledWith(classroom);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
