import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {AuthService} from "../../../core/auth/services/auth.service";
import {CartService} from "../../../core/services/cart.service";
import {FavoriteService} from "../../../core/services/favorite.service";
import {IOrder, ORDER_STATUS_LABELS, OrderService, PAYMENT_STATUS_LABELS} from "../../../core/services/order.service";
import {IProduct} from "../../../core/models/models";
import {environment} from "../../../../environments/environment";

type CabinetTab = 'profile' | 'orders' | 'favorites' | 'address' | 'security';

interface IUserProfile {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  emailVerified: boolean;
}

interface IClientData {
  country: string;
  city: string;
  street: string;
  houseNumber: string;
  apartmentNumber: string;
  postalCode: string;
}

@Component({
  selector: 'app-cabinet',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './cabinet.component.html',
  styleUrl: './cabinet.component.scss'
})
export class CabinetComponent implements OnInit {
  private readonly auth            = inject(AuthService);
  private readonly cartService     = inject(CartService);
  private readonly favoriteService = inject(FavoriteService);
  private readonly orderService    = inject(OrderService);
  private readonly http            = inject(HttpClient);
  private readonly fb              = inject(FormBuilder);
  private readonly route           = inject(ActivatedRoute);

  readonly cartCount = this.cartService.cartCount;

  activeTab     = signal<CabinetTab>('profile');
  profile       = signal<IUserProfile | null>(null);
  clientData    = signal<IClientData | null>(null);
  orders        = signal<IOrder[]>([]);
  favorites     = signal<IProduct[]>([]);
  totalOrders   = signal(0);
  isLoading     = signal(true);
  saveSuccess   = signal('');
  saveError     = signal('');
  selectedOrder = signal<IOrder | null>(null);

  profileForm!: FormGroup;
  addressForm!: FormGroup;
  passwordForm!: FormGroup;

  readonly orderStatusLabels   = ORDER_STATUS_LABELS;
  readonly paymentStatusLabels = PAYMENT_STATUS_LABELS;

  get isAdmin():  boolean { return this.auth.getRoleId() === 1; }
  get isClient(): boolean { return this.auth.getRoleId() === 2; }

  ngOnInit(): void {
    this.buildForms();
    this.loadAll();

    // Обробка queryParams — ?tab=orders&orderId=5
    this.route.queryParams.subscribe(params => {
      if (params['tab']) {
        this.setTab(params['tab'] as CabinetTab);
      }
      if (params['orderId']) {
        const id = +params['orderId'];
        this.orderService.getMyOrderById(id).subscribe({
          next: (order) => this.selectedOrder.set(order),
          error: () => {}
        });
      }
    });
  }

  private buildForms(): void {
    this.profileForm = this.fb.group({
      firstName:   ['', [Validators.required, Validators.minLength(2)]],
      lastName:    ['', [Validators.required, Validators.minLength(2)]],
      phoneNumber: ['', Validators.required],
    });

    this.addressForm = this.fb.group({
      country:         ['', Validators.required],
      city:            ['', Validators.required],
      street:          ['', Validators.required],
      houseNumber:     ['', Validators.required],
      apartmentNumber: [''],
      postalCode:      ['', Validators.required],
    });

    this.passwordForm = this.fb.group({
      password:        ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
    });
  }

  loadAll(): void {
    this.isLoading.set(true);

    this.http.get<IUserProfile>(`${environment.rootUrl}users/info`).subscribe({
      next: (p) => {
        this.profile.set(p);
        this.profileForm.patchValue({
          firstName:   p.firstName,
          lastName:    p.lastName,
          phoneNumber: p.phoneNumber,
        });
      }
    });

    this.http.get<IClientData>(`${environment.rootUrl}clients/me`).subscribe({
      next: (c) => {
        this.clientData.set(c);
        this.addressForm.patchValue(c);
      },
      error: () => {}
    });

    if (this.isClient) {
      this.orderService.getMyOrders().subscribe({
        next: (page) => {
          this.orders.set(page.content);
          this.totalOrders.set(page.totalElements);
        }
      });

      this.favoriteService.getMyFavorites().subscribe({
        next: (list) => this.favorites.set(list)
      });

      this.cartService.getCart().subscribe({ error: () => {} });
    }

    this.isLoading.set(false);
  }

  setTab(tab: CabinetTab): void {
    this.activeTab.set(tab);
    this.saveSuccess.set('');
    this.saveError.set('');
    this.selectedOrder.set(null);
  }

  // ---- Profile ----
  saveProfile(): void {
    if (this.profileForm.invalid) return;
    this.http.put(`${environment.rootUrl}users/update`, this.profileForm.value).subscribe({
      next: () => {
        this.saveSuccess.set('Дані збережено!');
        setTimeout(() => this.saveSuccess.set(''), 3000);
        this.loadAll();
      },
      error: () => this.saveError.set('Помилка збереження.')
    });
  }

  // ---- Address ----
  saveAddress(): void {
    if (this.addressForm.invalid) return;
    this.http.put(`${environment.rootUrl}clients/me`, this.addressForm.value).subscribe({
      next: () => {
        this.saveSuccess.set('Адресу збережено!');
        setTimeout(() => this.saveSuccess.set(''), 3000);
        this.loadAll();
      },
      error: () => this.saveError.set('Помилка збереження.')
    });
  }

  // ---- Password ----
  savePassword(): void {
    if (this.passwordForm.invalid) return;
    const { password, confirmPassword } = this.passwordForm.value;
    if (password !== confirmPassword) {
      this.saveError.set('Паролі не збігаються.');
      return;
    }
    this.http.put(`${environment.rootUrl}users/password`, { password }).subscribe({
      next: () => {
        this.saveSuccess.set('Пароль змінено!');
        this.passwordForm.reset();
        setTimeout(() => this.saveSuccess.set(''), 3000);
      },
      error: () => this.saveError.set('Помилка зміни пароля.')
    });
  }

  // ---- Orders ----
  openOrder(order: IOrder): void { this.selectedOrder.set(order); }
  closeOrder(): void             { this.selectedOrder.set(null); }

  payOrder(orderId: number): void {
    this.orderService.payOrder(orderId).subscribe({
      next: (updated) => {
        this.orders.update(list => list.map(o => o.id === orderId ? updated : o));
        this.selectedOrder.set(updated);
        this.saveSuccess.set('Замовлення оплачено!');
        setTimeout(() => this.saveSuccess.set(''), 3000);
      },
      error: () => this.saveError.set('Помилка оплати.')
    });
  }

  cancelOrder(orderId: number): void {
    this.orderService.cancelOrder(orderId).subscribe({
      next: (updated) => {
        this.orders.update(list => list.map(o => o.id === orderId ? updated : o));
        this.selectedOrder.set(updated);
      }
    });
  }

  // ---- Favorites ----
  removeFavorite(productId: number): void {
    this.favoriteService.toggle(productId).subscribe({
      next: () => this.favorites.update(list => list.filter(p => p.id !== productId))
    });
  }

  // ---- Utils ----
  getStatusColor(status: string): string {
    const map: Record<string, string> = {
      PENDING:    'badge--warning',
      CONFIRMED:  'badge--info',
      PROCESSING: 'badge--info',
      SHIPPED:    'badge--primary',
      DELIVERED:  'badge--success',
      CANCELLED:  'badge--danger',
      REFUNDED:   'badge--muted',
      PAID:       'badge--success',
      FAILED:     'badge--danger',
    };
    return map[status] ?? 'badge--muted';
  }

  canPay(order: IOrder): boolean {
    return order.paymentStatus === 'PENDING' && order.status !== 'CANCELLED';
  }

  canCancel(order: IOrder): boolean {
    return order.status !== 'CANCELLED'
      && order.status !== 'SHIPPED'
      && order.status !== 'DELIVERED';
  }

  logout(): void { this.auth.logout(); }
}
