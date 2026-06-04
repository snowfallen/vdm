import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IProduct } from '../models/models';

@Injectable({ providedIn: 'root' })
export class FavoriteService {
  private readonly http = inject(HttpClient);
  private readonly api  = `${environment.rootUrl}favorites`;

  readonly favoriteIds = signal<Set<number>>(new Set());

  private err = (e: unknown) => throwError(() => e);

  getMyFavorites(): Observable<IProduct[]> {
    return this.http.get<IProduct[]>(this.api).pipe(
      tap(list => this.favoriteIds.set(new Set(list.map(p => p.id)))),
      catchError(this.err)
    );
  }

  toggle(productId: number): Observable<{ added: boolean; productId: number }> {
    return this.http.post<{ added: boolean; productId: number }>(
      `${this.api}/${productId}`, {}
    ).pipe(
      tap(res => {
        const set = new Set(this.favoriteIds());
        if (res.added) { set.add(productId); } else { set.delete(productId); }
        this.favoriteIds.set(set);
      }),
      catchError(this.err)
    );
  }

  isFavorite(productId: number): boolean {
    return this.favoriteIds().has(productId);
  }
}
