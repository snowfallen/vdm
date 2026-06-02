import { Component, inject, signal, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  searchQuery = signal('');
  cartCount   = signal(0);
  mobileMenuOpen = signal(false);
  isScrolled = signal(false);

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
    // TODO: підключити search service
    console.log('Search:', this.searchQuery());
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen.update(v => !v);
  }
}
