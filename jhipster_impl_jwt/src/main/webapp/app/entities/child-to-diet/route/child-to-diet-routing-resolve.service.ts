import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IChildToDiet, ChildToDiet } from '../child-to-diet.model';
import { ChildToDietService } from '../service/child-to-diet.service';

@Injectable({ providedIn: 'root' })
export class ChildToDietRoutingResolveService implements Resolve<IChildToDiet> {
  constructor(protected service: ChildToDietService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IChildToDiet> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((childToDiet: HttpResponse<ChildToDiet>) => {
          if (childToDiet.body) {
            return of(childToDiet.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ChildToDiet());
  }
}
