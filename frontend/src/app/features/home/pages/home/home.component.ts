import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductService }  from '../../../../core/services/product.service';
import { AuthService }     from '../../../../core/auth/services/auth.service';
import { MediaService, IMediaFile } from '../../../../core/services/media.service';
import { IProduct }        from '../../../../core/models/models';
import { ProductCardComponent } from '../../../product-card/product-card.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, ProductCardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit, OnDestroy {
  private readonly productService = inject(ProductService);
  private readonly authService    = inject(AuthService);
  private readonly mediaService   = inject(MediaService);

  newArrivals   = signal<IProduct[]>([]);
  bestsellers   = signal<IProduct[]>([]);
  isLoadingNew  = signal(true);
  isLoadingBest = signal(true);

  get isClient(): boolean { return this.authService.getRoleId() === 2; }

  sliderFiles  = signal<IMediaFile[]>([]);
  activeSlide  = signal(0);
  private sliderTimer: ReturnType<typeof setInterval> | null = null;

  readonly fallbackSlides = [
    { title: 'Оновлено асортимент освітлення для меблів',   subtitle: 'Вимикачі · Світлодіодна стрічка · Трансформатори', cta: 'Дивитись каталог', ctaLink: '/catalog', bg: 'slide-lighting', imageUrl: '' },
    { title: 'Гардеробні системи від провідних виробників', subtitle: 'LAGUNA · Blum · GTV — офіційний представник',       cta: 'Обрати систему',   ctaLink: '/catalog', bg: 'slide-wardrobe', imageUrl: '' },
    { title: '8000+ позицій в асортименті',                 subtitle: '22 роки досвіду · Сертифікована продукція',          cta: 'Про компанію',     ctaLink: '/about',   bg: 'slide-main',     imageUrl: '' },
  ];

  get slides() {
    const files = this.sliderFiles();
    if (files.length === 0) return this.fallbackSlides;
    return files.map((f, i) => ({
      title: f.title || 'VDM — Все для меблів', subtitle: '',
      cta: 'Дивитись каталог', ctaLink: '/catalog',
      bg: `slide-${i}`, imageUrl: f.url
    }));
  }

  readonly stats = [
    { value: '22+',    label: 'роки лідерства',        icon: 'shield' },
    { value: '8000+',  label: 'позицій в асортименті', icon: 'layers' },
    { value: 'LAGUNA', label: 'офіційний представник', icon: 'users'  },
  ];

  readonly partners = ['Blum', 'GTV', 'Bosetti & Marella', 'Sige', 'Laguna', 'FGV', 'Ambos', 'Siso', 'Amig', 'Polkemic', 'Rejs', 'Lemann'];

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
      next: (files) => { this.sliderFiles.set(files); this.activeSlide.set(0); },
      error: () => {}
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
}
