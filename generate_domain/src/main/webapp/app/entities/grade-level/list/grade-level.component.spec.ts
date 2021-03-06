import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { GradeLevelService } from '../service/grade-level.service';

import { GradeLevelComponent } from './grade-level.component';

describe('GradeLevel Management Component', () => {
  let comp: GradeLevelComponent;
  let fixture: ComponentFixture<GradeLevelComponent>;
  let service: GradeLevelService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [GradeLevelComponent],
    })
      .overrideTemplate(GradeLevelComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GradeLevelComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(GradeLevelService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 'ABC' }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.gradeLevels?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
