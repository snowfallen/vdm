import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {MediaService} from '../../../core/services/media.service';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './about.component.html',
})
export class AboutComponent implements OnInit {
  private readonly media = inject(MediaService);

  aboutImageUrl = signal<string>('');

  ngOnInit(): void {
    // Якщо settings вже завантажені (з app init) — беремо звідти
    const cached = this.media.settings().about_image_url;
    if (cached) { this.aboutImageUrl.set(cached); return; }

    // Інакше завантажуємо
    this.media.loadSettings().subscribe({
      next: s => this.aboutImageUrl.set(s.about_image_url ?? '')
    });
  }
}
