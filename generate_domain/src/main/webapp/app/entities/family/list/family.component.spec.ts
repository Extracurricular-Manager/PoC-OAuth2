import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { FamilyService } from '../service/family.service';

import { FamilyComponent } from './family.component';

describe('Family Management Component', () => {
  let comp: FamilyComponent;
  let fixture: ComponentFixture<FamilyComponent>;
  let service: FamilyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [FamilyComponent],
    })
      .overrideTemplate(FamilyComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FamilyComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FamilyService);

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
    expect(comp.families?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
