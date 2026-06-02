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
  private readonly categoryService    = inject(CategoryService);
  private readonly subCategoryService = inject(SubCategoryService);
  private readonly productGroupService = inject(ProductGroupService);

  categories      = signal<INavCategory[]>([]);
  activeCategory  = signal<INavCategory | null>(null);
  isLoading       = signal(true);
  mobileOpen      = signal<number | null>(null); // id відкритої категорії на мобілі

  ngOnInit(): void {
    this.loadMenu();
  }

  private loadMenu(): void {
    this.categoryService.getAllList().subscribe({
      next: (cats) => {
        // Завантажуємо підкатегорії для всіх категорій паралельно
        const subRequests = cats.map(cat =>
          this.subCategoryService.getByCategoryId(cat.id)
        );

        forkJoin(subRequests).subscribe({
          next: (subCatsPerCategory) => {
            // Для кожної підкатегорії завантажуємо product groups
            const allSubCats = subCatsPerCategory.flat();
            const groupRequests = allSubCats.map(sub =>
              this.productGroupService.getBySubCategoryId(sub.id)
            );

            forkJoin(groupRequests).subscribe({
              next: (groupsPerSub) => {
                // Збираємо в повну структуру
                const navCats: INavCategory[] = cats.map((cat, catIdx) => ({
                  ...cat,
                  subCategories: subCatsPerCategory[catIdx].map((sub, subIdx) => {
                    const globalSubIdx = subCatsPerCategory
                      .slice(0, catIdx)
                      .reduce((acc, arr) => acc + arr.length, 0) + subIdx;
                    return {
                      ...sub,
                      productGroups: groupsPerSub[globalSubIdx] || []
                    };
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

  // ---- Mobile accordion ----
  toggleMobile(catId: number): void {
    this.mobileOpen.update(v => v === catId ? null : catId);
  }

  isMobileOpen(catId: number): boolean {
    return this.mobileOpen() === catId;
  }

  // Закрити мега-меню при кліку поза ним
  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.activeCategory.set(null);
  }
}
