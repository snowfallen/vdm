import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import {environment} from '../../../environments/environment';
import {ICategory, IPage} from '../models/models';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}categories`;

  private handleError = (error: unknown) => throwError(() => error);

  getAll(page = 0, size = 20): Observable<IPage<ICategory>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<IPage<ICategory>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getAllList(): Observable<ICategory[]> {
    return this.http.get<ICategory[]>(`${this.apiUrl}/list`).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: number): Observable<ICategory> {
    return this.http.get<ICategory>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }
}
