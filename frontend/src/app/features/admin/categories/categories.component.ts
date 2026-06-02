import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import {DataTableComponent, ITableColumn} from '../../../shared/components/data-table/data-table.component';
import {ConfirmDialogComponent} from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import {CategoryService} from '../../../core/services/category.service';
import {ICategory} from '../../../core/models/models';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DataTableComponent, ConfirmDialogComponent],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.scss'
})
export class CategoriesComponent implements OnInit {
  private readonly categoryService = inject(CategoryService);
  private readonly fb              = inject(FormBuilder);

  categories = signal<ICategory[]>([]);
  isLoading  = signal(true);
  modalOpen  = signal(false);
  editingId  = signal<number | null>(null);
  deleteId   = signal<number | null>(null);
  error      = signal('');

  readonly columns: ITableColumn[] = [
    { key: 'id',   label: 'ID',    width: '60px' },
    { key: 'name', label: 'Назва' },
  ];

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
  });

  ngOnInit(): void { this.load(); }

  load(): void {
    this.isLoading.set(true);
    this.categoryService.getAllList().subscribe({
      next: (data) => { this.categories.set(data); this.isLoading.set(false); },
      error: () => this.isLoading.set(false)
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.form.reset();
    this.error.set('');
    this.modalOpen.set(true);
  }

  openEdit(cat: ICategory): void {
    this.editingId.set(cat.id);
    this.form.patchValue({ name: cat.name });
    this.error.set('');
    this.modalOpen.set(true);
  }

  closeModal(): void { this.modalOpen.set(false); this.form.reset(); }

  save(): void {
    if (this.form.invalid) return;
    const id  = this.editingId();
    const dto = { name: this.form.value.name! };

    const req$ = id
      ? this.categoryService.update(id, dto)
      : this.categoryService.create(dto);

    req$.subscribe({
      next: () => { this.closeModal(); this.load(); },
      error: () => this.error.set('Помилка збереження.')
    });
  }

  confirmDelete(cat: ICategory): void { this.deleteId.set(cat.id); }

  doDelete(): void {
    const id = this.deleteId();
    if (!id) return;
    this.categoryService.delete(id).subscribe({
      next: () => { this.deleteId.set(null); this.load(); },
      error: () => this.error.set('Помилка видалення.')
    });
  }

  get isEditing(): boolean { return this.editingId() !== null; }
}
