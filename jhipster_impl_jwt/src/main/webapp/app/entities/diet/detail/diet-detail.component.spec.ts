import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DietDetailComponent } from './diet-detail.component';

describe('Diet Management Detail Component', () => {
  let comp: DietDetailComponent;
  let fixture: ComponentFixture<DietDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DietDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ diet: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DietDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DietDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load diet on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.diet).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
