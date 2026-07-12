export * from './login-request.dto';
export * from './login-response.dto';

export type AuthRequestDTO = import('./login-request.dto').LoginRequestDTO;
export type AuthResponseDTO = import('./login-response.dto').LoginResponseDTO;

