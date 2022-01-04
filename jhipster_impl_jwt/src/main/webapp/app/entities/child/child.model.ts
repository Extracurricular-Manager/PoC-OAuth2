import * as dayjs from 'dayjs';
import { IClassroom } from 'app/entities/classroom/classroom.model';
import { IFamily } from 'app/entities/family/family.model';
import { IGradeLevel } from 'app/entities/grade-level/grade-level.model';
import { IChildToDiet } from 'app/entities/child-to-diet/child-to-diet.model';

export interface IChild {
  id?: number;
  name?: string | null;
  surname?: string | null;
  birthday?: dayjs.Dayjs | null;
  gradeLevel?: string | null;
  classroom?: number | null;
  adelphie?: number | null;
  classroom?: IClassroom | null;
  adelphie?: IFamily | null;
  gradeLevel?: IGradeLevel | null;
  ids?: IChildToDiet[] | null;
}

export class Child implements IChild {
  constructor(
    public id?: number,
    public name?: string | null,
    public surname?: string | null,
    public birthday?: dayjs.Dayjs | null,
    public gradeLevel?: string | null,
    public classroom?: number | null,
    public adelphie?: number | null,
    public classroom?: IClassroom | null,
    public adelphie?: IFamily | null,
    public gradeLevel?: IGradeLevel | null,
    public ids?: IChildToDiet[] | null
  ) {}
}

export function getChildIdentifier(child: IChild): number | undefined {
  return child.id;
}
