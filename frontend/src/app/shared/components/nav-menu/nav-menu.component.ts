import {
  Component, inject, signal, OnInit, HostListener
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin }      from 'rxjs';
import {ICategory, IProductGroup, ISubCategory} from '../../../core/models/models';
import {CategoryService} from '../../../core/services/category.service';
import {SubCategoryService} from '../../../core/services/sub-category.service';
import {ProductGroupService} from '../../../core/services/product-group.service';

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

  categories     = signal<INavCategory[]>([]);
  activeCategory = signal<INavCategory | null>(null);
  isLoading      = signal(true);

  // Mobile
  mobileNavOpen = signal(false);
  mobileOpen    = signal<number | null>(null);
  mobileMenuOpen = signal<number | null>(null); // сумісність зі старим шаблоном

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
                this.categories.set(navCats);
                this.isLoading.set(false);
              }
            });
          }
        });
      }
    });
  }

  // ---- Desktop hover ----
  onCategoryEnter(cat: INavCategory): void {
    this.activeCategory.set(cat);
  }

  onMenuLeave(): void {
    this.activeCategory.set(null);
  }

  // ---- Mobile nav ----
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
    this.closeMobileNav();
  }
}
