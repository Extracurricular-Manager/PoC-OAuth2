import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDiet, Diet } from '../diet.model';
import { DietService } from '../service/diet.service';

@Injectable({ providedIn: 'root' })
export class DietRoutingResolveService implements Resolve<IDiet> {
  constructor(protected service: DietService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDiet> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((diet: HttpResponse<Diet>) => {
          if (diet.body) {
            return of(diet.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Diet());
  }
}
