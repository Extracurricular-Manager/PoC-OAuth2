import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGradeLevel, getGradeLevelIdentifier } from '../grade-level.model';

export type EntityResponseType = HttpResponse<IGradeLevel>;
export type EntityArrayResponseType = HttpResponse<IGradeLevel[]>;

@Injectable({ providedIn: 'root' })
export class GradeLevelService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/grade-levels');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(gradeLevel: IGradeLevel): Observable<EntityResponseType> {
    return this.http.post<IGradeLevel>(this.resourceUrl, gradeLevel, { observe: 'response' });
  }

  update(gradeLevel: IGradeLevel): Observable<EntityResponseType> {
    return this.http.put<IGradeLevel>(`${this.resourceUrl}/${getGradeLevelIdentifier(gradeLevel) as string}`, gradeLevel, {
      observe: 'response',
    });
  }

  partialUpdate(gradeLevel: IGradeLevel): Observable<EntityResponseType> {
    return this.http.patch<IGradeLevel>(`${this.resourceUrl}/${getGradeLevelIdentifier(gradeLevel) as string}`, gradeLevel, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IGradeLevel>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGradeLevel[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addGradeLevelToCollectionIfMissing(
    gradeLevelCollection: IGradeLevel[],
    ...gradeLevelsToCheck: (IGradeLevel | null | undefined)[]
  ): IGradeLevel[] {
    const gradeLevels: IGradeLevel[] = gradeLevelsToCheck.filter(isPresent);
    if (gradeLevels.length > 0) {
      const gradeLevelCollectionIdentifiers = gradeLevelCollection.map(gradeLevelItem => getGradeLevelIdentifier(gradeLevelItem)!);
      const gradeLevelsToAdd = gradeLevels.filter(gradeLevelItem => {
        const gradeLevelIdentifier = getGradeLevelIdentifier(gradeLevelItem);
        if (gradeLevelIdentifier == null || gradeLevelCollectionIdentifiers.includes(gradeLevelIdentifier)) {
          return false;
        }
        gradeLevelCollectionIdentifiers.push(gradeLevelIdentifier);
        return true;
      });
      return [...gradeLevelsToAdd, ...gradeLevelCollection];
    }
    return gradeLevelCollection;
  }
}
