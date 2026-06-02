import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import {DataTableComponent, ITableColumn} from '../../../shared/components/data-table/data-table.component';
import {ConfirmDialogComponent} from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import {AttributeOptionService} from '../../../core/services/attribute-option.service';
import {AttributeAdminService} from '../../../core/services/attribute-admin.service';
import {IAttribute, IAttributeOption, IAttributeOptionRequest} from '../../../core/models/models';


@Component({
  selector: 'app-attribute-options',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DataTableComponent, ConfirmDialogComponent],
  templateUrl: './attribute-options.component.html',
  styleUrl: './attribute-options.component.scss'
})
export class AttributeOptionsComponent implements OnInit {
  private readonly optionService    = inject(AttributeOptionService);
  private readonly attributeService = inject(AttributeAdminService);
  private readonly fb               = inject(FormBuilder);

  options          = signal<IAttributeOption[]>([]);
  attributes       = signal<IAttribute[]>([]);
  isLoading        = signal(false);
  modalOpen        = signal(false);
  editingId        = signal<number | null>(null);
  deleteId         = signal<number | null>(null);
  selectedAttrId   = signal<number | null>(null);
  error            = signal('');

  readonly columns: ITableColumn[] = [
    { key: 'id',            label: 'ID',        width: '60px' },
    { key: 'value',         label: 'Значення' },
    { key: 'attributeName', label: 'Атрибут' },
  ];

  form = this.fb.group({
    attributeId: [null as number | null, Validators.required],
    value:       ['', [Validators.required, Validators.maxLength(255)]],
  });

  ngOnInit(): void {
    // Завантажуємо тільки DICT атрибути
    this.attributeService.getAllList().subscribe({
      next: (data) => this.attributes.set(data.filter(a => a.dataType === 'DICT'))
    });
  }

  loadByAttribute(attrId: number): void {
    this.selectedAttrId.set(attrId);
    this.isLoading.set(true);
    this.optionService.getAllByAttributeId(attrId).subscribe({
      next: (data) => { this.options.set(data); this.isLoading.set(false); },
      error: () => this.isLoading.set(false)
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.form.reset({ attributeId: this.selectedAttrId() });
    this.error.set('');
    this.modalOpen.set(true);
  }

  openEdit(opt: IAttributeOption): void {
    this.editingId.set(opt.id);
    this.form.patchValue({ attributeId: opt.attributeId, value: opt.value });
    this.error.set('');
    this.modalOpen.set(true);
  }

  closeModal(): void { this.modalOpen.set(false); this.form.reset(); }

  save(): void {
    if (this.form.invalid) return;
    const id  = this.editingId();
    const dto: IAttributeOptionRequest = {
      attributeId: this.form.value.attributeId!,
      value:       this.form.value.value!,
    };

    const req$ = id
      ? this.optionService.update(id, dto)
      : this.optionService.create(dto);

    req$.subscribe({
      next: () => {
        this.closeModal();
        const attrId = this.selectedAttrId();
        if (attrId) this.loadByAttribute(attrId);
      },
      error: () => this.error.set('Помилка збереження.')
    });
  }

  confirmDelete(opt: IAttributeOption): void { this.deleteId.set(opt.id); }

  doDelete(): void {
    const id = this.deleteId();
    if (!id) return;
    this.optionService.delete(id).subscribe({
      next: () => {
        this.deleteId.set(null);
        const attrId = this.selectedAttrId();
        if (attrId) this.loadByAttribute(attrId);
      },
      error: () => this.error.set('Помилка видалення.')
    });
  }

  get selectedAttrName(): string {
    return this.attributes().find(a => a.id === this.selectedAttrId())?.name ?? '';
  }

  get isEditing(): boolean { return this.editingId() !== null; }
}
