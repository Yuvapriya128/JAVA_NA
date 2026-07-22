import { TestBed } from '@angular/core/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TrackList } from './track-list';
import { environment } from '../../../../environments/environment';

describe('TrackList', () => {
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiBaseUrl}/tracks`;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackList],
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should create and load tracks on init', async () => {
    const fixture = TestBed.createComponent(TrackList);
    fixture.detectChanges();

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush([]);

    expect(fixture.componentInstance).toBeTruthy();
    httpMock.verify();
  });

  it('should show empty-state when there are no tracks', async () => {
    const fixture = TestBed.createComponent(TrackList);
    fixture.detectChanges();

    httpMock.expectOne(baseUrl).flush([]);
    await fixture.whenStable();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.empty-state')).toBeTruthy();
    httpMock.verify();
  });
});
