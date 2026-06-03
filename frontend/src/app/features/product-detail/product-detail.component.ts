import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import {ProductCardComponent} from '../product-card/product-card.component';
import {ProductService} from '../../core/services/product.service';
import {ProductAttributeService} from '../../core/services/product-attribute.service';
import {ProductGroupService} from '../../core/services/product-group.service';
import {CartService} from '../../core/services/cart.service';
import {AuthService} from '../../core/auth/services/auth.service';
import {IProduct, IProductAttribute, IProductGroup} from '../../core/models/models';
import {Roles} from '../../core/auth/enums/roles';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ProductCardComponent],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {
  private readonly route                  = inject(ActivatedRoute);
  private readonly productService         = inject(ProductService);
  private readonly productAttributeService= inject(ProductAttributeService);
  private readonly productGroupService    = inject(ProductGroupService);
  private readonly cartService            = inject(CartService);
  private readonly authService            = inject(AuthService);

  product       = signal<IProduct | null>(null);
  attributes    = signal<IProductAttribute[]>([]);
  related       = signal<IProduct[]>([]);
  productGroup  = signal<IProductGroup | null>(null);
  isLoading     = signal(true);
  quantity      = signal(1);
  addedToCart   = signal(false);
  cartError     = signal('');

  // Роль
  get isAdmin():  boolean { return this.authService.getRoleId() === Roles.ADMIN; }
  get isClient(): boolean { return this.authService.getRoleId() === 2; }
  get isGuest():  boolean { return !this.authService.getRoleId(); }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.load(id);
    });
  }

  load(id: number): void {
    this.isLoading.set(true);
    this.addedToCart.set(false);
    this.cartError.set('');

    forkJoin({
      product:    this.productService.getById(id),
      attributes: this.productAttributeService.getAllByProductId(id),
    }).subscribe({
      next: ({ product, attributes }) => {
        this.product.set(product);
        this.attributes.set(attributes);

        // Завантажуємо групу і схожі товари
        this.productGroupService.getById(product.productGroupId).subscribe({
          next: (group) => {
            this.productGroup.set(group);
          }
        });

        this.productService.getByProductGroupId(product.productGroupId).subscribe({
          next: (all) => {
            // Схожі — всі з тієї ж групи крім поточного (до 4)
            this.related.set(all.filter(p => p.id !== id).slice(0, 4));
          }
        });

        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  addToCart(): void {
    const product = this.product();
    if (!product) return;

    this.cartService.addToCart(product.id, this.quantity()).subscribe({
      next: () => {
        this.addedToCart.set(true);
        this.cartError.set('');
        setTimeout(() => this.addedToCart.set(false), 3000);
      },
      error: () => this.cartError.set('Помилка додавання до кошика.')
    });
  }

  changeQuantity(delta: number): void {
    const newVal = Math.max(1, this.quantity() + delta);
    this.quantity.set(newVal);
  }

  // Групуємо атрибути по типу для відображення
  get dictAttributes(): IProductAttribute[] {
    return this.attributes().filter(a => a.dataType === 'DICT');
  }

  get numberAttributes(): IProductAttribute[] {
    return this.attributes().filter(a => a.dataType === 'NUMBER');
  }

  get textAttributes(): IProductAttribute[] {
    return this.attributes().filter(a => a.dataType === 'TEXT');
  }
}
