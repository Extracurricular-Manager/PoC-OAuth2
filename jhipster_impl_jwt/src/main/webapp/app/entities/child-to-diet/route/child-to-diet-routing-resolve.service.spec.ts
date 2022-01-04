jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IChildToDiet, ChildToDiet } from '../child-to-diet.model';
import { ChildToDietService } from '../service/child-to-diet.service';

import { ChildToDietRoutingResolveService } from './child-to-diet-routing-resolve.service';

describe('ChildToDiet routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ChildToDietRoutingResolveService;
  let service: ChildToDietService;
  let resultChildToDiet: IChildToDiet | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(ChildToDietRoutingResolveService);
    service = TestBed.inject(ChildToDietService);
    resultChildToDiet = undefined;
  });

  describe('resolve', () => {
    it('should return IChildToDiet returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultChildToDiet = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultChildToDiet).toEqual({ id: 123 });
    });

    it('should return new IChildToDiet if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultChildToDiet = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultChildToDiet).toEqual(new ChildToDiet());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as ChildToDiet })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultChildToDiet = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultChildToDiet).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
