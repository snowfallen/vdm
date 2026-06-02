import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {DataTableComponent, ITableColumn} from '../../../shared/components/data-table/data-table.component';
import {ConfirmDialogComponent} from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import {AttributeAdminService} from '../../../core/services/attribute-admin.service';
import {UnitService} from '../../../core/services/unit.service';
import {AttributeDataType, IAttribute, IAttributeRequest, IPage, IUnit} from '../../../core/models/models';


@Component({
  selector: 'app-attributes',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule,
            DataTableComponent, ConfirmDialogComponent],
  templateUrl: './attributes.component.html',
  styleUrl: './attributes.component.scss'
})
export class AttributesComponent implements OnInit {
  private readonly attributeService = inject(AttributeAdminService);
  private readonly unitService      = inject(UnitService);
  private readonly fb               = inject(FormBuilder);

  attributes  = signal<IAttribute[]>([]);
  units       = signal<IUnit[]>([]);
  isLoading   = signal(true);
  modalOpen   = signal(false);
  editingId   = signal<number | null>(null);
  deleteId    = signal<number | null>(null);
  error       = signal('');
  currentPage = signal(0);
  totalElements = signal(0);
  readonly pageSize = 50;

  readonly dataTypes: { value: AttributeDataType; label: string }[] = [
    { value: 'DICT',   label: 'Список (DICT)' },
    { value: 'TEXT',   label: 'Текст (TEXT)' },
    { value: 'NUMBER', label: 'Число (NUMBER)' },
  ];

  readonly columns: ITableColumn[] = [
    { key: 'id',         label: 'ID',       width: '60px' },
    { key: 'name',       label: 'Назва' },
    { key: 'dataType',   label: 'Тип',      width: '120px' },
    { key: 'unitSymbol', label: 'Одиниця',  width: '100px' },
  ];

  form = this.fb.group({
    name:     ['', [Validators.required, Validators.maxLength(255)]],
    dataType: ['DICT' as AttributeDataType, Validators.required],
    unitId:   [null as number | null],
  });

  get selectedDataType(): AttributeDataType {
    return this.form.get('dataType')?.value as AttributeDataType;
  }

  ngOnInit(): void {
    this.loadUnits();
    this.loadAttributes();
  }

  loadUnits(): void {
    this.unitService.getAll().subscribe({
      next: (data) => this.units.set(data)
    });
  }

  loadAttributes(page = 0): void {
    this.isLoading.set(true);
    this.attributeService.getAll(page, this.pageSize).subscribe({
      next: (data: IPage<IAttribute>) => {
        this.attributes.set(data.content);
        this.totalElements.set(data.totalElements);
        this.currentPage.set(data.number);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.form.reset({ dataType: 'DICT', unitId: null });
    this.error.set('');
    this.modalOpen.set(true);
  }

  openEdit(attr: IAttribute): void {
    this.editingId.set(attr.id);
    this.form.patchValue({
      name:     attr.name,
      dataType: attr.dataType,
      unitId:   attr.unitId,
    });
    this.error.set('');
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.modalOpen.set(false);
    this.form.reset();
  }

  save(): void {
    if (this.form.invalid) return;
    const v = this.form.value;
    const dto: IAttributeRequest = {
      name:     v.name!,
      dataType: v.dataType as AttributeDataType,
      unitId:   v.dataType === 'NUMBER' ? v.unitId : null,
    };
    const id = this.editingId();

    const request$ = id
      ? this.attributeService.update(id, dto)
      : this.attributeService.create(dto);

    request$.subscribe({
      next: () => { this.closeModal(); this.loadAttributes(this.currentPage()); },
      error: () => this.error.set('Помилка збереження.')
    });
  }

  confirmDelete(attr: IAttribute): void { this.deleteId.set(attr.id); }

  doDelete(): void {
    const id = this.deleteId();
    if (!id) return;
    this.attributeService.delete(id).subscribe({
      next: () => { this.deleteId.set(null); this.loadAttributes(this.currentPage()); },
      error: () => this.error.set('Помилка видалення.')
    });
  }

  get isEditing(): boolean { return this.editingId() !== null; }
}
