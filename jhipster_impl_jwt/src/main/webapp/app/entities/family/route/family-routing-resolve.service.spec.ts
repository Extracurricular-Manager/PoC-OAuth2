jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IFamily, Family } from '../family.model';
import { FamilyService } from '../service/family.service';

import { FamilyRoutingResolveService } from './family-routing-resolve.service';

describe('Family routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: FamilyRoutingResolveService;
  let service: FamilyService;
  let resultFamily: IFamily | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(FamilyRoutingResolveService);
    service = TestBed.inject(FamilyService);
    resultFamily = undefined;
  });

  describe('resolve', () => {
    it('should return IFamily returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultFamily = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultFamily).toEqual({ id: 123 });
    });

    it('should return new IFamily if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultFamily = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultFamily).toEqual(new Family());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Family })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultFamily = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultFamily).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
