import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']})
export class FooterComponent {
  readonly appVersion = 'v2.4.1';
  readonly environmentLabel = environment.production ? 'Production' : 'Non-Production';
  readonly organization = 'Nova Lending';
  readonly supportLink = 'mailto:support@novalending.io';
  readonly year = new Date().getFullYear();

  lastSyncTime(): string {
    return new Date().toLocaleString('en-IN');
  }
}
