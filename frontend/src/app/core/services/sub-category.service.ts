import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import {environment} from '../../../environments/environment';
import {IPage, ISubCategory} from '../models/models';


@Injectable({ providedIn: 'root' })
export class SubCategoryService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}sub-categories`;

  private handleError = (error: unknown) => throwError(() => error);

  getAll(page = 0, size = 50): Observable<IPage<ISubCategory>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<IPage<ISubCategory>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getByCategoryId(categoryId: number): Observable<ISubCategory[]> {
    return this.http.get<ISubCategory[]>(
      `${this.apiUrl}/category/${categoryId}`
    ).pipe(catchError(this.handleError));
  }

  getById(id: number): Observable<ISubCategory> {
    return this.http.get<ISubCategory>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }
}
