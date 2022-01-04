import { IChild } from 'app/entities/child/child.model';

export interface IClassroom {
  id?: number;
  name?: string | null;
  professor?: string | null;
  ids?: IChild[] | null;
}

export class Classroom implements IClassroom {
  constructor(public id?: number, public name?: string | null, public professor?: string | null, public ids?: IChild[] | null) {}
}

export function getClassroomIdentifier(classroom: IClassroom): number | undefined {
  return classroom.id;
}
