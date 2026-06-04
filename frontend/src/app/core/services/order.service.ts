import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'REFUNDED';
export type PaymentStatus = 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';

export interface IOrderItem {
  id: number;
  productId: number;
  productName: string;
  pricePerUnit: number;
  quantity: number;
  subTotal: number;
}

export interface IOrder {
  id: number;
  userId: number;
  userEmail: string;
  userFirstName: string;
  userLastName: string;
  status: OrderStatus;
  paymentStatus: PaymentStatus;
  totalPrice: number;
  deliveryCountry: string;
  deliveryCity: string;
  deliveryStreet: string;
  deliveryHouse: string;
  deliveryApartment: string;
  deliveryPostalCode: string;
  comment: string;
  createdAt: string;
  updatedAt: string;
  items: IOrderItem[];
}

export interface IPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface IPlaceOrderRequest {
  deliveryCountry?: string;
  deliveryCity?: string;
  deliveryStreet?: string;
  deliveryHouse?: string;
  deliveryApartment?: string;
  deliveryPostalCode?: string;
  comment?: string;
}

export const ORDER_STATUS_LABELS: Record<OrderStatus, string> = {
  PENDING:    'Очікує підтвердження',
  CONFIRMED:  'Підтверджено',
  PROCESSING: 'В обробці',
  SHIPPED:    'Відправлено',
  DELIVERED:  'Доставлено',
  CANCELLED:  'Скасовано',
  REFUNDED:   'Повернуто',
};

export const PAYMENT_STATUS_LABELS: Record<PaymentStatus, string> = {
  PENDING:  'Очікує оплати',
  PAID:     'Оплачено',
  FAILED:   'Не вдалося',
  REFUNDED: 'Повернуто',
};

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http   = inject(HttpClient);
  private readonly api    = environment.rootUrl;
  private readonly err    = (e: unknown) => throwError(() => e);

  // CLIENT
  placeOrder(dto: IPlaceOrderRequest): Observable<IOrder> {
    return this.http.post<IOrder>(`${this.api}orders`, dto).pipe(catchError(this.err));
  }

  getMyOrders(page = 0, size = 10): Observable<IPage<IOrder>> {
    return this.http.get<IPage<IOrder>>(`${this.api}orders/my?page=${page}&size=${size}&sort=createdAt,desc`)
      .pipe(catchError(this.err));
  }

  getMyOrderById(orderId: number): Observable<IOrder> {
    return this.http.get<IOrder>(`${this.api}orders/my/${orderId}`).pipe(catchError(this.err));
  }

  payOrder(orderId: number): Observable<IOrder> {
    return this.http.post<IOrder>(`${this.api}orders/${orderId}/pay`, {}).pipe(catchError(this.err));
  }

  cancelOrder(orderId: number): Observable<IOrder> {
    return this.http.post<IOrder>(`${this.api}orders/${orderId}/cancel`, {}).pipe(catchError(this.err));
  }

  // ADMIN
  getAllOrders(page = 0, size = 20): Observable<IPage<IOrder>> {
    return this.http.get<IPage<IOrder>>(`${this.api}admin/orders?page=${page}&size=${size}&sort=createdAt,desc`)
      .pipe(catchError(this.err));
  }

  updateOrderStatus(orderId: number, status: OrderStatus): Observable<IOrder> {
    return this.http.put<IOrder>(`${this.api}admin/orders/${orderId}/status`, { status })
      .pipe(catchError(this.err));
  }
}
