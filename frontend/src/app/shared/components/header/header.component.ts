import {Component, inject, signal, HostListener, OnInit, ElementRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {AuthService} from "../../../core/auth/services/auth.service";
import {CartService} from "../../../core/services/cart.service";
import {Roles} from "../../../core/auth/enums/roles";
import {CategoryService} from "../../../core/services/category.service";
import {SubCategoryService} from "../../../core/services/sub-category.service";
import {ProductGroupService} from "../../../core/services/product-group.service";
import {forkJoin} from "rxjs";
import {INavCategory} from "../nav-menu/nav-menu.component";

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
    this.loadMenu();

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

  private readonly categoryService     = inject(CategoryService);
  private readonly subCategoryService  = inject(SubCategoryService);
  private readonly productGroupService = inject(ProductGroupService);
  private readonly elRef               = inject(ElementRef); // Додаємо для відслідковування кліків

  categories     = signal<INavCategory[]>([]);
  activeCategory = signal<INavCategory | null>(null);
  isLoading      = signal(true);

  // Desktop Menu State
  desktopMenuOpen = signal(false);

  // Mobile Menu State
  mobileNavOpen  = signal(false);
  mobileOpen     = signal<number | null>(null);
  mobileMenuOpen = signal<number | null>(null);

  private loadMenu(): void {
    this.categoryService.getAllList().subscribe({
      next: (cats) => {
        const subRequests = cats.map(cat =>
            this.subCategoryService.getByCategoryId(cat.id)
        );

        forkJoin(subRequests).subscribe({
          next: (subCatsPerCategory) => {
            const allSubCats = subCatsPerCategory.flat();
            const groupRequests = allSubCats.map(sub =>
                this.productGroupService.getBySubCategoryId(sub.id)
            );

            forkJoin(groupRequests).subscribe({
              next: (groupsPerSub) => {
                let globalIdx = 0;
                const navCats: INavCategory[] = cats.map((cat, catIdx) => ({
                  ...cat,
                  subCategories: subCatsPerCategory[catIdx].map(sub => {
                    const groups = groupsPerSub[globalIdx] || [];
                    globalIdx++;
                    return { ...sub, productGroups: groups };
                  })
                }));
                console.log(navCats);
                this.categories.set(navCats);
                this.isLoading.set(false);
              }
            });
          }
        });
      }
    });
  }

  // ---- Desktop Logic (Click Based) ----

  toggleDesktopMenu(event: Event): void {
    // Зупиняємо спливання події, щоб document:click не спрацював миттєво і не закрив меню
    event.stopPropagation();
    this.desktopMenuOpen.update(v => !v);

    // Якщо відкрили меню, але категорія не вибрана — ставимо першу
    if (this.desktopMenuOpen() && !this.activeCategory() && this.categories().length > 0) {
      this.activeCategory.set(this.categories()[0]);
    }
  }

  // Закриваємо меню, якщо клікнули десь поза компонентом
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    const clickedInside = this.elRef.nativeElement.contains(event.target);
    if (!clickedInside && this.desktopMenuOpen()) {
      this.desktopMenuOpen.set(false);
      this.activeCategory.set(null); // скидаємо активну категорію при закритті
    }
  }

  // Залишаємо ховер для перемикання категорій ВСЕРЕДИНІ відкритого меню
  onCategoryEnter(cat: INavCategory): void {
    cat.subCategories.sort((a, b) => b.productGroups.length - a.productGroups.length);
    this.activeCategory.set(cat);
  }

  onLinkClick(): void {
    this.desktopMenuOpen.set(false);
    this.activeCategory.set(null);
  }

  // ---- Mobile Logic ----
  toggleMobileNav(): void {
    this.mobileNavOpen.update(v => !v);
    if (!this.mobileNavOpen()) {
      this.mobileOpen.set(null);
    }
  }

  closeMobileNav(): void {
    this.mobileNavOpen.set(false);
    this.mobileOpen.set(null);
  }

  toggleMobile(catId: number): void {
    this.mobileOpen.update(v => v === catId ? null : catId);
  }

  isMobileOpen(catId: number): boolean {
    return this.mobileOpen() === catId;
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.activeCategory.set(null);
    this.desktopMenuOpen.set(false);
    this.closeMobileNav();
  }
}
