import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.css']
})
export class NotFoundComponent {
  private readonly router = inject(Router);
  readonly searchTerm = signal('');

  searchRoute(): void {
    const query = this.searchTerm().trim().toLowerCase();
    if (!query) {
      return;
    }

    if (query.includes('loan')) {
      this.router.navigate(['/loans']);
      return;
    }
    if (query.includes('customer')) {
      this.router.navigate(['/customers']);
      return;
    }
    if (query.includes('payment') || query.includes('emi')) {
      this.router.navigate(['/payments']);
      return;
    }
    this.router.navigate(['/dashboard']);
  }
}
