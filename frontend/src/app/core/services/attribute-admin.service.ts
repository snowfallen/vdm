import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  IAttribute, IAttributeRequest, IAttributeWithOptions, IPage
} from '../models/models';

@Injectable({ providedIn: 'root' })
export class AttributeAdminService {
  private readonly http   = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}attributes`;

  private handleError = (e: unknown) => throwError(() => e);

  getAll(page = 0, size = 50): Observable<IPage<IAttribute>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<IPage<IAttribute>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getAllList(): Observable<IAttribute[]> {
    return this.http.get<IAttribute[]>(`${this.apiUrl}/list`).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: number): Observable<IAttribute> {
    return this.http.get<IAttribute>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getWithOptions(id: number): Observable<IAttributeWithOptions> {
    return this.http.get<IAttributeWithOptions>(`${this.apiUrl}/${id}/with-options`).pipe(
      catchError(this.handleError)
    );
  }

  create(dto: IAttributeRequest): Observable<IAttribute> {
    return this.http.post<IAttribute>(this.apiUrl, dto).pipe(
      catchError(this.handleError)
    );
  }

  update(id: number, dto: IAttributeRequest): Observable<IAttribute> {
    return this.http.put<IAttribute>(`${this.apiUrl}/${id}`, dto).pipe(
      catchError(this.handleError)
    );
  }

  delete(id: number): Observable<IAttribute> {
    return this.http.delete<IAttribute>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }
}
