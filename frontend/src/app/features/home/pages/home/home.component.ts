import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {ProductService} from '../../../../core/services/product.service';
import {IProduct} from '../../../../core/models/models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  private readonly productService = inject(ProductService);

  newArrivals   = signal<IProduct[]>([]);
  bestsellers   = signal<IProduct[]>([]);
  isLoadingNew  = signal(true);
  isLoadingBest = signal(true);

  // Hero слайдер
  activeSlide = signal(0);
  readonly slides = [
    {
      title: 'Оновлено асортимент освітлення для меблів',
      subtitle: 'Вимикачі · Світлодіодна стрічка · Трансформатори',
      cta: 'Дивитись каталог',
      ctaLink: '/catalog',
      bg: 'slide-lighting'
    },
    {
      title: 'Гардеробні системи від провідних виробників',
      subtitle: 'LAGUNA · Blum · GTV — офіційний представник',
      cta: 'Обрати систему',
      ctaLink: '/catalog/1',
      bg: 'slide-wardrobe'
    },
    {
      title: '8000+ позицій в асортименті',
      subtitle: '22 роки досвіду · Сертифікована продукція',
      cta: 'Про компанію',
      ctaLink: '/about',
      bg: 'slide-main'
    }
  ];

  readonly stats = [
    { value: '22+',    label: 'роки лідерства',       icon: 'shield' },
    { value: '8000+',  label: 'позицій в асортименті', icon: 'layers' },
    { value: 'LAGUNA', label: 'офіційний представник', icon: 'users' },
  ];

  readonly partners = [
    'Blum', 'GTV', 'Bosetti & Marella', 'Sige', 'Laguna',
    'FGV', 'Ambos', 'Siso', 'Amig', 'Polkemic', 'Rejs', 'Lemann'
  ];

  ngOnInit(): void {
    this.loadProducts();
    this.startSlider();
  }

  private loadProducts(): void {
    this.productService.getNewArrivals(8).subscribe({
      next: ({ content }) => {
        this.newArrivals.set(content);
        this.isLoadingNew.set(false);
      },
      error: () => this.isLoadingNew.set(false)
    });

    this.productService.getBestsellers(8).subscribe({
      next: ({ content }) => {
        this.bestsellers.set(content);
        this.isLoadingBest.set(false);
      },
      error: () => this.isLoadingBest.set(false)
    });
  }

  private startSlider(): void {
    setInterval(() => {
      this.activeSlide.update(i => (i + 1) % this.slides.length);
    }, 5000);
  }

  setSlide(idx: number): void {
    this.activeSlide.set(idx);
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('uk-UA', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(price);
  }

  skeletonArray = Array(8).fill(0);
}
