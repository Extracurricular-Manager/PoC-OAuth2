import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDiet, Diet } from '../diet.model';

import { DietService } from './diet.service';

describe('Diet Service', () => {
  let service: DietService;
  let httpMock: HttpTestingController;
  let elemDefault: IDiet;
  let expectedResult: IDiet | IDiet[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DietService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      description: 'AAAAAAA',
      children: 0,
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

    it('should create a Diet', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Diet()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Diet', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
          children: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Diet', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
        },
        new Diet()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Diet', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
          children: 1,
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

    it('should delete a Diet', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDietToCollectionIfMissing', () => {
      it('should add a Diet to an empty array', () => {
        const diet: IDiet = { id: 123 };
        expectedResult = service.addDietToCollectionIfMissing([], diet);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(diet);
      });

      it('should not add a Diet to an array that contains it', () => {
        const diet: IDiet = { id: 123 };
        const dietCollection: IDiet[] = [
          {
            ...diet,
          },
          { id: 456 },
        ];
        expectedResult = service.addDietToCollectionIfMissing(dietCollection, diet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Diet to an array that doesn't contain it", () => {
        const diet: IDiet = { id: 123 };
        const dietCollection: IDiet[] = [{ id: 456 }];
        expectedResult = service.addDietToCollectionIfMissing(dietCollection, diet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(diet);
      });

      it('should add only unique Diet to an array', () => {
        const dietArray: IDiet[] = [{ id: 123 }, { id: 456 }, { id: 34542 }];
        const dietCollection: IDiet[] = [{ id: 123 }];
        expectedResult = service.addDietToCollectionIfMissing(dietCollection, ...dietArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const diet: IDiet = { id: 123 };
        const diet2: IDiet = { id: 456 };
        expectedResult = service.addDietToCollectionIfMissing([], diet, diet2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(diet);
        expect(expectedResult).toContain(diet2);
      });

      it('should accept null and undefined values', () => {
        const diet: IDiet = { id: 123 };
        expectedResult = service.addDietToCollectionIfMissing([], null, diet, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(diet);
      });

      it('should return initial array if no Diet is added', () => {
        const dietCollection: IDiet[] = [{ id: 123 }];
        expectedResult = service.addDietToCollectionIfMissing(dietCollection, undefined, null);
        expect(expectedResult).toEqual(dietCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
