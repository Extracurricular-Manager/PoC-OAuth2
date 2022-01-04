import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ChildDetailComponent } from './child-detail.component';

describe('Child Management Detail Component', () => {
  let comp: ChildDetailComponent;
  let fixture: ComponentFixture<ChildDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChildDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ child: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ChildDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ChildDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load child on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.child).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
