import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';
import { ScanApiService } from '../../services/scan-api.service';
import { ScanListComponent } from './scan-list.component';

describe('ScanListComponent', () => {
  let component: ScanListComponent;
  let fixture: ComponentFixture<ScanListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScanListComponent],
      providers: [
        provideRouter([]),
        {
          provide: ScanApiService,
          useValue: {
            getScans: () =>
              of([
                {
                  id: 1,
                  domainName: 'example.com',
                  numPages: 10,
                  numBrokenLinks: 2,
                  numMissingImages: 1,
                  deleted: false,
                },
              ]),
            deleteScan: () => of(void 0),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ScanListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render scan row data', () => {
    const element = fixture.nativeElement as HTMLElement;
    expect(element.textContent).toContain('example.com');
    expect(element.textContent).toContain('10');
  });
});
