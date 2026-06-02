import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import {CategoryService} from '../../../core/services/category.service';
import {ProductService} from '../../../core/services/product.service';
import {UserService} from '../../../core/services/user.service';
import {UnitService} from '../../../core/services/unit.service';
import {AttributeAdminService} from '../../../core/services/attribute-admin.service';
import {AuthService} from '../../../core/auth/services/auth.service';
import {Roles} from '../../../core/auth/enums/roles';

interface IStatCard {
  label: string;
  value: number | string;
  icon: string;
  link: string;
  color: string;
}

interface IQuickLink {
  label: string;
  description: string;
  path: string;
  icon: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly categoryService    = inject(CategoryService);
  private readonly productService     = inject(ProductService);
  private readonly userService        = inject(UserService);
  private readonly unitService        = inject(UnitService);
  private readonly attributeService   = inject(AttributeAdminService);
  private readonly authService        = inject(AuthService);

  isAdmin   = signal(this.authService.getRoleId() === Roles.ADMIN);
  isLoading = signal(true);

  stats = signal<IStatCard[]>([]);

  readonly adminQuickLinks: IQuickLink[] = [
    { label: 'Додати товар',      description: 'Створити новий товар в каталозі',       path: '/admin/products',          icon: 'plus' },
    { label: 'Атрибути',          description: 'Налаштувати атрибути товарів',           path: '/admin/attributes',        icon: 'tag' },
    { label: 'Категорії',         description: 'Управляти категоріями каталогу',          path: '/admin/categories',        icon: 'folder' },
    { label: 'Користувачі',       description: 'Переглянути всіх користувачів',          path: '/admin/users',             icon: 'users' },
    { label: 'Одиниці виміру',    description: 'Налаштувати одиниці виміру',             path: '/admin/units',             icon: 'hash' },
    { label: 'Опції атрибутів',   description: 'Управляти значеннями атрибутів',         path: '/admin/attribute-options', icon: 'list' },
  ];

  readonly clientQuickLinks: IQuickLink[] = [
    { label: 'Каталог товарів',   description: 'Переглянути всі товари',   path: '/catalog',        icon: 'package' },
    { label: 'Мій профіль',       description: 'Редагувати особисті дані', path: '/admin/profile',  icon: 'user' },
  ];

  get quickLinks(): IQuickLink[] {
    return this.isAdmin() ? this.adminQuickLinks : this.clientQuickLinks;
  }

  ngOnInit(): void {
    if (!this.isAdmin()) {
      this.isLoading.set(false);
      return;
    }

    forkJoin({
      categories:  this.categoryService.getAllList(),
      products:    this.productService.getAll(0, 1),
      units:       this.unitService.getAll(),
      attributes:  this.attributeService.getAll(0, 1),
    }).subscribe({
      next: ({ categories, products, units, attributes }) => {
        this.stats.set([
          { label: 'Категорій',  value: categories.length,            icon: 'folder',  link: '/admin/categories',  color: 'blue'   },
          { label: 'Товарів',    value: products.totalElements,       icon: 'package', link: '/admin/products',    color: 'green'  },
          { label: 'Атрибутів',  value: attributes.totalElements,     icon: 'tag',     link: '/admin/attributes',  color: 'purple' },
          { label: 'Одиниць',    value: units.length,                 icon: 'hash',    link: '/admin/units',       color: 'orange' },
        ]);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  getIcon(name: string): string {
    const icons: Record<string, string> = {
      plus:    `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>`,
      tag:     `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>`,
      folder:  `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>`,
      users:   `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`,
      hash:    `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="4" y1="9" x2="20" y2="9"/><line x1="4" y1="15" x2="20" y2="15"/><line x1="10" y1="3" x2="8" y2="21"/><line x1="16" y1="3" x2="14" y2="21"/></svg>`,
      list:    `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>`,
      package: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="16.5" y1="9.4" x2="7.5" y2="4.21"/><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></svg>`,
      user:    `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>`,
    };
    return icons[name] ?? '';
  }
}
