export interface IClassroom {
  id?: number;
  name?: string | null;
  professor?: string | null;
}

export class Classroom implements IClassroom {
  constructor(public id?: number, public name?: string | null, public professor?: string | null) {}
}

export function getClassroomIdentifier(classroom: IClassroom): number | undefined {
  return classroom.id;
}
