import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IProductGroup, IPage } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ProductGroupService {
  private readonly http   = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}product-groups`;

  private handleError = (error: unknown) => throwError(() => error);

  getAll(page = 0, size = 50): Observable<IPage<IProductGroup>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<IPage<IProductGroup>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getBySubCategoryId(subCategoryId: number): Observable<IProductGroup[]> {
    return this.http.get<IProductGroup[]>(
      `${this.apiUrl}/sub-category/${subCategoryId}`
    ).pipe(catchError(this.handleError));
  }

  getById(id: number): Observable<IProductGroup> {
    return this.http.get<IProductGroup>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  create(dto: { name: string; subCategoryId: number }): Observable<IProductGroup> {
    return this.http.post<IProductGroup>(this.apiUrl, dto).pipe(
      catchError(this.handleError)
    );
  }

  update(id: number, dto: { name: string; subCategoryId: number }): Observable<IProductGroup> {
    return this.http.put<IProductGroup>(`${this.apiUrl}/${id}`, dto).pipe(
      catchError(this.handleError)
    );
  }

  delete(id: number): Observable<IProductGroup> {
    return this.http.delete<IProductGroup>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }
}
