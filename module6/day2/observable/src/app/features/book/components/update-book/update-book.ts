import {Component, inject} from '@angular/core';
import {BookServices} from '../../services/book-services';
import BookDTO from '../../DTO/BookDTO';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-update-book',
  imports: [FormsModule],
  templateUrl: './update-book.html',
  styleUrl: './update-book.css',
})
export class UpdateBook {
  bookServices:BookServices=inject(BookServices);
  protected newBook:BookDTO={
    id: 0,
    title: '',
    author: '',
    publisher: ''
  }
}
