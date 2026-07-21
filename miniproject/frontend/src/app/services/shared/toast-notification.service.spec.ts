import { TestBed } from '@angular/core/testing';
import { ToastNotificationService } from './toast-notification.service';

describe('ToastNotificationService', () => {
  let service: ToastNotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ToastNotificationService);
  });

  it('should enqueue toast message', () => {
    service.show('hello', 'success', 0);
    expect(service.messages().length).toBe(1);
    expect(service.messages()[0].message).toBe('hello');
  });

  it('should dismiss toast message', () => {
    service.show('hello', 'info', 0);
    const id = service.messages()[0].id;
    service.dismiss(id);
    expect(service.messages().length).toBe(0);
  });
});
