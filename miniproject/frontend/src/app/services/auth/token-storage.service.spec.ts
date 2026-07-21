import { TestBed } from '@angular/core/testing';
import { TokenStorageService } from './token-storage.service';

describe('TokenStorageService', () => {
  let service: TokenStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TokenStorageService);
    localStorage.clear();
  });

  it('should save and read token', () => {
    service.saveToken('abc.def.ghi');
    expect(service.getToken()).toBe('abc.def.ghi');
  });

  it('should clear token on remove', () => {
    service.saveToken('abc.def.ghi');
    service.removeToken();
    expect(service.getToken()).toBeNull();
  });

  it('should return false when no token exists', () => {
    expect(service.isLoggedIn()).toBeFalsy();
  });
});
