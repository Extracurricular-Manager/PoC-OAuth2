import { IChild } from 'app/entities/child/child.model';

export interface IDiet {
  id?: number;
  name?: string | null;
  description?: string | null;
  children?: number | null;
  children?: IChild[] | null;
}

export class Diet implements IDiet {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public children?: number | null,
    public children?: IChild[] | null
  ) {}
}

export function getDietIdentifier(diet: IDiet): number | undefined {
  return diet.id;
}
