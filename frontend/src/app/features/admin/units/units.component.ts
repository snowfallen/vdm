import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import {DataTableComponent, ITableColumn} from '../../../shared/components/data-table/data-table.component';
import {ConfirmDialogComponent} from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import {UnitService} from '../../../core/services/unit.service';
import {IUnit, IUnitRequest} from '../../../core/models/models';

@Component({
  selector: 'app-units',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, DataTableComponent, ConfirmDialogComponent],
  templateUrl: './units.component.html',
  styleUrl: './units.component.scss'
})
export class UnitsComponent implements OnInit {
  private readonly unitService = inject(UnitService);
  private readonly fb          = inject(FormBuilder);

  units     = signal<IUnit[]>([]);
  isLoading = signal(true);
  modalOpen = signal(false);
  editingId = signal<number | null>(null);
  deleteId  = signal<number | null>(null);
  error     = signal('');

  readonly columns: ITableColumn[] = [
    { key: 'id',          label: 'ID',      width: '60px' },
    { key: 'symbol',      label: 'Символ',  width: '120px' },
    { key: 'description', label: 'Опис' },
  ];

  form = this.fb.group({
    symbol:      ['', [Validators.required, Validators.maxLength(20)]],
    description: [''],
  });

  ngOnInit(): void {
    this.loadUnits();
  }

  loadUnits(): void {
    this.isLoading.set(true);
    this.unitService.getAll().subscribe({
      next: (data) => {
        this.units.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.form.reset();
    this.error.set('');
    this.modalOpen.set(true);
  }

  openEdit(unit: IUnit): void {
    this.editingId.set(unit.id);
    this.form.patchValue({ symbol: unit.symbol, description: unit.description });
    this.error.set('');
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.modalOpen.set(false);
    this.form.reset();
  }

  save(): void {
    if (this.form.invalid) return;
    const dto = this.form.value as IUnitRequest;
    const id  = this.editingId();

    const request$ = id
      ? this.unitService.update(id, dto)
      : this.unitService.create(dto);

    request$.subscribe({
      next: () => { this.closeModal(); this.loadUnits(); },
      error: () => this.error.set('Помилка збереження. Перевірте дані.')
    });
  }

  confirmDelete(unit: IUnit): void {
    this.deleteId.set(unit.id);
  }

  doDelete(): void {
    const id = this.deleteId();
    if (!id) return;
    this.unitService.delete(id).subscribe({
      next: () => { this.deleteId.set(null); this.loadUnits(); },
      error: () => this.error.set('Помилка видалення.')
    });
  }

  get isEditing(): boolean { return this.editingId() !== null; }
}
