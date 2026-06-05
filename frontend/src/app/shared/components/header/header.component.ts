import { Component, inject, signal, HostListener, OnInit, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule }      from '@angular/forms';
import { forkJoin }         from 'rxjs';
import { AuthService }      from '../../../core/auth/services/auth.service';
import { CartService }      from '../../../core/services/cart.service';
import { FavoriteService }  from '../../../core/services/favorite.service';
import { CategoryService }  from '../../../core/services/category.service';
import { SubCategoryService }   from '../../../core/services/sub-category.service';
import { ProductGroupService }  from '../../../core/services/product-group.service';
import { Roles }            from '../../../core/auth/enums/roles';
import { INavCategory }     from '../nav-menu/nav-menu.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  private readonly authService     = inject(AuthService);
  private readonly cartService     = inject(CartService);
  private readonly favoriteService = inject(FavoriteService);
  private readonly router          = inject(Router);
  private readonly elRef           = inject(ElementRef);

  private readonly categoryService     = inject(CategoryService);
  private readonly subCategoryService  = inject(SubCategoryService);
  private readonly productGroupService = inject(ProductGroupService);

  searchQuery = signal('');
  isScrolled  = signal(false);

  readonly cartCount = this.cartService.cartCount;
  readonly favCount  = this.favoriteService.favoriteIds;

  get isLoggedIn(): boolean { return !!this.authService.getRoleId(); }
  get isAdmin():    boolean { return this.authService.getRoleId() === Roles.ADMIN; }
  get isClient():   boolean { return this.authService.getRoleId() === 2; }

  readonly phones = [
    { label: '(067) 291-71-97', href: 'tel:+380672917197' },
    { label: '(067) 405-33-44', href: 'tel:+380674053344' },
  ];

  readonly workHours = 'Пн-Пт 09:00–17:30';

  readonly topLinks = [
    { label: 'Про нас',            path: '/about'        },
    { label: 'Оплата та доставка', path: '/delivery'     },
    { label: 'Сертифікати',        path: '/certificates' },
    // { label: 'Новини',             path: '/news'         },
    // { label: 'Статті',             path: '/articles'     },
    { label: 'Виробники',          path: '/brands'       },
    { label: 'Контакти',           path: '/contacts'     },
  ];

  categories      = signal<INavCategory[]>([]);
  activeCategory  = signal<INavCategory | null>(null);
  isLoading       = signal(true);
  desktopMenuOpen = signal(false);
  mobileNavOpen   = signal(false);
  mobileOpen      = signal<number | null>(null);

  ngOnInit(): void {
    this.loadMenu();

    if (this.isClient) {
      this.cartService.getCart().subscribe({ error: () => {} });
      this.favoriteService.getMyFavorites().subscribe({ error: () => {} });
    }
  }

  private loadMenu(): void {
    this.categoryService.getAllList().subscribe({
      next: (cats) => {
        forkJoin(cats.map(c => this.subCategoryService.getByCategoryId(c.id))).subscribe({
          next: (subCatsPerCat) => {
            const allSubs = subCatsPerCat.flat();
            forkJoin(allSubs.map(s => this.productGroupService.getBySubCategoryId(s.id))).subscribe({
              next: (groupsPerSub) => {
                let idx = 0;
                const navCats: INavCategory[] = cats.map((cat, ci) => {
                  const subCategories = subCatsPerCat[ci].map(sub => {
                    const groups = groupsPerSub[idx] || [];
                    idx++;
                    return { ...sub, productGroups: groups };
                  });

                  // КРИТИЧНА БІЗНЕС-ЛОГІКА: підкатегорії з найбільшою кількістю
                  // груп товарів — першими. Сортуємо ОДИН РАЗ при побудові дерева
                  // (не на кожен hover) — стабільно, без мутації сигнального стану.
                  // .toSorted() повертає новий масив (ES2023), сортування стабільне.
                  const sortedSubs = [...subCategories].sort(
                    (a, b) => b.productGroups.length - a.productGroups.length
                  );

                  return { ...cat, subCategories: sortedSubs };
                });

                this.categories.set(navCats);
                this.isLoading.set(false);
              }
            });
          }
        });
      }
    });
  }

  @HostListener('window:scroll')
  onScroll(): void { this.isScrolled.set(window.scrollY > 40); }

  onSearch(): void {
    const q = this.searchQuery().trim();
    if (!q) return;
    this.desktopMenuOpen.set(false);
    this.router.navigate(['/search'], { queryParams: { q } });
  }

  logout(): void { this.authService.logout(); }

  toggleDesktopMenu(event: Event): void {
    event.stopPropagation();
    this.desktopMenuOpen.update(v => !v);
    if (this.desktopMenuOpen() && !this.activeCategory() && this.categories().length > 0) {
      this.activeCategory.set(this.categories()[0]);
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    if (!this.elRef.nativeElement.contains(event.target) && this.desktopMenuOpen()) {
      this.desktopMenuOpen.set(false);
      this.activeCategory.set(null);
    }
  }

  // Тільки встановлюємо активну категорію — без мутації/сортування.
  // Дерево вже відсортоване в loadMenu().
  onCategoryEnter(cat: INavCategory): void {
    this.activeCategory.set(cat);
  }

  onLinkClick(): void {
    this.desktopMenuOpen.set(false);
    this.activeCategory.set(null);
  }

  toggleMobileNav(): void { this.mobileNavOpen.update(v => !v); }
  closeMobileNav(): void  { this.mobileNavOpen.set(false); this.mobileOpen.set(null); }
  toggleMobile(id: number): void { this.mobileOpen.update(v => v === id ? null : id); }
  isMobileOpen(id: number): boolean { return this.mobileOpen() === id; }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.desktopMenuOpen.set(false);
    this.activeCategory.set(null);
    this.closeMobileNav();
  }
}
