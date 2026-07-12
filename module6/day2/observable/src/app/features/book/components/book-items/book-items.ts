import {Component, inject, Input} from '@angular/core';
import {BookServices} from '../../services/book-services';
import BookDTO from '../../DTO/BookDTO';

@Component({
  selector: 'app-book-items',
  imports: [],
  templateUrl: './book-items.html',
  styleUrl: './book-items.css',
})
export class BookItems {
  @Input()
  public book!: BookDTO;

  bookServices:BookServices=inject(BookServices);
}
