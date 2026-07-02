export interface DivisionResponseDTO {
  id: number;
  name: string;
  code: string;
  active: boolean;
  countryId: number;
}

export interface DivisionRequestDTO {
  id?: number; 
  name: string;
  code: string;
  active: boolean;
  countryId: number;
}