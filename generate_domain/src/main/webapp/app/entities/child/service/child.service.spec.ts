import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IChild, Child } from '../child.model';

import { ChildService } from './child.service';

describe('Child Service', () => {
  let service: ChildService;
  let httpMock: HttpTestingController;
  let elemDefault: IChild;
  let expectedResult: IChild | IChild[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ChildService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      surname: 'AAAAAAA',
      birthday: currentDate,
      gradeLevel: 'AAAAAAA',
      classroom: 0,
      adelphie: 0,
      diet: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          birthday: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Child', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          birthday: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          birthday: currentDate,
        },
        returnedFromService
      );

      service.create(new Child()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Child', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          surname: 'BBBBBB',
          birthday: currentDate.format(DATE_TIME_FORMAT),
          gradeLevel: 'BBBBBB',
          classroom: 1,
          adelphie: 1,
          diet: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          birthday: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Child', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          surname: 'BBBBBB',
          gradeLevel: 'BBBBBB',
          diet: 1,
        },
        new Child()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          birthday: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Child', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          surname: 'BBBBBB',
          birthday: currentDate.format(DATE_TIME_FORMAT),
          gradeLevel: 'BBBBBB',
          classroom: 1,
          adelphie: 1,
          diet: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          birthday: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Child', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addChildToCollectionIfMissing', () => {
      it('should add a Child to an empty array', () => {
        const child: IChild = { id: 123 };
        expectedResult = service.addChildToCollectionIfMissing([], child);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(child);
      });

      it('should not add a Child to an array that contains it', () => {
        const child: IChild = { id: 123 };
        const childCollection: IChild[] = [
          {
            ...child,
          },
          { id: 456 },
        ];
        expectedResult = service.addChildToCollectionIfMissing(childCollection, child);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Child to an array that doesn't contain it", () => {
        const child: IChild = { id: 123 };
        const childCollection: IChild[] = [{ id: 456 }];
        expectedResult = service.addChildToCollectionIfMissing(childCollection, child);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(child);
      });

      it('should add only unique Child to an array', () => {
        const childArray: IChild[] = [{ id: 123 }, { id: 456 }, { id: 43503 }];
        const childCollection: IChild[] = [{ id: 123 }];
        expectedResult = service.addChildToCollectionIfMissing(childCollection, ...childArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const child: IChild = { id: 123 };
        const child2: IChild = { id: 456 };
        expectedResult = service.addChildToCollectionIfMissing([], child, child2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(child);
        expect(expectedResult).toContain(child2);
      });

      it('should accept null and undefined values', () => {
        const child: IChild = { id: 123 };
        expectedResult = service.addChildToCollectionIfMissing([], null, child, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(child);
      });

      it('should return initial array if no Child is added', () => {
        const childCollection: IChild[] = [{ id: 123 }];
        expectedResult = service.addChildToCollectionIfMissing(childCollection, undefined, null);
        expect(expectedResult).toEqual(childCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
