import {Component, inject} from '@angular/core';
import {BookServices} from '../../services/book-services';
import {AsyncPipe} from '@angular/common';
import {BookItems} from '../book-items/book-items';
import {AddBook} from '../add-book/add-book';
import {UpdateBook} from '../update-book/update-book';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-book-components',
  imports: [AsyncPipe,BookItems,AddBook,UpdateBook,FormsModule],
  templateUrl: './book-components.html',
  styleUrl: './book-components.css',
})
export class BookComponents {
  bookServices:BookServices=inject(BookServices);
  protected showUpdate:boolean = false;
}
