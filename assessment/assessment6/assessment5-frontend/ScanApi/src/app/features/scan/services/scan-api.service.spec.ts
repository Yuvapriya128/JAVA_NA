import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ScanApiService } from './scan-api.service';
import { CreateScanRequestDto, ScanDto } from './scan.dto';

describe('ScanApiService', () => {
  let service: ScanApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(ScanApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch all scans', () => {
    const mockScans: ScanDto[] = [
      {
        id: 1,
        domainName: 'example.com',
        numPages: 10,
        numBrokenLinks: 2,
        numMissingImages: 1,
        deleted: false,
      },
    ];

    service.getScans().subscribe((data) => {
      expect(data.length).toBe(1);
      expect(data[0].domainName).toBe('example.com');
    });

    const request = httpMock.expectOne('http://localhost:9090/scan');
    expect(request.request.method).toBe('GET');
    request.flush(mockScans);
  });

  it('should create a scan', () => {
    const payload: CreateScanRequestDto = {
      domainName: 'test.com',
      numPages: 5,
      numBrokenLinks: 0,
      numMissingImages: 0,
    };

    service.createScan(payload).subscribe((response) => {
      expect(response.id).toBe(9);
      expect(response.domainName).toBe('test.com');
    });

    const request = httpMock.expectOne('http://localhost:9090/scan');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);

    request.flush({
      id: 9,
      ...payload,
      deleted: false,
    });
  });

  it('should call search with orderBy query parameter', () => {
    service.searchScans('hackerrank.com', 'numPages').subscribe();

    const request = httpMock.expectOne(
      'http://localhost:9090/scan/search/hackerrank.com?orderBy=numPages'
    );
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });
});
