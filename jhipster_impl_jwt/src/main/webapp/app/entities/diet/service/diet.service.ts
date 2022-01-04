import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDiet, getDietIdentifier } from '../diet.model';

export type EntityResponseType = HttpResponse<IDiet>;
export type EntityArrayResponseType = HttpResponse<IDiet[]>;

@Injectable({ providedIn: 'root' })
export class DietService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/diets');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(diet: IDiet): Observable<EntityResponseType> {
    return this.http.post<IDiet>(this.resourceUrl, diet, { observe: 'response' });
  }

  update(diet: IDiet): Observable<EntityResponseType> {
    return this.http.put<IDiet>(`${this.resourceUrl}/${getDietIdentifier(diet) as number}`, diet, { observe: 'response' });
  }

  partialUpdate(diet: IDiet): Observable<EntityResponseType> {
    return this.http.patch<IDiet>(`${this.resourceUrl}/${getDietIdentifier(diet) as number}`, diet, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDiet>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDiet[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDietToCollectionIfMissing(dietCollection: IDiet[], ...dietsToCheck: (IDiet | null | undefined)[]): IDiet[] {
    const diets: IDiet[] = dietsToCheck.filter(isPresent);
    if (diets.length > 0) {
      const dietCollectionIdentifiers = dietCollection.map(dietItem => getDietIdentifier(dietItem)!);
      const dietsToAdd = diets.filter(dietItem => {
        const dietIdentifier = getDietIdentifier(dietItem);
        if (dietIdentifier == null || dietCollectionIdentifiers.includes(dietIdentifier)) {
          return false;
        }
        dietCollectionIdentifiers.push(dietIdentifier);
        return true;
      });
      return [...dietsToAdd, ...dietCollection];
    }
    return dietCollection;
  }
}
