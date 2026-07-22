import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './shared/components/navbar/navbar';
import { Footer } from './shared/components/footer/footer';
import { Loading } from './shared/components/loading/loading';
import { AlertMessage } from './shared/components/alert-message/alert-message';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Footer, Loading, AlertMessage],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {}
