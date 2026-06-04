import { Component, signal, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface ICertificate {
  src: string;
  title: string;
}

@Component({
  selector: 'app-certificates',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './certificates.component.html',
})
export class CertificatesComponent {
  // ⚠️ ПОМІНЯЙ src на свої реальні шляхи до фото
  readonly certificates: ICertificate[] = [
    { src: 'assets/certificates/cert-1.jpg', title: 'Сертифікат якості Blum' },
    { src: 'assets/certificates/cert-2.jpg', title: 'Офіційний дилер LAGUNA' },
    { src: 'assets/certificates/cert-3.jpg', title: 'Сертифікат відповідності GTV' },
    { src: 'assets/certificates/cert-4.jpg', title: 'Партнерський статус FGV' },
  ];

  // Лайтбокс
  activeIndex = signal<number | null>(null);

  open(i: number): void { this.activeIndex.set(i); }
  close(): void         { this.activeIndex.set(null); }

  prev(event: Event): void {
    event.stopPropagation();
    const cur = this.activeIndex();
    if (cur === null) return;
    this.activeIndex.set((cur - 1 + this.certificates.length) % this.certificates.length);
  }

  next(event: Event): void {
    event.stopPropagation();
    const cur = this.activeIndex();
    if (cur === null) return;
    this.activeIndex.set((cur + 1) % this.certificates.length);
  }

  @HostListener('document:keydown.escape')
  onEsc(): void { this.close(); }

  @HostListener('document:keydown.arrowLeft', ['$event'])
  onLeft(e: Event): void { if (this.activeIndex() !== null) this.prev(e); }

  @HostListener('document:keydown.arrowRight', ['$event'])
  onRight(e: Event): void { if (this.activeIndex() !== null) this.next(e); }
}
