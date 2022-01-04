import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { DietService } from '../service/diet.service';

import { DietComponent } from './diet.component';

describe('Diet Management Component', () => {
  let comp: DietComponent;
  let fixture: ComponentFixture<DietComponent>;
  let service: DietService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [DietComponent],
    })
      .overrideTemplate(DietComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DietComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DietService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
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
    expect(comp.diets?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
