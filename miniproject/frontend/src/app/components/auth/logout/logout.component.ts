import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorageService } from '../../../services/auth/token-storage.service';

@Component({
  selector: 'app-logout',
  standalone: true,
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.tokenStorage.clearSession();
    this.router.navigate(['/login']);
  }
}
