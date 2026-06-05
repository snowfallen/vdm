import { Component, Input, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { IProduct }        from '../../core/models/models';
import { CartService }     from '../../core/services/cart.service';
import { AuthService }     from '../../core/auth/services/auth.service';
import { FavoriteService } from '../../core/services/favorite.service';
import { Roles }           from '../../core/auth/enums/roles';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss'
})
export class ProductCardComponent {
  @Input() product!: IProduct;
  @Input() badge: 'NEW' | 'TOP' | null = null; // передається з батька

  private readonly cartService     = inject(CartService);
  private readonly favoriteService = inject(FavoriteService);
  private readonly authService     = inject(AuthService);

  added = signal(false);

  get isClient(): boolean { return this.authService.getRoleId() === 2; }
  get isAdmin():  boolean { return this.authService.getRoleId() === Roles.ADMIN; }

  isFav(): boolean { return this.favoriteService.isFavorite(this.product.id); }

  toggleFav(event: Event): void {
    event.preventDefault(); event.stopPropagation();
    if (!this.isClient) return;
    this.favoriteService.toggle(this.product.id).subscribe();
  }

  onAddToCart(event: Event): void {
    event.preventDefault(); event.stopPropagation();
    if (!this.isClient) return;
    this.cartService.addToCart(this.product.id, 1).subscribe({
      next: () => {
        this.added.set(true);
        setTimeout(() => this.added.set(false), 2000);
      }
    });
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('uk-UA', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(price);
  }
}
