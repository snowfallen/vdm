import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService, ICart, ICartItemResponse } from '../../core/services/cart.service';
import { OrderService, IPlaceOrderRequest } from '../../core/services/order.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {
  private readonly cartService  = inject(CartService);
  private readonly orderService = inject(OrderService);
  private readonly router       = inject(Router);

  cart         = signal<ICart | null>(null);
  isLoading    = signal(true);
  isOrdering   = signal(false);
  error        = signal('');
  orderError   = signal('');
  showCheckout = signal(false);   // блок підтвердження замовлення

  // Поля для форми оформлення (необов'язкові — беруться з профілю якщо порожні)
  comment = signal('');

  ngOnInit(): void { this.load(); }

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

  // Відкрити блок підтвердження
  openCheckout(): void {
    this.showCheckout.set(true);
    this.orderError.set('');
  }

  closeCheckout(): void {
    this.showCheckout.set(false);
  }

  // Оформити замовлення
  placeOrder(): void {
    this.isOrdering.set(true);
    this.orderError.set('');

    const dto: IPlaceOrderRequest = {
      comment: this.comment() || undefined
      // deliveryCity etc. — беруться автоматично з профілю клієнта на бекенді
    };

    this.orderService.placeOrder(dto).subscribe({
      next: (order) => {
        this.isOrdering.set(false);
        // Переходимо на кабінет і відразу в замовлення
        this.router.navigate(['/cabinet'], { queryParams: { tab: 'orders', orderId: order.id } });
      },
      error: () => {
        this.isOrdering.set(false);
        this.orderError.set('Помилка оформлення замовлення. Перевірте адресу доставки в кабінеті.');
      }
    });
  }

  get items(): ICartItemResponse[] { return this.cart()?.cartItems ?? []; }
  get total(): number { return this.cart()?.totalPrice ?? 0; }
  get isEmpty(): boolean { return this.items.length === 0; }
}
