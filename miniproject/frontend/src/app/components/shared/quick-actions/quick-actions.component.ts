import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, EventEmitter, Output, QueryList, ViewChild, ViewChildren, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { QUICK_ACTION_COMMANDS, QuickActionCommand, QuickActionGroup, QuickActionRole } from './quick-actions.config';

@Component({
  selector: 'app-quick-actions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './quick-actions.component.html',
  styleUrl: './quick-actions.component.css'
})
export class QuickActionsComponent implements AfterViewInit {
  @Output() closed = new EventEmitter<void>();
  @ViewChild('searchInput') searchInput?: ElementRef<HTMLInputElement>;
  @ViewChildren('commandItem') commandItems?: QueryList<ElementRef<HTMLButtonElement>>;

  readonly groupOrder: QuickActionGroup[] = ['Customers', 'Loans', 'EMI', 'Reports', 'Account', 'System'];
  searchTerm = '';
  highlightedIndex = 0;
  closing = false;

  private readonly router = inject(Router);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly role = this.resolveRole();
  private readonly favoriteStorageKey = 'loanhub-quick-actions-favorites';
  private readonly recentStorageKey = 'loanhub-quick-actions-recent';

  private favoriteIds = new Set<string>(this.readStorage(this.favoriteStorageKey));
  private recentIds = this.readStorage(this.recentStorageKey);

  ngAfterViewInit(): void {
    setTimeout(() => this.searchInput?.nativeElement.focus());
  }

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('quick-actions-backdrop')) {
      this.requestClose();
    }
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      event.preventDefault();
      this.requestClose();
      return;
    }

    if (event.key === 'ArrowDown') {
      event.preventDefault();
      this.moveHighlight(1);
      return;
    }

    if (event.key === 'ArrowUp') {
      event.preventDefault();
      this.moveHighlight(-1);
      return;
    }

    if (event.key === 'Enter') {
      event.preventDefault();
      const command = this.visibleCommands()[this.highlightedIndex];
      if (command) {
        this.executeCommand(command);
      }
      return;
    }

    if (event.key === 'Tab') {
      this.trapFocus(event);
    }
  }

  onSearchChange(): void {
    this.highlightedIndex = 0;
  }

  pinnedCommands(): QuickActionCommand[] {
    return this.filteredCommands().filter((command) => this.favoriteIds.has(command.id));
  }

  recentCommands(): QuickActionCommand[] {
    if (this.searchTerm.trim().length > 0) {
      return [];
    }
    const availableMap = new Map(this.availableCommands().map((command) => [command.id, command]));
    return this.recentIds
      .map((id) => availableMap.get(id))
      .filter((command): command is QuickActionCommand => !!command);
  }

  groupedCommands(group: QuickActionGroup): QuickActionCommand[] {
    return this.filteredCommands().filter(
      (command) => command.group === group && !this.favoriteIds.has(command.id)
    );
  }

  visibleCommands(): QuickActionCommand[] {
    const grouped = this.groupOrder.flatMap((group) => this.groupedCommands(group));
    return [...this.pinnedCommands(), ...grouped];
  }

  hasResults(): boolean {
    return this.visibleCommands().length > 0;
  }

  isFavorite(command: QuickActionCommand): boolean {
    return this.favoriteIds.has(command.id);
  }

  toggleFavorite(event: MouseEvent, command: QuickActionCommand): void {
    event.stopPropagation();

    if (this.favoriteIds.has(command.id)) {
      this.favoriteIds.delete(command.id);
    } else {
      this.favoriteIds.add(command.id);
    }

    this.favoriteIds = new Set(this.favoriteIds);
    this.writeStorage(this.favoriteStorageKey, Array.from(this.favoriteIds));
  }

  commandIndex(command: QuickActionCommand): number {
    return this.visibleCommands().findIndex((item) => item.id === command.id);
  }

  executeCommand(command: QuickActionCommand): void {
    this.persistRecentCommand(command.id);
    this.router.navigate([command.route]);
    this.requestClose();
  }

  requestClose(): void {
    this.closing = true;
    setTimeout(() => this.closed.emit(), 200);
  }

  private moveHighlight(direction: number): void {
    const commands = this.visibleCommands();
    if (commands.length === 0) {
      this.highlightedIndex = 0;
      return;
    }

    const nextIndex = this.highlightedIndex + direction;
    if (nextIndex < 0) {
      this.highlightedIndex = commands.length - 1;
    } else if (nextIndex >= commands.length) {
      this.highlightedIndex = 0;
    } else {
      this.highlightedIndex = nextIndex;
    }

    this.scrollHighlightedIntoView();
  }

  private scrollHighlightedIntoView(): void {
    setTimeout(() => {
      const target = this.commandItems?.get(this.highlightedIndex)?.nativeElement;
      target?.scrollIntoView({ block: 'nearest' });
    });
  }

  private trapFocus(event: KeyboardEvent): void {
    const root = event.currentTarget as HTMLElement;
    const focusable = Array.from(
      root.querySelectorAll<HTMLElement>('button, input, [href], [tabindex]:not([tabindex="-1"])')
    ).filter((element) => !element.hasAttribute('disabled'));

    if (focusable.length === 0) {
      return;
    }

    const activeElement = document.activeElement as HTMLElement | null;
    const first = focusable[0];
    const last = focusable[focusable.length - 1];

    if (event.shiftKey && activeElement === first) {
      event.preventDefault();
      last.focus();
      return;
    }

    if (!event.shiftKey && activeElement === last) {
      event.preventDefault();
      first.focus();
    }
  }

  private availableCommands(): QuickActionCommand[] {
    return QUICK_ACTION_COMMANDS.filter((command) => command.roles.includes(this.role));
  }

  private filteredCommands(): QuickActionCommand[] {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) {
      return this.availableCommands();
    }

    return this.availableCommands().filter((command) => {
      const keywordHit = command.keywords.some((keyword) => keyword.toLowerCase().includes(term));
      return command.title.toLowerCase().includes(term)
        || command.description.toLowerCase().includes(term)
        || command.route.toLowerCase().includes(term)
        || keywordHit;
    });
  }

  private persistRecentCommand(id: string): void {
    const deduped = [id, ...this.recentIds.filter((entry) => entry !== id)];
    this.recentIds = deduped.slice(0, 5);
    this.writeStorage(this.recentStorageKey, this.recentIds);
  }

  private resolveRole(): QuickActionRole {
    const role = this.tokenStorage.getPrimaryRole();
    if (role === 'ADMIN' || role === 'MANAGER') {
      return role;
    }
    return 'USER';
  }

  private readStorage(key: string): string[] {
    if (typeof window === 'undefined') {
      return [];
    }
    const value = localStorage.getItem(key);
    if (!value) {
      return [];
    }
    try {
      const parsed = JSON.parse(value) as unknown;
      return Array.isArray(parsed)
        ? parsed.map((item) => String(item)).filter((item) => item.length > 0)
        : [];
    } catch {
      return [];
    }
  }

  private writeStorage(key: string, value: string[]): void {
    if (typeof window === 'undefined') {
      return;
    }
    localStorage.setItem(key, JSON.stringify(value));
  }
}
