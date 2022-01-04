export interface IFamily {
  id?: number;
  referingParentName?: string | null;
  referingParentSurname?: string | null;
  telephoneNumber?: string | null;
  postalAdress?: string | null;
}

export class Family implements IFamily {
  constructor(
    public id?: number,
    public referingParentName?: string | null,
    public referingParentSurname?: string | null,
    public telephoneNumber?: string | null,
    public postalAdress?: string | null
  ) {}
}

export function getFamilyIdentifier(family: IFamily): number | undefined {
  return family.id;
}
