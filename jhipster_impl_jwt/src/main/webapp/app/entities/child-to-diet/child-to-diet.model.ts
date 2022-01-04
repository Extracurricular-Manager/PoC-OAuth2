import { IChild } from 'app/entities/child/child.model';
import { IDiet } from 'app/entities/diet/diet.model';

export interface IChildToDiet {
  id?: number;
  idChild?: number | null;
  idDiet?: number | null;
  idChildren?: IChild[] | null;
  idDiets?: IDiet[] | null;
}

export class ChildToDiet implements IChildToDiet {
  constructor(
    public id?: number,
    public idChild?: number | null,
    public idDiet?: number | null,
    public idChildren?: IChild[] | null,
    public idDiets?: IDiet[] | null
  ) {}
}

export function getChildToDietIdentifier(childToDiet: IChildToDiet): number | undefined {
  return childToDiet.id;
}
