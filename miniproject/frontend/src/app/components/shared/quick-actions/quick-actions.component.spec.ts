import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { vi } from 'vitest';

import { QuickActionsComponent } from './quick-actions.component';
import { TokenStorageService } from '../../../services/auth/token-storage.service';

describe('QuickActionsComponent', () => {
  let component: QuickActionsComponent;
  let fixture: ComponentFixture<QuickActionsComponent>;
  let router: Router;

  beforeEach(async () => {
    localStorage.clear();

    await TestBed.configureTestingModule({
      imports: [QuickActionsComponent],
      providers: [
        provideRouter([]),
        {
          provide: TokenStorageService,
          useValue: {
            getPrimaryRole: () => 'USER'
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(QuickActionsComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should hide admin-only commands for USER role', () => {
    const titles = component.visibleCommands().map((command) => command.title);
    expect(titles).toContain('Dashboard');
    expect(titles).not.toContain('Create Customer');
    expect(titles).not.toContain('Interest Management');
  });

  it('should execute selected command and navigate', () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    const command = component.visibleCommands().find((item) => item.title === 'Profile');
    expect(command).toBeTruthy();
    if (!command) {
      return;
    }

    component.executeCommand(command);

    expect(navigateSpy).toHaveBeenCalledWith(['/profile']);
  });
});
