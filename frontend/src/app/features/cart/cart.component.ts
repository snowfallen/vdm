import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {CartService, ICart, ICartItemResponse} from '../../core/services/cart.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  private readonly cartService = inject(CartService);

  cart      = signal<ICart | null>(null);
  isLoading = signal(true);
  error     = signal('');

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.cartService.getCart().subscribe({
      next:  (c) => { this.cart.set(c); this.isLoading.set(false); },
      error: () => { this.error.set('Помилка завантаження кошика.'); this.isLoading.set(false); }
    });
  }

  updateQty(item: ICartItemResponse, delta: number): void {
    const newQty = Math.max(1, item.quantity + delta);
    if (newQty === item.quantity) return;
    this.cartService.updateQuantity(item.id, newQty).subscribe({
      next: (c) => this.cart.set(c)
    });
  }

  setQty(item: ICartItemResponse, event: Event): void {
    const val = +(event.target as HTMLInputElement).value;
    if (val >= 1 && val !== item.quantity) {
      this.cartService.updateQuantity(item.id, val).subscribe({
        next: (c) => this.cart.set(c)
      });
    }
  }

  remove(item: ICartItemResponse): void {
    this.cartService.removeItem(item.id).subscribe({
      next: (c) => this.cart.set(c)
    });
  }

  clear(): void {
    this.cartService.clearCart().subscribe({
      next: () => this.cart.set({ ...this.cart()!, cartItems: [], totalPrice: 0 })
    });
  }

  get items(): ICartItemResponse[] {
    return this.cart()?.cartItems ?? [];
  }

  get total(): number {
    return this.cart()?.totalPrice ?? 0;
  }

  get isEmpty(): boolean {
    return this.items.length === 0;
  }
}
