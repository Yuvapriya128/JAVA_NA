export interface LoginResponseUserDTO {
  token?: string;
  accessToken?: string;
  jwt?: string;
}

export interface LoginResponseDTO extends LoginResponseUserDTO {
}

export interface ErrorResponseDTO {
  message?: string;
  error?: string;
  fieldErrors?: Record<string, string>;
  violations?: Array<{ field: string; message: string }>;
  status?: number;
  timestamp?: string;
}
