jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IGradeLevel, GradeLevel } from '../grade-level.model';
import { GradeLevelService } from '../service/grade-level.service';

import { GradeLevelRoutingResolveService } from './grade-level-routing-resolve.service';

describe('GradeLevel routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: GradeLevelRoutingResolveService;
  let service: GradeLevelService;
  let resultGradeLevel: IGradeLevel | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(GradeLevelRoutingResolveService);
    service = TestBed.inject(GradeLevelService);
    resultGradeLevel = undefined;
  });

  describe('resolve', () => {
    it('should return IGradeLevel returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGradeLevel = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultGradeLevel).toEqual({ id: 'ABC' });
    });

    it('should return new IGradeLevel if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGradeLevel = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultGradeLevel).toEqual(new GradeLevel());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as GradeLevel })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGradeLevel = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultGradeLevel).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
