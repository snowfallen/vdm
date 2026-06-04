import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface IMediaFile {
  id:           number;
  fileKey:      string;
  url:          string;
  title:        string;
  sortOrder:    number;
  originalName: string;
}

export type MediaFileKey = 'slider' | 'certificates' | 'about';

export interface ISiteSettings {
  about_image_url?:       string;
  contact_address?:       string;
  contact_phone_main?:    string;
  contact_phone_second?:  string;
  contact_phone_mobile?:  string;
  contact_email?:         string;
  work_hours_weekday?:    string;
  work_hours_weekend?:    string;
}

// Бекенд повертає { settings: { key: value } }
interface ISettingsResponse {
  settings: ISiteSettings;
}

@Injectable({ providedIn: 'root' })
export class MediaService {
  private readonly http = inject(HttpClient);
  private readonly api  = `${environment.rootUrl}media`;

  readonly settings = signal<ISiteSettings>({});

  // ─── Публічні ────────────────────────────────────────────────

  loadSettings(): Observable<ISiteSettings> {
    return this.http.get<ISettingsResponse>(`${this.api}/settings`).pipe(
      map(r => r.settings ?? r as unknown as ISiteSettings),
      tap(s => this.settings.set(s))
    );
  }

  getFiles(fileKey: MediaFileKey): Observable<IMediaFile[]> {
    return this.http.get<IMediaFile[]>(`${this.api}/files/${fileKey}`);
  }

  // ─── Admin ───────────────────────────────────────────────────

  updateSettings(body: Partial<ISiteSettings>): Observable<void> {
    return this.http.put<void>(`${this.api}/settings`, { settings: body });
  }

  upload(fileKey: MediaFileKey, file: File, title?: string): Observable<IMediaFile> {
    const form = new FormData();
    form.append('file', file);
    if (title) form.append('title', title);
    return this.http.post<IMediaFile>(`${this.api}/upload/${fileKey}`, form);
  }

  deleteFile(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/files/${id}`);
  }

  reorder(fileKey: MediaFileKey, orderedIds: number[]): Observable<void> {
    return this.http.put<void>(
      `${this.api}/files/reorder/${fileKey}`,
      { orderedIds }
    );
  }

  updateTitle(id: number, title: string): Observable<IMediaFile> {
    return this.http.patch<IMediaFile>(`${this.api}/files/${id}/title`, { title });
  }
}
