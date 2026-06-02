import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import {DataTableComponent, ITableColumn} from '../../../shared/components/data-table/data-table.component';
import {ConfirmDialogComponent} from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import {SubCategoryService} from '../../../core/services/sub-category.service';
import {CategoryService} from '../../../core/services/category.service';
import {ICategory, ISubCategory} from '../../../core/models/models';

@Component({
  selector: 'app-sub-categories',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DataTableComponent, ConfirmDialogComponent],
  templateUrl: './sub-categories.component.html',
  styleUrl: './sub-categories.component.scss'
})
export class SubCategoriesComponent implements OnInit {
  private readonly subCategoryService = inject(SubCategoryService);
  private readonly categoryService    = inject(CategoryService);
  private readonly fb                 = inject(FormBuilder);

  subCategories  = signal<ISubCategory[]>([]);
  categories     = signal<ICategory[]>([]);
  isLoading      = signal(true);
  modalOpen      = signal(false);
  editingId      = signal<number | null>(null);
  deleteId       = signal<number | null>(null);
  filterCategory = signal<number | null>(null);
  error          = signal('');

  totalElements = signal(0);
  currentPage   = signal(0);
  readonly pageSize = 50;

  readonly columns: ITableColumn[] = [
    { key: 'id',         label: 'ID',        width: '60px' },
    { key: 'name',       label: 'Назва' },
    { key: 'categoryId', label: 'Категорія', width: '120px' },
  ];

  form = this.fb.group({
    name:       ['', [Validators.required, Validators.minLength(2)]],
    categoryId: [null as number | null, Validators.required],
  });

  ngOnInit(): void {
    this.categoryService.getAllList().subscribe({
      next: (data) => this.categories.set(data)
    });
    this.load();
  }

  load(page = 0): void {
    this.isLoading.set(true);
    this.subCategoryService.getAll(page, this.pageSize).subscribe({
      next: (data) => {
        this.subCategories.set(data.content);
        this.totalElements.set(data.totalElements);
        this.currentPage.set(data.number);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  filterByCategoryId(catId: number | null): void {
    this.filterCategory.set(catId);
    if (!catId) { this.load(); return; }
    this.isLoading.set(true);
    this.subCategoryService.getByCategoryId(catId).subscribe({
      next: (data) => {
        this.subCategories.set(data);
        this.totalElements.set(data.length);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.form.reset({ categoryId: this.filterCategory() });
    this.error.set('');
    this.modalOpen.set(true);
  }

  openEdit(sub: ISubCategory): void {
    this.editingId.set(sub.id);
    this.form.patchValue({ name: sub.name, categoryId: sub.categoryId });
    this.error.set('');
    this.modalOpen.set(true);
  }

  closeModal(): void { this.modalOpen.set(false); this.form.reset(); }

  save(): void {
    if (this.form.invalid) return;
    const id  = this.editingId();
    const dto = { name: this.form.value.name!, categoryId: this.form.value.categoryId! };

    const req$ = id
      ? this.subCategoryService.update(id, { ...dto })
      : this.subCategoryService.create({ ...dto });

    req$.subscribe({
      next: () => { this.closeModal(); this.filterByCategoryId(this.filterCategory()); },
      error: () => this.error.set('Помилка збереження.')
    });
  }

  confirmDelete(sub: ISubCategory): void { this.deleteId.set(sub.id); }

  doDelete(): void {
    const id = this.deleteId();
    if (!id) return;
    this.subCategoryService.delete(id).subscribe({
      next: () => { this.deleteId.set(null); this.filterByCategoryId(this.filterCategory()); },
      error: () => this.error.set('Помилка видалення.')
    });
  }

  getCategoryName(id: number): string {
    return this.categories().find(c => c.id === id)?.name ?? String(id);
  }

  get isEditing(): boolean { return this.editingId() !== null; }
}
