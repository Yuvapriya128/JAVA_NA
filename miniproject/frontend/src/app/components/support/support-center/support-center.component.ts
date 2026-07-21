import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoanService } from '../../../services/loan/loan.service';
import { AuthService } from '../../../services/auth/auth.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';

type TicketStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED';

interface SupportTicket {
  id: string;
  subject: string;
  category: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  status: TicketStatus;
  createdAt: string;
  description: string;
}

@Component({
  selector: 'app-support-center',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PageHeaderComponent],
  templateUrl: './support-center.component.html',
  styleUrls: ['./support-center.component.css']
})
export class SupportCenterComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly loanService = inject(LoanService);
  private readonly authService = inject(AuthService);
  private readonly tokenStorage = inject(TokenStorageService);

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly tickets = signal<SupportTicket[]>([]);
  readonly role = signal<'USER' | 'MANAGER' | 'ADMIN'>(this.resolveRole());
  readonly activeLoans = signal(0);
  readonly pendingApplications = signal(0);
  readonly recentPayments = signal(0);
  readonly selectedFilter = signal<'ALL' | TicketStatus>('ALL');
  readonly successMessage = signal('');

  readonly ticketForm = this.fb.group({
    subject: ['', [Validators.required, Validators.minLength(5)]],
    category: ['PAYMENT', [Validators.required]],
    priority: ['MEDIUM', [Validators.required]],
    description: ['', [Validators.required, Validators.minLength(10)]]
  });

  readonly faqItems = [
    { title: 'EMI payment failed but amount debited', answer: 'Wait for reconciliation. If status does not update in 30 minutes, raise a Payment ticket with reference number.' },
    { title: 'Need foreclosure statement', answer: 'Go to Loans > Foreclosure, generate quote and submit support request if quote differs from expected values.' },
    { title: 'Application stuck in review', answer: 'Track status in Applications timeline. Manager/Admin can update review stage from Approval Center.' }
  ];

  ngOnInit(): void {
    this.seedTickets();
    this.loadContextSummary();
  }

  submitTicket(): void {
    if (this.ticketForm.invalid) {
      this.ticketForm.markAllAsTouched();
      return;
    }

    const value = this.ticketForm.getRawValue();
    const now = new Date();
    const newTicket: SupportTicket = {
      id: `SUP-${now.getTime().toString(36).toUpperCase()}`,
      subject: value.subject || '',
      category: value.category || 'GENERAL',
      priority: (value.priority as 'LOW' | 'MEDIUM' | 'HIGH') || 'MEDIUM',
      status: 'OPEN',
      createdAt: now.toLocaleString('en-IN'),
      description: value.description || ''
    };
    this.tickets.set([newTicket, ...this.tickets()]);
    this.successMessage.set(`Support ticket ${newTicket.id} created successfully.`);
    this.ticketForm.reset({
      subject: '',
      category: 'PAYMENT',
      priority: 'MEDIUM',
      description: ''
    });
  }

  filteredTickets(): SupportTicket[] {
    const filter = this.selectedFilter();
    if (filter === 'ALL') {
      return this.tickets();
    }
    return this.tickets().filter((item) => item.status === filter);
  }

  setFilter(value: 'ALL' | TicketStatus): void {
    this.selectedFilter.set(value);
  }

  statusClass(status: TicketStatus): string {
    if (status === 'RESOLVED') {
      return 'success';
    }
    if (status === 'IN_PROGRESS') {
      return 'warning';
    }
    return 'primary';
  }

  private seedTickets(): void {
    this.tickets.set([
      {
        id: 'SUP-EMI-901',
        subject: 'EMI receipt not visible after payment',
        category: 'PAYMENT',
        priority: 'MEDIUM',
        status: 'IN_PROGRESS',
        createdAt: '15 Jul 2026, 10:15 AM',
        description: 'Payment was successful but receipt is missing from history.'
      },
      {
        id: 'SUP-APP-442',
        subject: 'Application timeline not updating',
        category: 'APPLICATION',
        priority: 'LOW',
        status: 'OPEN',
        createdAt: '14 Jul 2026, 06:40 PM',
        description: 'Application still shows submitted even after manager review.'
      }
    ]);
  }

  private loadContextSummary(): void {
    this.status.set({ loading: true, success: false, error: '' });
    if (this.role() === 'USER') {
      this.loadUserContext();
      return;
    }

    this.loanService.getDashboard().subscribe({
      next: (dashboard) => {
        this.activeLoans.set(Number(dashboard.activeLoans) || 0);
        this.pendingApplications.set(Number(dashboard.overdueEMIs) || 0);
        this.recentPayments.set(Math.round((Number(dashboard.totalEMICollected) || 0) / 100000));
        this.status.set({ loading: false, success: false, error: '' });
      },
      error: () => {
        this.status.set({ loading: false, success: false, error: '' });
      }
    });
  }

  private loadUserContext(): void {
    const cached = this.authService.getCurrentUserSync();
    if (cached?.customerId) {
      this.populateUserSummary(cached.customerId);
      return;
    }

    this.authService.getCurrentUser().subscribe({
      next: (user) => this.populateUserSummary(user.customerId),
      error: () => {
        this.status.set({ loading: false, success: false, error: '' });
      }
    });
  }

  private populateUserSummary(customerId: number): void {
    this.loanService.getLoans(0, 300).subscribe({
      next: (loansResponse) => {
        const loans = loansResponse.content.filter((loan) => loan.customerId === customerId);
        const activeCount = loans.filter((loan) => String(loan.loanStatus || '').toUpperCase() === 'ACTIVE').length;
        this.activeLoans.set(activeCount);

        this.loanService.getMyLoanApplications(0, 100).subscribe({
          next: (applicationsResponse) => {
            const pending = applicationsResponse.content.filter((app) => String(app.applicationStatus || '').toUpperCase() !== 'APPROVED').length;
            this.pendingApplications.set(pending);

            this.loanService.getEmiPayments(0, 200).subscribe({
              next: (paymentsResponse) => {
                const rows = Array.isArray(paymentsResponse) ? paymentsResponse : paymentsResponse.content;
                const myPayments = rows.filter((row) => Number((row as { customerId?: number }).customerId) === customerId).length;
                this.recentPayments.set(myPayments);
                this.status.set({ loading: false, success: false, error: '' });
              },
              error: () => this.status.set({ loading: false, success: false, error: '' })
            });
          },
          error: () => this.status.set({ loading: false, success: false, error: '' })
        });
      },
      error: () => this.status.set({ loading: false, success: false, error: '' })
    });
  }

  private resolveRole(): 'USER' | 'MANAGER' | 'ADMIN' {
    const primaryRole = String(this.tokenStorage.getPrimaryRole() || '').replace('ROLE_', '');
    if (primaryRole === 'ADMIN' || primaryRole === 'MANAGER') {
      return primaryRole;
    }
    return 'USER';
  }
}

