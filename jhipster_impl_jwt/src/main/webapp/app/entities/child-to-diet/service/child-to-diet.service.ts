import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IChildToDiet, getChildToDietIdentifier } from '../child-to-diet.model';

export type EntityResponseType = HttpResponse<IChildToDiet>;
export type EntityArrayResponseType = HttpResponse<IChildToDiet[]>;

@Injectable({ providedIn: 'root' })
export class ChildToDietService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/child-to-diets');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(childToDiet: IChildToDiet): Observable<EntityResponseType> {
    return this.http.post<IChildToDiet>(this.resourceUrl, childToDiet, { observe: 'response' });
  }

  update(childToDiet: IChildToDiet): Observable<EntityResponseType> {
    return this.http.put<IChildToDiet>(`${this.resourceUrl}/${getChildToDietIdentifier(childToDiet) as number}`, childToDiet, {
      observe: 'response',
    });
  }

  partialUpdate(childToDiet: IChildToDiet): Observable<EntityResponseType> {
    return this.http.patch<IChildToDiet>(`${this.resourceUrl}/${getChildToDietIdentifier(childToDiet) as number}`, childToDiet, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IChildToDiet>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IChildToDiet[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addChildToDietToCollectionIfMissing(
    childToDietCollection: IChildToDiet[],
    ...childToDietsToCheck: (IChildToDiet | null | undefined)[]
  ): IChildToDiet[] {
    const childToDiets: IChildToDiet[] = childToDietsToCheck.filter(isPresent);
    if (childToDiets.length > 0) {
      const childToDietCollectionIdentifiers = childToDietCollection.map(childToDietItem => getChildToDietIdentifier(childToDietItem)!);
      const childToDietsToAdd = childToDiets.filter(childToDietItem => {
        const childToDietIdentifier = getChildToDietIdentifier(childToDietItem);
        if (childToDietIdentifier == null || childToDietCollectionIdentifiers.includes(childToDietIdentifier)) {
          return false;
        }
        childToDietCollectionIdentifiers.push(childToDietIdentifier);
        return true;
      });
      return [...childToDietsToAdd, ...childToDietCollection];
    }
    return childToDietCollection;
  }
}
