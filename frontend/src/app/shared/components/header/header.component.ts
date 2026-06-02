import { Component, inject, signal, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {AuthService} from '../../../core/auth/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  private readonly authService = inject(AuthService);

  searchQuery    = signal('');
  cartCount      = signal(0);
  isScrolled     = signal(false);

  // Чи залогінений юзер — перевіряємо наявність JWT cookie
  get isLoggedIn(): boolean {
    return !!this.authService.getRoleId();
  }

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

  @HostListener('window:scroll')
  onScroll(): void {
    this.isScrolled.set(window.scrollY > 40);
  }

  onSearch(): void {
    console.log('Search:', this.searchQuery());
  }

  logout(): void {
    this.authService.logout();
  }
}
