import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'})
export class SearchComponent implements OnInit, OnDestroy {
  @Input() ariaLabel = 'Search input';
  @Input() debounceMs = 300;
  @Input() suggestions: string[] = [];
  @Input() minSuggestionLength = 1;
  @Input() loading = false;
  @Input() buttonLabel = 'Search';
  @Input() placeholder = 'Search...';
  @Output() search = new EventEmitter<string>();
  @Output() searchChange = new EventEmitter<string>();
  @Output() suggestionSelected = new EventEmitter<string>();

  searchTerm = '';
  isSuggestionOpen = false;

  filteredSuggestions: string[] = [];

  private readonly inputSubject = new Subject<string>();
  private inputSub?: Subscription;

  ngOnInit(): void {
    this.bindDebouncedSearch();
  }

  ngOnDestroy(): void {
    this.inputSub?.unsubscribe();
  }

  onSearchChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.searchTerm = target.value;
    this.inputSubject.next(this.searchTerm);
  }

  onSearch(): void {
    this.search.emit(this.searchTerm);
    this.searchChange.emit(this.searchTerm);
    this.isSuggestionOpen = false;
  }

  onFocus(): void {
    this.updateSuggestions(this.searchTerm);
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.isSuggestionOpen = false;
    }
  }

  selectSuggestion(value: string): void {
    this.searchTerm = value;
    this.search.emit(value);
    this.searchChange.emit(value);
    this.suggestionSelected.emit(value);
    this.isSuggestionOpen = false;
  }

  closeSuggestions(): void {
    setTimeout(() => {
      this.isSuggestionOpen = false;
    }, 100);
  }

  clear(): void {
    this.searchTerm = '';
    this.filteredSuggestions = [];
    this.isSuggestionOpen = false;
    this.search.emit('');
    this.searchChange.emit('');
  }

  trackBySuggestion(_: number, suggestion: string): string {
    return suggestion;
  }

  private bindDebouncedSearch(): void {
    this.inputSub = this.inputSubject
      .pipe(debounceTime(this.debounceMs), distinctUntilChanged())
      .subscribe((term) => {
        this.updateSuggestions(term);
        this.searchChange.emit(term);
      });
  }

  private updateSuggestions(term: string): void {
    const normalized = term.trim().toLowerCase();
    if (!normalized || normalized.length < this.minSuggestionLength) {
      this.filteredSuggestions = [];
      this.isSuggestionOpen = false;
      return;
    }

    const unique = Array.from(new Set(this.suggestions));
    this.filteredSuggestions = unique
      .filter((item) => item.toLowerCase().includes(normalized))
      .slice(0, 8);
    this.isSuggestionOpen = this.filteredSuggestions.length > 0;
  }
}

