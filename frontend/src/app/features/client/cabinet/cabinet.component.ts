import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {AuthService} from '../../../core/auth/services/auth.service';
import {CartService} from '../../../core/services/cart.service';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';

interface IUserProfile {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  emailVerified: boolean;
}

@Component({
  selector: 'app-cabinet',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cabinet.component.html',
  styleUrl: './cabinet.component.scss'
})
export class CabinetComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly cartService = inject(CartService);
  private readonly http        = inject(HttpClient);

  profile   = signal<IUserProfile | null>(null);
  cartCount = this.cartService.cartCount;
  activeTab = signal<'profile' | 'orders'>('profile');

  get isAdmin():  boolean { return this.authService.getRoleId() === 1; }
  get isClient(): boolean { return this.authService.getRoleId() === 2; }

  ngOnInit(): void {
    this.loadProfile();
    if (this.isClient) {
      this.cartService.getCart().subscribe({ error: () => {} });
    }
  }

  loadProfile(): void {
    this.http.get<IUserProfile>(`${environment.rootUrl}users/info`).subscribe({
      next:  (p) => this.profile.set(p),
      error: () => {}
    });
  }

  logout(): void { this.authService.logout(); }
}
