import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-contacts',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './contacts.component.html',
})
export class ContactsComponent {
  // Embed Google Maps без API-ключа (адреса VDM, бульвар Романа Роллана 3, Київ).
  // DomSanitizer потрібен, щоб Angular дозволив iframe-src.
  readonly mapUrl: SafeResourceUrl;

  constructor(private sanitizer: DomSanitizer) {
    const raw =
      'https://www.google.com/maps?q=' +
      encodeURIComponent('бульвар Романа Роллана, 3, Київ') +
      '&output=embed';
    this.mapUrl = this.sanitizer.bypassSecurityTrustResourceUrl(raw);
  }
}
