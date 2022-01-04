import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IChildToDiet, ChildToDiet } from '../child-to-diet.model';

import { ChildToDietService } from './child-to-diet.service';

describe('ChildToDiet Service', () => {
  let service: ChildToDietService;
  let httpMock: HttpTestingController;
  let elemDefault: IChildToDiet;
  let expectedResult: IChildToDiet | IChildToDiet[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ChildToDietService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      idChild: 0,
      idDiet: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a ChildToDiet', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new ChildToDiet()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ChildToDiet', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idChild: 1,
          idDiet: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ChildToDiet', () => {
      const patchObject = Object.assign({}, new ChildToDiet());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ChildToDiet', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idChild: 1,
          idDiet: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a ChildToDiet', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addChildToDietToCollectionIfMissing', () => {
      it('should add a ChildToDiet to an empty array', () => {
        const childToDiet: IChildToDiet = { id: 123 };
        expectedResult = service.addChildToDietToCollectionIfMissing([], childToDiet);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(childToDiet);
      });

      it('should not add a ChildToDiet to an array that contains it', () => {
        const childToDiet: IChildToDiet = { id: 123 };
        const childToDietCollection: IChildToDiet[] = [
          {
            ...childToDiet,
          },
          { id: 456 },
        ];
        expectedResult = service.addChildToDietToCollectionIfMissing(childToDietCollection, childToDiet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ChildToDiet to an array that doesn't contain it", () => {
        const childToDiet: IChildToDiet = { id: 123 };
        const childToDietCollection: IChildToDiet[] = [{ id: 456 }];
        expectedResult = service.addChildToDietToCollectionIfMissing(childToDietCollection, childToDiet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(childToDiet);
      });

      it('should add only unique ChildToDiet to an array', () => {
        const childToDietArray: IChildToDiet[] = [{ id: 123 }, { id: 456 }, { id: 2344 }];
        const childToDietCollection: IChildToDiet[] = [{ id: 123 }];
        expectedResult = service.addChildToDietToCollectionIfMissing(childToDietCollection, ...childToDietArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const childToDiet: IChildToDiet = { id: 123 };
        const childToDiet2: IChildToDiet = { id: 456 };
        expectedResult = service.addChildToDietToCollectionIfMissing([], childToDiet, childToDiet2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(childToDiet);
        expect(expectedResult).toContain(childToDiet2);
      });

      it('should accept null and undefined values', () => {
        const childToDiet: IChildToDiet = { id: 123 };
        expectedResult = service.addChildToDietToCollectionIfMissing([], null, childToDiet, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(childToDiet);
      });

      it('should return initial array if no ChildToDiet is added', () => {
        const childToDietCollection: IChildToDiet[] = [{ id: 123 }];
        expectedResult = service.addChildToDietToCollectionIfMissing(childToDietCollection, undefined, null);
        expect(expectedResult).toEqual(childToDietCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
