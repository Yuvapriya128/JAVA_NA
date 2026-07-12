import {Component, inject, signal} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,RouterLink,RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  //protected readonly title = signal('routingDemo');
  router=inject(Router);
  searchTerm=signal('');

  showPerson(){
  if(this.searchTerm()==='yuva') {
    this.router.navigate(['/person',1]);
  }
}

}
