import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGradeLevel, GradeLevel } from '../grade-level.model';

import { GradeLevelService } from './grade-level.service';

describe('GradeLevel Service', () => {
  let service: GradeLevelService;
  let httpMock: HttpTestingController;
  let elemDefault: IGradeLevel;
  let expectedResult: IGradeLevel | IGradeLevel[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GradeLevelService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a GradeLevel', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new GradeLevel()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GradeLevel', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GradeLevel', () => {
      const patchObject = Object.assign({}, new GradeLevel());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GradeLevel', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
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

    it('should delete a GradeLevel', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGradeLevelToCollectionIfMissing', () => {
      it('should add a GradeLevel to an empty array', () => {
        const gradeLevel: IGradeLevel = { id: 'ABC' };
        expectedResult = service.addGradeLevelToCollectionIfMissing([], gradeLevel);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gradeLevel);
      });

      it('should not add a GradeLevel to an array that contains it', () => {
        const gradeLevel: IGradeLevel = { id: 'ABC' };
        const gradeLevelCollection: IGradeLevel[] = [
          {
            ...gradeLevel,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addGradeLevelToCollectionIfMissing(gradeLevelCollection, gradeLevel);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GradeLevel to an array that doesn't contain it", () => {
        const gradeLevel: IGradeLevel = { id: 'ABC' };
        const gradeLevelCollection: IGradeLevel[] = [{ id: 'CBA' }];
        expectedResult = service.addGradeLevelToCollectionIfMissing(gradeLevelCollection, gradeLevel);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gradeLevel);
      });

      it('should add only unique GradeLevel to an array', () => {
        const gradeLevelArray: IGradeLevel[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '33167c5c-b422-4ead-b8a9-d345a6c409ba' }];
        const gradeLevelCollection: IGradeLevel[] = [{ id: 'ABC' }];
        expectedResult = service.addGradeLevelToCollectionIfMissing(gradeLevelCollection, ...gradeLevelArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const gradeLevel: IGradeLevel = { id: 'ABC' };
        const gradeLevel2: IGradeLevel = { id: 'CBA' };
        expectedResult = service.addGradeLevelToCollectionIfMissing([], gradeLevel, gradeLevel2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gradeLevel);
        expect(expectedResult).toContain(gradeLevel2);
      });

      it('should accept null and undefined values', () => {
        const gradeLevel: IGradeLevel = { id: 'ABC' };
        expectedResult = service.addGradeLevelToCollectionIfMissing([], null, gradeLevel, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gradeLevel);
      });

      it('should return initial array if no GradeLevel is added', () => {
        const gradeLevelCollection: IGradeLevel[] = [{ id: 'ABC' }];
        expectedResult = service.addGradeLevelToCollectionIfMissing(gradeLevelCollection, undefined, null);
        expect(expectedResult).toEqual(gradeLevelCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
