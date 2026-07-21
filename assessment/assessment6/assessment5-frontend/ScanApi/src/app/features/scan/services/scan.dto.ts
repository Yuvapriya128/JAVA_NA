export interface ScanDto {
  id: number;
  domainName: string;
  numPages: number;
  numBrokenLinks: number;
  numMissingImages: number;
  deleted: boolean;
}

export interface CreateScanRequestDto {
  domainName: string;
  numPages: number;
  numBrokenLinks: number;
  numMissingImages: number;
}

export interface ApiMessage {
  message: string;
}

export type ScanOrderBy =
  | 'id'
  | 'domainName'
  | 'numPages'
  | 'numBrokenLinks'
  | 'numMissingImages'
  | 'deleted';
