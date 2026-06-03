import {
  Component, inject, signal, OnInit, HostListener, ElementRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin }      from 'rxjs';
import { ICategory, IProductGroup, ISubCategory } from '../../../core/models/models';
import { CategoryService } from '../../../core/services/category.service';
import { SubCategoryService } from '../../../core/services/sub-category.service';
import { ProductGroupService } from '../../../core/services/product-group.service';

export interface INavSubCategory extends ISubCategory {
  productGroups: IProductGroup[];
}

export interface INavCategory extends ICategory {
  subCategories: INavSubCategory[];
}

@Component({
  selector: 'app-nav-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './nav-menu.component.html',
  styleUrl: './nav-menu.component.scss'
})
export class NavMenuComponent implements OnInit {
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

  ngOnInit(): void {
    this.loadMenu();
  }

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
