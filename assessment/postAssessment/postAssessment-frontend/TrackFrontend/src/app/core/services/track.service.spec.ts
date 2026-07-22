import { TestBed } from '@angular/core/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TrackService } from './track.service';
import { Track, TrackRequest } from '../models/track.model';
import { environment } from '../../../environments/environment';

describe('TrackService', () => {
  let service: TrackService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiBaseUrl}/tracks`;

  const sampleTrack: Track = {
    id: 1,
    title: 'Test Song',
    albumName: 'Test Album',
    releaseDate: '2024-01-01',
    playCount: 10,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
        TrackService,
      ],
    });
    service = TestBed.inject(TrackService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAll() should GET the tracks collection', () => {
    service.getAll().subscribe((tracks) => {
      expect(tracks).toEqual([sampleTrack]);
    });
    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush([sampleTrack]);
  });

  it('searchByTitle() should GET /search with title param', () => {
    service.searchByTitle('Test Song').subscribe((track) => {
      expect(track).toEqual(sampleTrack);
    });
    const req = httpMock.expectOne(
      (r) => r.url === `${baseUrl}/search` && r.params.get('title') === 'Test Song',
    );
    expect(req.request.method).toBe('GET');
    req.flush(sampleTrack);
  });

  it('create() should POST the payload', () => {
    const payload: TrackRequest = {
      title: 'Test Song',
      albumName: 'Test Album',
      releaseDate: '2024-01-01',
      playCount: 10,
    };
    service.create(payload).subscribe((track) => {
      expect(track).toEqual(sampleTrack);
    });
    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush(sampleTrack);
  });

  it('delete() should DELETE the track by id', () => {
    service.delete(1).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
