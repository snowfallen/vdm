import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IProduct, IPage } from '../models/models';

export interface IProductRequest {
  name: string;
  price: number;
  productGroupId: number;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http   = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}products`;

  private handleError = (error: unknown) => throwError(() => error);

  getAll(page = 0, size = 12): Observable<IPage<IProduct>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<IPage<IProduct>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: number): Observable<IProduct> {
    return this.http.get<IProduct>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getByProductGroupId(productGroupId: number): Observable<IProduct[]> {
    return this.http.get<IProduct[]>(
      `${this.apiUrl}/product-group/${productGroupId}`
    ).pipe(catchError(this.handleError));
  }

  create(dto: IProductRequest): Observable<IProduct> {
    return this.http.post<IProduct>(this.apiUrl, dto).pipe(
      catchError(this.handleError)
    );
  }

  update(id: number, dto: IProductRequest): Observable<IProduct> {
    return this.http.put<IProduct>(`${this.apiUrl}/${id}`, dto).pipe(
      catchError(this.handleError)
    );
  }

  delete(id: number): Observable<IProduct> {
    return this.http.delete<IProduct>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  search(q: string, page = 0, size = 12): Observable<IPage<IProduct>> {
    const params = new HttpParams()
      .set('q', q)
      .set('page', page)
      .set('size', size);
    return this.http.get<IPage<IProduct>>(`${this.apiUrl}/search`, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getNewArrivals(size = 8): Observable<IPage<IProduct>> {
    const params = new HttpParams()
      .set('page', 0).set('size', size).set('sort', 'id,desc');
    return this.http.get<IPage<IProduct>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getBestsellers(size = 8): Observable<IPage<IProduct>> {
    const params = new HttpParams()
      .set('page', 0).set('size', size).set('sort', 'price,desc');
    return this.http.get<IPage<IProduct>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }
}
