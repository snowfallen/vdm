import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { IProduct } from '../../core/models/models';
import {ProductCardComponent} from '../../features/product-card/product-card.component';

interface IPage<T> { content: T[]; totalElements: number; totalPages: number; }

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ProductCardComponent],
  template: `
    <div class="search-page">
      <div class="container">
        <h1 class="search-page__title">
          Результати пошуку: <em>"{{ query() }}"</em>
          <span class="search-page__count" *ngIf="!isLoading()">{{ total() }} товарів</span>
        </h1>

        <div *ngIf="isLoading()" class="products-grid">
          <div *ngFor="let _ of [1,2,3,4,5,6]" class="skeleton" style="height:280px;border-radius:12px;"></div>
        </div>

        <div *ngIf="!isLoading() && products().length === 0" class="search-page__empty">
          <p>Нічого не знайдено за запитом «{{ query() }}»</p>
          <a routerLink="/catalog" class="btn btn--primary">До каталогу</a>
        </div>

        <div *ngIf="products().length > 0" class="products-grid">
          <app-product-card *ngFor="let p of products()" [product]="p" />
        </div>
      </div>
    </div>
  `,
  styles: [`
    .search-page { padding-block: var(--space-8) var(--space-16); }
    .search-page__title {
      font-family: var(--font-display); font-size: var(--fs-2xl); font-weight: var(--fw-bold);
      color: var(--clr-text-primary); margin-bottom: var(--space-6);
      em { color: var(--clr-primary); font-style: normal; }
    }
    .search-page__count {
      font-size: var(--fs-sm); color: var(--clr-text-muted);
      background: var(--clr-surface-2); padding: 2px var(--space-3);
      border-radius: var(--radius-full); border: 1px solid var(--clr-border);
      margin-left: var(--space-3); font-family: var(--font-base); font-weight: var(--fw-medium);
    }
    .search-page__empty {
      text-align: center; padding: var(--space-16);
      p { color: var(--clr-text-muted); margin-bottom: var(--space-4); }
    }
  `]
})
export class SearchComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly http  = inject(HttpClient);

  query     = signal('');
  products  = signal<IProduct[]>([]);
  total     = signal(0);
  isLoading = signal(false);

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const q = params['q'] ?? '';
      this.query.set(q);
      if (q) this.search(q);
    });
  }

  private search(q: string): void {
    this.isLoading.set(true);
    this.http.get<IPage<IProduct>>(
      `${environment.rootUrl}products/search?q=${encodeURIComponent(q)}&size=50`
    ).subscribe({
      next: (page) => {
        this.products.set(page.content);
        this.total.set(page.totalElements);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }
}
