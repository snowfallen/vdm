import { Component, Input, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {IProduct} from '../../core/models/models';
import {CartService} from '../../core/services/cart.service';
import {AuthService} from '../../core/auth/services/auth.service';
import {Roles} from '../../core/auth/enums/roles';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss'
})
export class ProductCardComponent {
  @Input() product!: IProduct;

  private readonly cartService  = inject(CartService);
  private readonly authService  = inject(AuthService);

  added = signal(false);

  get isClient(): boolean { return this.authService.getRoleId() === 2; }
  get isAdmin():  boolean { return this.authService.getRoleId() === Roles.ADMIN; }

  onAddToCart(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (!this.isClient) return;

    this.cartService.addToCart(this.product.id, 1).subscribe({
      next: () => {
        this.added.set(true);
        setTimeout(() => this.added.set(false), 2000);
      }
    });
  }
}
