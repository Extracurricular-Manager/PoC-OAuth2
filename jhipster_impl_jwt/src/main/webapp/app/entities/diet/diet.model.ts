import { IChildToDiet } from 'app/entities/child-to-diet/child-to-diet.model';

export interface IDiet {
  id?: number;
  name?: string | null;
  description?: string | null;
  ids?: IChildToDiet[] | null;
}

export class Diet implements IDiet {
  constructor(public id?: number, public name?: string | null, public description?: string | null, public ids?: IChildToDiet[] | null) {}
}

export function getDietIdentifier(diet: IDiet): number | undefined {
  return diet.id;
}
