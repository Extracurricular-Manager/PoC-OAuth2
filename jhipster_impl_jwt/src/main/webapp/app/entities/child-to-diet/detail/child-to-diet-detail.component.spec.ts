import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ChildToDietDetailComponent } from './child-to-diet-detail.component';

describe('ChildToDiet Management Detail Component', () => {
  let comp: ChildToDietDetailComponent;
  let fixture: ComponentFixture<ChildToDietDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChildToDietDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ childToDiet: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ChildToDietDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ChildToDietDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load childToDiet on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.childToDiet).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
