import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGradeLevel, GradeLevel } from '../grade-level.model';
import { GradeLevelService } from '../service/grade-level.service';

@Injectable({ providedIn: 'root' })
export class GradeLevelRoutingResolveService implements Resolve<IGradeLevel> {
  constructor(protected service: GradeLevelService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGradeLevel> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((gradeLevel: HttpResponse<GradeLevel>) => {
          if (gradeLevel.body) {
            return of(gradeLevel.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new GradeLevel());
  }
}
