import { Component, inject, signal, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {AuthService} from "../../../core/auth/services/auth.service";
import {CartService} from "../../../core/services/cart.service";
import {Roles} from "../../../core/auth/enums/roles";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly cartService = inject(CartService);

  searchQuery = signal('');
  isScrolled  = signal(false);

  // Живий лічильник кошика з CartService
  readonly cartCount = this.cartService.cartCount;

  get isLoggedIn(): boolean { return !!this.authService.getRoleId(); }
  get isAdmin():    boolean { return this.authService.getRoleId() === Roles.ADMIN; }
  get isClient():   boolean { return this.authService.getRoleId() === 2; }

  readonly phones = [
    { label: '(067) 291-71-97', href: 'tel:+380672917197' },
    { label: '(067) 405-33-44', href: 'tel:+380674053344' },
  ];

  readonly workHours = 'Пн-Пт 09:00–17:30';

  readonly topLinks = [
    { label: 'Про нас',            path: '/about' },
    { label: 'Оплата та доставка', path: '/delivery' },
    { label: 'Сертифікати',        path: '/certificates' },
    { label: 'Новини',             path: '/news' },
    { label: 'Статті',             path: '/articles' },
    { label: 'Виробники',          path: '/brands' },
    { label: 'Контакти',           path: '/contacts' },
  ];

  ngOnInit(): void {
    // Завантажуємо кількість в кошику при старті якщо клієнт
    if (this.isClient) {
      this.cartService.getCart().subscribe({
        error: () => {}  // ігноруємо помилку — просто лічильник не оновиться
      });
    }
  }

  @HostListener('window:scroll')
  onScroll(): void { this.isScrolled.set(window.scrollY > 40); }

  onSearch(): void {
    // TODO: navigate to search
  }

  logout(): void { this.authService.logout(); }
}
