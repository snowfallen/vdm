import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ICartItemResponse {
  id: number;
  productId: number;
  productName: string;
  productImageUrl: string | null;
  pricePerUnit: number;
  quantity: number;
  subTotal: number;
}

export interface ICart {
  id: number;
  userId: number;
  cartItems: ICartItemResponse[];
  totalPrice: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly http   = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}cart`;

  // Реактивний сигнал для хедера
  readonly cartCount = signal(0);

  private err = (e: unknown) => throwError(() => e);

  getCart(): Observable<ICart> {
    return this.http.get<ICart>(this.apiUrl).pipe(
      tap(c => this.cartCount.set(c.cartItems?.length ?? 0)),
      catchError(this.err)
    );
  }

  addToCart(productId: number, quantity = 1): Observable<ICart> {
    return this.http.post<ICart>(`${this.apiUrl}/items`, { productId, quantity }).pipe(
      tap(c => this.cartCount.set(c.cartItems?.length ?? 0)),
      catchError(this.err)
    );
  }

  updateQuantity(cartItemId: number, quantity: number): Observable<ICart> {
    return this.http.put<ICart>(`${this.apiUrl}/items/${cartItemId}`, { quantity }).pipe(
      tap(c => this.cartCount.set(c.cartItems?.length ?? 0)),
      catchError(this.err)
    );
  }

  removeItem(cartItemId: number): Observable<ICart> {
    return this.http.delete<ICart>(`${this.apiUrl}/items/${cartItemId}`).pipe(
      tap(c => this.cartCount.set(c.cartItems?.length ?? 0)),
      catchError(this.err)
    );
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.apiUrl).pipe(
      tap(() => this.cartCount.set(0)),
      catchError(this.err)
    );
  }
}
