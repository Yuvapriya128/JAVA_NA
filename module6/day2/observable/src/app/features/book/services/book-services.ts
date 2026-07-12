import { Service } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import BookDTO from '../DTO/BookDTO';

@Service()
export class BookServices {
  private books: BookDTO[] = [
    {
      id: 1,
      title: 'Ramayana',
      author: 'Valmiki',
      publisher: 'Valmiki Press',
    },
    {
      id: 2,
      title: 'Mahabharat',
      author: 'Kalki',
      publisher: 'Kalki Press',
    },
    {
      id: 3,
      title: 'Berlin',
      author: 'Martha',
      publisher: 'Berlin Press',
    }
  ];
  private books$:BehaviorSubject<BookDTO[]> = new BehaviorSubject(this.books);

  getBooks(): BehaviorSubject<BookDTO[]> {
    return this.books$;
  }
  addBook(book: BookDTO) {
    this.books$.next([...this.books$.getValue(), book]);
  }
  deleteBook(id: number) {
    this.books=this.books.filter(b=>b.id!==id);
    this.books$.next(this.books);
  }
  updateBook(book: BookDTO) {
    this.books=this.books.map(b=>b.id===book.id ? book:b);
    this.books$.next(this.books);
    // this.books$.next([...this.books$.getValue(), book]);
  }
}
