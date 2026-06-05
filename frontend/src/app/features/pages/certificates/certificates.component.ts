import { Component, inject, signal, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {IMediaFile, MediaService} from "../../../core/services/media.service";

@Component({
  selector: 'app-certificates',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './certificates.component.html',
})
export class CertificatesComponent implements OnInit {
  private readonly media = inject(MediaService);

  certificates = signal<IMediaFile[]>([]);
  activeIndex  = signal<number | null>(null);

  ngOnInit(): void {
    this.media.getFiles('certificates').subscribe({
      next: files => this.certificates.set(files)
    });
  }

  open(i: number): void  { this.activeIndex.set(i); }
  close(): void          { this.activeIndex.set(null); }

  prev(event: Event): void {
    event.stopPropagation();
    const cur = this.activeIndex();
    if (cur === null) return;
    this.activeIndex.set(
      (cur - 1 + this.certificates().length) % this.certificates().length
    );
  }

  next(event: Event): void {
    event.stopPropagation();
    const cur = this.activeIndex();
    if (cur === null) return;
    this.activeIndex.set(
      (cur + 1) % this.certificates().length
    );
  }

  @HostListener('document:keydown.escape')
  onEsc(): void { this.close(); }

  @HostListener('document:keydown.arrowLeft', ['$event'])
  onLeft(e: Event): void { if (this.activeIndex() !== null) this.prev(e); }

  @HostListener('document:keydown.arrowRight', ['$event'])
  onRight(e: Event): void { if (this.activeIndex() !== null) this.next(e); }
}
