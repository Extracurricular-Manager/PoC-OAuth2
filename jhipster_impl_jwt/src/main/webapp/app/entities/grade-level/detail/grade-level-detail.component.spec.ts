import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GradeLevelDetailComponent } from './grade-level-detail.component';

describe('GradeLevel Management Detail Component', () => {
  let comp: GradeLevelDetailComponent;
  let fixture: ComponentFixture<GradeLevelDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GradeLevelDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ gradeLevel: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(GradeLevelDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GradeLevelDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load gradeLevel on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.gradeLevel).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
