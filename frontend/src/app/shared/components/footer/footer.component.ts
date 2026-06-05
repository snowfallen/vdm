import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {ISiteSettings, MediaService} from '../../../core/services/media.service';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss'
})
export class FooterComponent implements OnInit {
  private readonly media = inject(MediaService);

  readonly currentYear = new Date().getFullYear();

  // Налаштування з API — з fallback на захардкоджені значення
  s = signal<ISiteSettings>({
    contact_phone_main:   '(067) 291-71-97',
    contact_phone_second: '(067) 405-33-44',
    contact_phone_mobile: '(067) 291-71-79',
    contact_email:        'info@vdm.ua',
    work_hours_weekday:   'Пн-Пт 09:00 – 17:30',
    work_hours_weekend:   'Сб-Нд – Вихідний',
  });

  ngOnInit(): void {
    // Якщо settings вже є (завантажені раніше) — беремо звідти
    const cached = this.media.settings();
    if (cached.contact_phone_main) { this.s.set({ ...this.s(), ...cached }); return; }

    this.media.loadSettings().subscribe({
      next: settings => this.s.set({ ...this.s(), ...settings })
    });
  }

  readonly topLinks = [
    { label: 'Про нас',            path: '/about' },
    { label: 'Оплата та доставка', path: '/delivery' },
    { label: 'Сертифікати',        path: '/certificates' },
    // { label: 'Новини',             path: '/news' },
    // { label: 'Статті',             path: '/articles' },
    { label: 'Виробники',          path: '/brands' },
    { label: 'Контакти',           path: '/contacts' },
  ];

  readonly companyLinks = [
    { label: 'Про нас',   path: '/about' },
    // { label: 'Новини',    path: '/news' },
    // { label: 'Статті',    path: '/articles' },
  ];

  readonly buyerLinks = [
    { label: 'Контакти',       path: '/contacts' },
    { label: 'Постачальникам', path: '/suppliers' },
  ];

  readonly socials = [
    {
      label: 'Facebook', href: 'https://facebook.com',
      icon: `<path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"/>`
    },
    {
      label: 'YouTube', href: 'https://youtube.com',
      icon: `<polygon points="23 7 16 12 23 17 23 7"/><rect x="1" y="5" width="15" height="14" rx="2" ry="2"/>`
    },
    {
      label: 'Instagram', href: 'https://instagram.com',
      icon: `<rect x="2" y="2" width="20" height="20" rx="5" ry="5"/><circle cx="12" cy="12" r="4"/><circle cx="17.5" cy="6.5" r="1" fill="currentColor" stroke="none"/>`
    },
  ];

  phoneHref(phone: string | undefined): string {
    if (!phone) return 'tel:';
    return 'tel:' + phone.replace(/[^+\d]/g, '');
  }
}
