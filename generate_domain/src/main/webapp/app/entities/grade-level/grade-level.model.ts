export interface IGradeLevel {
  id?: string;
}

export class GradeLevel implements IGradeLevel {
  constructor(public id?: string) {}
}

export function getGradeLevelIdentifier(gradeLevel: IGradeLevel): string | undefined {
  return gradeLevel.id;
}
