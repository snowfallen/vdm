import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  OrderService, IOrder, IPage, OrderStatus,
  ORDER_STATUS_LABELS, PAYMENT_STATUS_LABELS
} from '../../../core/services/order.service';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-orders.component.html',
  styleUrl: './admin-orders.component.scss'
})
export class AdminOrdersComponent implements OnInit {
  private readonly orderService = inject(OrderService);

  orders        = signal<IOrder[]>([]);
  totalElements = signal(0);
  currentPage   = signal(0);
  pageSize      = signal(20);
  isLoading     = signal(true);
  selectedOrder = signal<IOrder | null>(null);
  successMsg    = signal('');

  readonly allStatuses: OrderStatus[] = [
    'PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED'
  ];
  readonly orderStatusLabels  = ORDER_STATUS_LABELS;
  readonly paymentStatusLabels = PAYMENT_STATUS_LABELS;

  ngOnInit(): void { this.load(); }

  load(): void {
    this.isLoading.set(true);
    this.orderService.getAllOrders(this.currentPage(), this.pageSize()).subscribe({
      next: (page) => {
        this.orders.set(page.content);
        this.totalElements.set(page.totalElements);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  openOrder(order: IOrder): void { this.selectedOrder.set(order); }
  closeOrder(): void { this.selectedOrder.set(null); }

  updateStatus(orderId: number, status: OrderStatus): void {
    this.orderService.updateOrderStatus(orderId, status).subscribe({
      next: (updated) => {
        this.orders.update(list => list.map(o => o.id === orderId ? updated : o));
        this.selectedOrder.set(updated);
        this.successMsg.set('Статус оновлено!');
        setTimeout(() => this.successMsg.set(''), 2500);
      }
    });
  }

  getStatusColor(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'badge--warning', CONFIRMED: 'badge--info',
      PROCESSING: 'badge--info',  SHIPPED: 'badge--primary',
      DELIVERED: 'badge--success', CANCELLED: 'badge--danger',
      REFUNDED: 'badge--muted',    PAID: 'badge--success',
      FAILED: 'badge--danger',
    };
    return map[status] ?? 'badge--muted';
  }

  get totalPages(): number { return Math.ceil(this.totalElements() / this.pageSize()); }

  goPage(p: number): void {
    this.currentPage.set(p);
    this.load();
  }
}
