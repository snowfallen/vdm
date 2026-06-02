import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import {environment} from '../../../environments/environment';
import {IPage, IProduct} from '../models/models';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}products`;

  private handleError = (error: unknown) => throwError(() => error);

  getAll(page = 0, size = 12): Observable<IPage<IProduct>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<IPage<IProduct>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: number): Observable<IProduct> {
    return this.http.get<IProduct>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getByProductGroupId(productGroupId: number, page = 0, size = 12): Observable<IProduct[]> {
    return this.http.get<IProduct[]>(
      `${this.apiUrl}/product-group/${productGroupId}`
    ).pipe(catchError(this.handleError));
  }

  // Новинки — перша сторінка, відсортована по id desc
  getNewArrivals(size = 8): Observable<IPage<IProduct>> {
    const params = new HttpParams()
      .set('page', 0)
      .set('size', size)
      .set('sort', 'id,desc');
    return this.http.get<IPage<IProduct>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }

  // Хіти продажів — можна потім замінити на окремий ендпоінт
  getBestsellers(size = 8): Observable<IPage<IProduct>> {
    const params = new HttpParams()
      .set('page', 0)
      .set('size', size)
      .set('sort', 'price,desc');
    return this.http.get<IPage<IProduct>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }
}
