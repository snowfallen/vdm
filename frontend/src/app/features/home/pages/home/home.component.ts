import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../../../core/services/product.service';
import { CartService }    from '../../../../core/services/cart.service';
import { AuthService }    from '../../../../core/auth/services/auth.service';
import { MediaService, IMediaFile } from '../../../../core/services/media.service';
import { IProduct }       from '../../../../core/models/models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit, OnDestroy {
  private readonly productService = inject(ProductService);
  private readonly cartService    = inject(CartService);
  private readonly authService    = inject(AuthService);
  private readonly mediaService   = inject(MediaService);

  newArrivals   = signal<IProduct[]>([]);
  bestsellers   = signal<IProduct[]>([]);
  isLoadingNew  = signal(true);
  isLoadingBest = signal(true);

  addedIds = signal<Set<number>>(new Set());

  get isClient(): boolean { return this.authService.getRoleId() === 2; }

  // Hero слайдер — файли з MinIO
  sliderFiles   = signal<IMediaFile[]>([]);
  activeSlide   = signal(0);
  private sliderTimer: ReturnType<typeof setInterval> | null = null;

  // Fallback слайди якщо MinIO порожній
  readonly fallbackSlides = [
    {
      title:    'Оновлено асортимент освітлення для меблів',
      subtitle: 'Вимикачі · Світлодіодна стрічка · Трансформатори',
      cta:      'Дивитись каталог',
      ctaLink:  '/catalog',
      bg:       'slide-lighting',
      imageUrl: ''
    },
    {
      title:    'Гардеробні системи від провідних виробників',
      subtitle: 'LAGUNA · Blum · GTV — офіційний представник',
      cta:      'Обрати систему',
      ctaLink:  '/catalog',
      bg:       'slide-wardrobe',
      imageUrl: ''
    },
    {
      title:    '8000+ позицій в асортименті',
      subtitle: '22 роки досвіду · Сертифікована продукція',
      cta:      'Про компанію',
      ctaLink:  '/about',
      bg:       'slide-main',
      imageUrl: ''
    }
  ];

  // Поточні слайди — або з MinIO або fallback
  get slides() {
    const files = this.sliderFiles();
    if (files.length === 0) return this.fallbackSlides;
    return files.map((f, i) => ({
      title:    f.title || 'VDM — Все для меблів',
      subtitle: '',
      cta:      'Дивитись каталог',
      ctaLink:  '/catalog',
      bg:       `slide-${i}`,
      imageUrl: f.url
    }));
  }

  readonly stats = [
    { value: '22+',    label: 'роки лідерства',        icon: 'shield' },
    { value: '8000+',  label: 'позицій в асортименті', icon: 'layers' },
    { value: 'LAGUNA', label: 'офіційний представник', icon: 'users'  },
  ];

  readonly partners = [
    'Blum', 'GTV', 'Bosetti & Marella', 'Sige', 'Laguna',
    'FGV', 'Ambos', 'Siso', 'Amig', 'Polkemic', 'Rejs', 'Lemann'
  ];

  readonly skeletonArray = Array(8).fill(0);

  ngOnInit(): void {
    this.loadSlider();
    this.loadProducts();
    this.startSlider();
  }

  ngOnDestroy(): void {
    if (this.sliderTimer) clearInterval(this.sliderTimer);
  }

  private loadSlider(): void {
    this.mediaService.getFiles('slider').subscribe({
      next: (files: IMediaFile[]) => {
        this.sliderFiles.set(files);
        // Скидаємо на перший слайд після завантаження
        this.activeSlide.set(0);
      },
      error: () => {
        // Мовчки ігноруємо — покажуться fallback слайди з CSS градієнтами
      }
    });
  }

  private loadProducts(): void {
    this.productService.getNewArrivals(8).subscribe({
      next: ({ content }) => { this.newArrivals.set(content); this.isLoadingNew.set(false); },
      error: () => this.isLoadingNew.set(false)
    });

    this.productService.getBestsellers(8).subscribe({
      next: ({ content }) => { this.bestsellers.set(content); this.isLoadingBest.set(false); },
      error: () => this.isLoadingBest.set(false)
    });
  }

  private startSlider(): void {
    this.sliderTimer = setInterval(() => {
      this.activeSlide.update(i => (i + 1) % this.slides.length);
    }, 5000);
  }

  setSlide(idx: number): void { this.activeSlide.set(idx); }

  addToCart(event: Event, product: IProduct): void {
    event.preventDefault();
    event.stopPropagation();
    if (!this.isClient) return;

    this.cartService.addToCart(product.id, 1).subscribe({
      next: () => {
        const set = new Set(this.addedIds());
        set.add(product.id);
        this.addedIds.set(set);
        setTimeout(() => {
          const updated = new Set(this.addedIds());
          updated.delete(product.id);
          this.addedIds.set(updated);
        }, 2000);
      }
    });
  }

  isAdded(productId: number): boolean { return this.addedIds().has(productId); }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('uk-UA', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(price);
  }
}
