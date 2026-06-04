import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { CategoryService }     from '../../../../core/services/category.service';
import { SubCategoryService }  from '../../../../core/services/sub-category.service';
import { ProductGroupService } from '../../../../core/services/product-group.service';
import { ICategory, ISubCategory, IProductGroup } from '../../../../core/models/models';

interface INavGroup extends IProductGroup {}
interface INavSub   extends ISubCategory  { productGroups: INavGroup[]; }
interface INavCat   extends ICategory     { subCategories: INavSub[]; expanded: boolean; }

@Component({
  selector: 'app-catalog-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './catalog-home.component.html',
  styleUrl: './catalog-home.component.scss'
})
export class CatalogHomeComponent implements OnInit {
  private readonly categoryService     = inject(CategoryService);
  private readonly subCategoryService  = inject(SubCategoryService);
  private readonly productGroupService = inject(ProductGroupService);
  private readonly router              = inject(Router);

  categories = signal<INavCat[]>([]);
  isLoading  = signal(true);
  error      = signal('');

  activeId = signal<number | null>(null);

  ngOnInit(): void {
    this.load();
  }

  private load(): void {
    this.categoryService.getAllList().subscribe({
      next: (cats) => {
        forkJoin(cats.map(c => this.subCategoryService.getByCategoryId(c.id))).subscribe({
          next: (subCatsPerCat) => {
            const allSubs = subCatsPerCat.flat();
            if (allSubs.length === 0) {
              this.categories.set(cats.map((c, i) => ({
                ...c, subCategories: [], expanded: i === 0
              })));
              this.isLoading.set(false);
              return;
            }

            forkJoin(allSubs.map(s => this.productGroupService.getBySubCategoryId(s.id))).subscribe({
              next: (groupsPerSub) => {
                let idx = 0;
                const navCats: INavCat[] = cats.map((cat, ci) => {
                  const subCategories: INavSub[] = subCatsPerCat[ci].map(sub => {
                    const productGroups = groupsPerSub[idx] || [];
                    idx++;
                    return { ...sub, productGroups };
                  });

                  // КРИТИЧНА БІЗНЕС-ЛОГІКА: підкатегорії з найбільшою кількістю
                  // груп товарів — першими. Сортуємо ОДИН РАЗ при побудові дерева
                  // (не на кожен hover) — стабільно, без мутації сигнального стану.
                  // [...spread] повертає новий масив, оригінал не мутується.
                  const sortedSubs = [...subCategories].sort(
                    (a, b) => b.productGroups.length - a.productGroups.length
                  );

                  return { ...cat, subCategories: sortedSubs, expanded: ci === 0 };
                });

                this.categories.set(navCats);
                if (navCats.length > 0) this.activeId.set(navCats[0].id);
                this.isLoading.set(false);
              },
              error: () => { this.error.set('Помилка завантаження груп.'); this.isLoading.set(false); }
            });
          },
          error: () => { this.error.set('Помилка завантаження підкатегорій.'); this.isLoading.set(false); }
        });
      },
      error: () => { this.error.set('Помилка завантаження каталогу.'); this.isLoading.set(false); }
    });
  }

  get activeCategory(): INavCat | null {
    return this.categories().find(c => c.id === this.activeId()) ?? null;
  }

  setActive(id: number): void { this.activeId.set(id); }

  toggleExpand(cat: INavCat): void {
    this.categories.update(list =>
      list.map(c => c.id === cat.id ? { ...c, expanded: !c.expanded } : c)
    );
  }

  navigate(path: (string | number)[]): void {
    this.router.navigate(path.map(String));
  }
}
