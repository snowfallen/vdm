import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import {DataTableComponent, ITableColumn} from '../../../shared/components/data-table/data-table.component';
import {ConfirmDialogComponent} from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import {ProductService} from '../../../core/services/product.service';
import {ProductAttributeService} from '../../../core/services/product-attribute.service';
import {AttributeAdminService} from '../../../core/services/attribute-admin.service';
import {AttributeOptionService} from '../../../core/services/attribute-option.service';
import {ProductGroupService} from '../../../core/services/product-group.service';
import {
  AttributeDataType,
  IAttribute,
  IAttributeOption, IPage,
  IProduct,
  IProductAttribute, IProductAttributeRequest,
  IProductGroup
} from '../../../core/models/models';

type Tab = 'info' | 'attributes';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DataTableComponent, ConfirmDialogComponent],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent implements OnInit {
  private readonly productService          = inject(ProductService);
  private readonly productAttributeService = inject(ProductAttributeService);
  private readonly attributeService        = inject(AttributeAdminService);
  private readonly attributeOptionService  = inject(AttributeOptionService);
  private readonly productGroupService     = inject(ProductGroupService);
  private readonly fb                      = inject(FormBuilder);

  // Список товарів
  products      = signal<IProduct[]>([]);
  isLoading     = signal(true);
  totalElements = signal(0);
  currentPage   = signal(0);
  readonly pageSize = 20;

  // Модальне вікно
  modalOpen  = signal(false);
  activeTab  = signal<Tab>('info');
  editingId  = signal<number | null>(null);
  deleteId   = signal<number | null>(null);
  error      = signal('');

  // Атрибути для вкладки
  productAttributes = signal<IProductAttribute[]>([]);
  allAttributes     = signal<IAttribute[]>([]);
  attrOptions       = signal<IAttributeOption[]>([]);
  addAttrOpen       = signal(false);
  deleteAttrId      = signal<number | null>(null);

  // ProductGroups для select
  productGroups = signal<IProductGroup[]>([]);

  readonly columns: ITableColumn[] = [
    { key: 'id',              label: 'ID',       width: '60px' },
    { key: 'name',            label: 'Назва' },
    { key: 'price',           label: 'Ціна',     width: '100px' },
    { key: 'productGroupId',  label: 'Група',    width: '80px' },
  ];

  readonly attrColumns: ITableColumn[] = [
    { key: 'attributeName', label: 'Атрибут' },
    { key: 'value',         label: 'Значення' },
    { key: 'dataType',      label: 'Тип',      width: '90px' },
    { key: 'unitSymbol',    label: 'Одиниця',  width: '80px' },
  ];

  form = this.fb.group({
    name:           ['', [Validators.required, Validators.minLength(2)]],
    price:          [null as number | null, [Validators.required, Validators.min(0)]],
    productGroupId: [null as number | null, Validators.required],
  });

  attrForm = this.fb.group({
    attributeId: [null as number | null, Validators.required],
    optionId:    [null as number | null],
    customValue: [''],
  });

  get selectedAttr(): IAttribute | undefined {
    const id = this.attrForm.get('attributeId')?.value;
    return this.allAttributes().find(a => a.id === id);
  }

  get selectedAttrType(): AttributeDataType | null {
    return this.selectedAttr?.dataType ?? null;
  }

  ngOnInit(): void {
    this.load();
    this.attributeService.getAllList().subscribe({
      next: (data) => this.allAttributes.set(data)
    });
    this.productGroupService.getAll(0, 1000).subscribe({
      next: (data) => this.productGroups.set(data.content)
    });
  }

  load(page = 0): void {
    this.isLoading.set(true);
    this.productService.getAll(page, this.pageSize).subscribe({
      next: (data: IPage<IProduct>) => {
        this.products.set(data.content);
        this.totalElements.set(data.totalElements);
        this.currentPage.set(data.number);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.form.reset();
    this.productAttributes.set([]);
    this.activeTab.set('info');
    this.error.set('');
    this.modalOpen.set(true);
  }

  openEdit(product: IProduct): void {
    this.editingId.set(product.id);
    this.form.patchValue({
      name:           product.name,
      price:          product.price,
      productGroupId: product.productGroupId,
    });
    this.loadProductAttributes(product.id);
    this.activeTab.set('info');
    this.error.set('');
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.modalOpen.set(false);
    this.form.reset();
    this.attrForm.reset();
    this.productAttributes.set([]);
    this.addAttrOpen.set(false);
  }

  save(): void {
    if (this.form.invalid) return;
    const id  = this.editingId();
    const dto = {
      name:           this.form.value.name!,
      price:          this.form.value.price!,
      productGroupId: this.form.value.productGroupId!,
    };

    const req$ = id
      ? this.productService.update(id, dto)
      : this.productService.create(dto);

    req$.subscribe({
      next: (saved) => {
        if (!id) {
          // При створенні — залишаємо модалку відкритою для атрибутів
          this.editingId.set(saved.id);
          this.activeTab.set('attributes');
        } else {
          this.closeModal();
          this.load(this.currentPage());
        }
      },
      error: () => this.error.set('Помилка збереження.')
    });
  }

  // ---- Атрибути товару ----

  loadProductAttributes(productId: number): void {
    this.productAttributeService.getAllByProductId(productId).subscribe({
      next: (data) => this.productAttributes.set(data)
    });
  }

  onAttrChange(attrId: number): void {
    this.attrForm.patchValue({ optionId: null, customValue: '' });
    const attr = this.allAttributes().find(a => a.id === attrId);
    if (attr?.dataType === 'DICT') {
      this.attributeOptionService.getAllByAttributeId(attrId).subscribe({
        next: (opts) => this.attrOptions.set(opts)
      });
    } else {
      this.attrOptions.set([]);
    }
  }

  addAttribute(): void {
    const productId = this.editingId();
    if (!productId || this.attrForm.invalid) return;

    const v = this.attrForm.value;
    const dto: IProductAttributeRequest = {
      productId,
      attributeId: v.attributeId!,
      optionId:    v.optionId ?? null,
      customValue: v.customValue ?? null,
    };

    this.productAttributeService.create(dto).subscribe({
      next: () => {
        this.addAttrOpen.set(false);
        this.attrForm.reset();
        this.loadProductAttributes(productId);
      },
      error: () => this.error.set('Помилка додавання атрибуту.')
    });
  }

  confirmDeleteAttr(attr: IProductAttribute): void {
    this.deleteAttrId.set(attr.id);
  }

  doDeleteAttr(): void {
    const id = this.deleteAttrId();
    if (!id) return;
    this.productAttributeService.delete(id).subscribe({
      next: () => {
        this.deleteAttrId.set(null);
        const productId = this.editingId();
        if (productId) this.loadProductAttributes(productId);
      }
    });
  }

  confirmDelete(product: IProduct): void { this.deleteId.set(product.id); }

  doDelete(): void {
    const id = this.deleteId();
    if (!id) return;
    this.productService.delete(id).subscribe({
      next: () => { this.deleteId.set(null); this.load(this.currentPage()); },
      error: () => this.error.set('Помилка видалення.')
    });
  }

  get isEditing(): boolean { return this.editingId() !== null; }
}
