import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ChildToDietService } from '../service/child-to-diet.service';

import { ChildToDietComponent } from './child-to-diet.component';

describe('ChildToDiet Management Component', () => {
  let comp: ChildToDietComponent;
  let fixture: ComponentFixture<ChildToDietComponent>;
  let service: ChildToDietService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ChildToDietComponent],
    })
      .overrideTemplate(ChildToDietComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ChildToDietComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ChildToDietService);

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
    expect(comp.childToDiets?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
