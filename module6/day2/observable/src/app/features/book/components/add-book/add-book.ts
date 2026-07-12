import {Component, inject} from '@angular/core';
import {BookServices} from '../../services/book-services';
import BookDTO from '../../DTO/BookDTO';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-add-book',
  imports: [FormsModule],
  templateUrl: './add-book.html',
  styleUrl: './add-book.css',
})
export class AddBook {
  bookServices:BookServices=inject(BookServices);
  protected newBook:BookDTO={
    id: 0,
    title: '',
    author: '',
    publisher: ''
  }
}
