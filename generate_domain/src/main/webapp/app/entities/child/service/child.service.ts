import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IChild, getChildIdentifier } from '../child.model';

export type EntityResponseType = HttpResponse<IChild>;
export type EntityArrayResponseType = HttpResponse<IChild[]>;

@Injectable({ providedIn: 'root' })
export class ChildService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/children');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(child: IChild): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(child);
    return this.http
      .post<IChild>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(child: IChild): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(child);
    return this.http
      .put<IChild>(`${this.resourceUrl}/${getChildIdentifier(child) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(child: IChild): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(child);
    return this.http
      .patch<IChild>(`${this.resourceUrl}/${getChildIdentifier(child) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IChild>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IChild[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addChildToCollectionIfMissing(childCollection: IChild[], ...childrenToCheck: (IChild | null | undefined)[]): IChild[] {
    const children: IChild[] = childrenToCheck.filter(isPresent);
    if (children.length > 0) {
      const childCollectionIdentifiers = childCollection.map(childItem => getChildIdentifier(childItem)!);
      const childrenToAdd = children.filter(childItem => {
        const childIdentifier = getChildIdentifier(childItem);
        if (childIdentifier == null || childCollectionIdentifiers.includes(childIdentifier)) {
          return false;
        }
        childCollectionIdentifiers.push(childIdentifier);
        return true;
      });
      return [...childrenToAdd, ...childCollection];
    }
    return childCollection;
  }

  protected convertDateFromClient(child: IChild): IChild {
    return Object.assign({}, child, {
      birthday: child.birthday?.isValid() ? child.birthday.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.birthday = res.body.birthday ? dayjs(res.body.birthday) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((child: IChild) => {
        child.birthday = child.birthday ? dayjs(child.birthday) : undefined;
      });
    }
    return res;
  }
}
