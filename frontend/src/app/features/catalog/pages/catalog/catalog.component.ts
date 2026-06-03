import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ProductService }      from '../../../../core/services/product.service';
import { ProductGroupService } from '../../../../core/services/product-group.service';
import { SubCategoryService }  from '../../../../core/services/sub-category.service';
import { CategoryService }     from '../../../../core/services/category.service';
import { environment }         from '../../../../../environments/environment';
import {
  ICategory, IProduct, IProductGroup, ISubCategory
} from '../../../../core/models/models';
import { ProductCardComponent } from '../../../product-card/product-card.component';

type ViewMode  = 'grid' | 'list';
type SortOption = 'default' | 'price_asc' | 'price_desc' | 'name_asc';

interface IBreadcrumb { label: string; path?: string[]; }

interface IFilterAttribute {
  attributeId: number;
  attributeName: string;
  dataType: 'DICT' | 'TEXT' | 'NUMBER';
  unitSymbol: string | null;
  values: string[];
  minValue: string | null;
  maxValue: string | null;
}
interface ISubCategoryFilters {
  subCategoryId: number;
  filters: IFilterAttribute[];
}

// Формат запиту до POST /products/filter
interface IAttributeFilterDto {
  attributeId: number;
  values?: string[];
  minValue?: string;
  maxValue?: string;
}
interface IProductFilterRequest {
  subCategoryId?: number;
  productGroupId?: number;
  attributes: IAttributeFilterDto[];
}

// Відповідь Spring Page<>
interface IPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ProductCardComponent],
  templateUrl: './catalog.component.html',
  styleUrl:  './catalog.component.scss'
})
export class CatalogComponent implements OnInit {
  private readonly route               = inject(ActivatedRoute);
  private readonly router              = inject(Router);
  private readonly http                = inject(HttpClient);
  private readonly productService      = inject(ProductService);
  private readonly productGroupService = inject(ProductGroupService);
  private readonly subCategoryService  = inject(SubCategoryService);
  private readonly categoryService     = inject(CategoryService);

  allProducts   = signal<IProduct[]>([]);   // всі без фільтрів
  products      = signal<IProduct[]>([]);   // відфільтровані + відсортовані
  productGroups = signal<IProductGroup[]>([]);
  subCategories = signal<ISubCategory[]>([]);
  subCategory   = signal<ISubCategory | null>(null);
  category      = signal<ICategory | null>(null);
  currentGroup  = signal<IProductGroup | null>(null);
  breadcrumbs   = signal<IBreadcrumb[]>([]);

  isLoading      = signal(true);
  filtersLoading = signal(false);
  filterLoading  = signal(false);  // окремий лоадер для застосування фільтрів
  viewMode       = signal<ViewMode>('grid');
  sortOption     = signal<SortOption>('default');
  filtersOpen    = signal(true);
  error          = signal('');

  availableFilters = signal<IFilterAttribute[]>([]);
  activeFilters    = signal<Map<number, Set<string>>>(new Map());
  rangeFilters     = signal<Map<number, { min: string; max: string }>>(new Map());

  categoryId     = signal<number | null>(null);
  subCategoryId  = signal<number | null>(null);
  productGroupId = signal<number | null>(null);

  readonly sortOptions: { value: SortOption; label: string }[] = [
    { value: 'default',    label: 'За замовчуванням' },
    { value: 'price_asc',  label: 'Ціна: від дешевих' },
    { value: 'price_desc', label: 'Ціна: від дорогих' },
    { value: 'name_asc',   label: 'Назва А→Я' },
  ];

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.categoryId.set(params['categoryId']      ? +params['categoryId']      : null);
      this.subCategoryId.set(params['subCategoryId'] ? +params['subCategoryId']  : null);
      this.productGroupId.set(params['productGroupId']? +params['productGroupId']: null);

      this.activeFilters.set(new Map());
      this.rangeFilters.set(new Map());
      this.sortOption.set('default');
      this.availableFilters.set([]);

      this.load();
    });
  }

  load(): void {
    this.isLoading.set(true);
    this.error.set('');

    const pgId  = this.productGroupId();
    const subId = this.subCategoryId();
    const catId = this.categoryId();

    if (pgId) {
      forkJoin({
        group:    this.productGroupService.getById(pgId),
        products: this.productService.getByProductGroupId(pgId),
      }).subscribe({
        next: ({ group, products }) => {
          this.currentGroup.set(group);
          this.allProducts.set(products);
          this.products.set(this.sortProducts(products));
          this.productGroups.set([]);
          this.subCategories.set([]);
          this.buildBreadcrumbs();
          this.loadFiltersForGroup(pgId);
          this.isLoading.set(false);
        },
        error: () => { this.error.set('Помилка завантаження.'); this.isLoading.set(false); }
      });

    } else if (subId) {
      forkJoin({
        subCategory:   this.subCategoryService.getById(subId),
        productGroups: this.productGroupService.getBySubCategoryId(subId),
      }).subscribe({
        next: ({ subCategory, productGroups }) => {
          this.subCategory.set(subCategory);
          this.productGroups.set(productGroups);
          this.currentGroup.set(null);
          this.subCategories.set([]);

          if (productGroups.length > 0) {
            forkJoin(productGroups.map(g =>
              this.productService.getByProductGroupId(g.id)
            )).subscribe({
              next: (all) => {
                const flat = all.flat();
                this.allProducts.set(flat);
                this.products.set(this.sortProducts(flat));
                this.buildBreadcrumbs();
                this.loadFiltersForSubCategory(subId);
                this.isLoading.set(false);
              },
              error: () => { this.allProducts.set([]); this.products.set([]); this.isLoading.set(false); }
            });
          } else {
            this.allProducts.set([]);
            this.products.set([]);
            this.buildBreadcrumbs();
            this.isLoading.set(false);
          }
        },
        error: () => { this.error.set('Помилка завантаження.'); this.isLoading.set(false); }
      });

      if (catId) {
        this.categoryService.getById(catId).subscribe({
          next: (cat) => { this.category.set(cat); this.buildBreadcrumbs(); }
        });
      }

    } else if (catId) {
      forkJoin({
        category:      this.categoryService.getById(catId),
        subCategories: this.subCategoryService.getByCategoryId(catId),
      }).subscribe({
        next: ({ category, subCategories }) => {
          this.category.set(category);
          this.subCategories.set(subCategories);
          this.subCategory.set(null);
          this.currentGroup.set(null);
          this.productGroups.set([]);
          this.allProducts.set([]);
          this.products.set([]);
          this.buildBreadcrumbs();
          this.isLoading.set(false);
        },
        error: () => { this.error.set('Помилка завантаження.'); this.isLoading.set(false); }
      });

    } else {
      this.error.set('Невірний маршрут каталогу.');
      this.isLoading.set(false);
    }
  }

  // ---- Завантаження фільтрів ----

  loadFiltersForSubCategory(subId: number): void {
    this.filtersLoading.set(true);
    this.http.get<ISubCategoryFilters>(
      `${environment.rootUrl}attributes/sub-category/${subId}/filters`
    ).subscribe({
      next: (data) => {
        this.availableFilters.set(this.filterUseful(data.filters ?? []));
        this.filtersLoading.set(false);
      },
      error: () => this.filtersLoading.set(false)
    });
  }

  loadFiltersForGroup(pgId: number): void {
    this.filtersLoading.set(true);
    this.http.get<ISubCategoryFilters>(
      `${environment.rootUrl}attributes/product-group/${pgId}/filters`
    ).subscribe({
      next: (data) => {
        this.availableFilters.set(this.filterUseful(data.filters ?? []));
        this.filtersLoading.set(false);
      },
      error: () => this.filtersLoading.set(false)
    });
  }

  private filterUseful(filters: IFilterAttribute[]): IFilterAttribute[] {
    return filters.filter(f =>
      (f.dataType === 'DICT' && f.values?.length > 0) ||
      (f.dataType === 'NUMBER' && f.minValue != null && f.maxValue != null) ||
      (f.dataType === 'TEXT' && f.values?.length > 0)
    );
  }

  // ---- Керування фільтрами ----

  toggleFilter(attributeId: number, value: string): void {
    const map = new Map(this.activeFilters());
    if (!map.has(attributeId)) map.set(attributeId, new Set());
    const set = new Set(map.get(attributeId)!);
    if (set.has(value)) { set.delete(value); } else { set.add(value); }
    if (set.size === 0) { map.delete(attributeId); } else { map.set(attributeId, set); }
    this.activeFilters.set(map);
    this.applyFilters();
  }

  isFilterActive(attributeId: number, value: string): boolean {
    return this.activeFilters().get(attributeId)?.has(value) ?? false;
  }

  updateRange(attributeId: number, type: 'min' | 'max', value: string): void {
    const map = new Map(this.rangeFilters());
    const cur = map.get(attributeId) ?? { min: '', max: '' };
    map.set(attributeId, { ...cur, [type]: value });
    this.rangeFilters.set(map);
    this.applyFilters();
  }

  getRangeValue(attributeId: number, type: 'min' | 'max'): string {
    return this.rangeFilters().get(attributeId)?.[type] ?? '';
  }

  clearFilters(): void {
    this.activeFilters.set(new Map());
    this.rangeFilters.set(new Map());
    this.applyFilters();
  }

  get hasActiveFilters(): boolean {
    const hasDict = this.activeFilters().size > 0;
    const hasRange = [...this.rangeFilters().values()].some(v => v.min || v.max);
    return hasDict || hasRange;
  }

  get activeFilterCount(): number {
    let count = this.activeFilters().size;
    this.rangeFilters().forEach(v => { if (v.min || v.max) count++; });
    return count;
  }

  // ---- ОСНОВНА ЛОГІКА ФІЛЬТРАЦІЇ ----

  applyFilters(): void {
    // Якщо немає активних фільтрів — показуємо все з сортуванням
    if (!this.hasActiveFilters) {
      this.products.set(this.sortProducts(this.allProducts()));
      return;
    }

    // Будуємо запит до POST /products/filter
    const attributes: IAttributeFilterDto[] = [];

    // DICT фільтри
    this.activeFilters().forEach((values, attributeId) => {
      if (values.size > 0) {
        attributes.push({
          attributeId,
          values: [...values]
        });
      }
    });

    // NUMBER range фільтри
    this.rangeFilters().forEach((range, attributeId) => {
      if (range.min || range.max) {
        attributes.push({
          attributeId,
          minValue: range.min || undefined,
          maxValue: range.max || undefined
        });
      }
    });

    if (attributes.length === 0) {
      this.products.set(this.sortProducts(this.allProducts()));
      return;
    }

    const body: IProductFilterRequest = { attributes };

    // Встановлюємо скоп
    if (this.productGroupId()) {
      body.productGroupId = this.productGroupId()!;
    } else if (this.subCategoryId()) {
      body.subCategoryId = this.subCategoryId()!;
    }

    this.filterLoading.set(true);
    this.http.post<IPage<IProduct>>(
      `${environment.rootUrl}products/filter?size=100&sort=id`,
      body
    ).subscribe({
      next: (page) => {
        this.products.set(this.sortProducts(page.content));
        this.filterLoading.set(false);
      },
      error: () => {
        // При помилці фільтрації — показуємо всі товари
        this.products.set(this.sortProducts(this.allProducts()));
        this.filterLoading.set(false);
      }
    });
  }

  onSortChange(value: SortOption): void {
    this.sortOption.set(value);
    // Просто пересортовуємо вже відфільтровані товари
    this.products.set(this.sortProducts(this.products()));
  }

  private sortProducts(list: IProduct[]): IProduct[] {
    const copy = [...list];
    const sort = this.sortOption();
    if (sort === 'price_asc')  return copy.sort((a, b) => a.price - b.price);
    if (sort === 'price_desc') return copy.sort((a, b) => b.price - a.price);
    if (sort === 'name_asc')   return copy.sort((a, b) => a.name.localeCompare(b.name, 'uk'));
    return copy;
  }

  // ---- Навігація ----

  goToGroup(group: IProductGroup): void {
    const catId = this.categoryId();
    const subId = this.subCategoryId();
    if (catId && subId) {
      this.router.navigate(['/catalog', catId, subId, group.id]);
    } else {
      this.router.navigate(['/catalog/group', group.id]);
    }
  }

  goToSubCategory(sub: ISubCategory): void {
    const catId = this.categoryId();
    if (catId) this.router.navigate(['/catalog', catId, sub.id]);
  }

  buildBreadcrumbs(): void {
    const crumbs: IBreadcrumb[] = [{ label: 'Головна', path: ['/'] }];
    const cat   = this.category();
    const sub   = this.subCategory();
    const grp   = this.currentGroup();
    const catId = this.categoryId();
    const subId = this.subCategoryId();

    if (cat && catId) crumbs.push({ label: cat.name, path: ['/catalog', String(catId)] });
    if (sub && catId && subId) crumbs.push({ label: sub.name, path: ['/catalog', String(catId), String(subId)] });
    if (grp) crumbs.push({ label: grp.name });

    this.breadcrumbs.set(crumbs);
  }

  get pageTitle(): string {
    if (this.currentGroup()) return this.currentGroup()!.name;
    if (this.subCategory())  return this.subCategory()!.name;
    if (this.category())     return this.category()!.name;
    return 'Каталог';
  }

  get hasGroups(): boolean {
    return !this.productGroupId() && !!this.subCategoryId() && this.productGroups().length > 0;
  }

  get hasSubCategories(): boolean {
    return !this.subCategoryId() && !this.productGroupId() && this.subCategories().length > 0;
  }

  toggleView(mode: ViewMode): void { this.viewMode.set(mode); }
  toggleFilters(): void { this.filtersOpen.update(v => !v); }
}
