import { IChild } from 'app/entities/child/child.model';

export interface IGradeLevel {
  id?: string;
  ids?: IChild[] | null;
}

export class GradeLevel implements IGradeLevel {
  constructor(public id?: string, public ids?: IChild[] | null) {}
}

export function getGradeLevelIdentifier(gradeLevel: IGradeLevel): string | undefined {
  return gradeLevel.id;
}
