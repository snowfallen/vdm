import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IProductAttribute, IProductAttributeRequest } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ProductAttributeService {
  private readonly http   = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}product-attributes`;

  private handleError = (e: unknown) => throwError(() => e);

  getAllByProductId(productId: number): Observable<IProductAttribute[]> {
    return this.http.get<IProductAttribute[]>(
      `${this.apiUrl}/product/${productId}`
    ).pipe(catchError(this.handleError));
  }

  create(dto: IProductAttributeRequest): Observable<IProductAttribute> {
    return this.http.post<IProductAttribute>(this.apiUrl, dto).pipe(
      catchError(this.handleError)
    );
  }

  update(id: number, dto: IProductAttributeRequest): Observable<IProductAttribute> {
    return this.http.put<IProductAttribute>(`${this.apiUrl}/${id}`, dto).pipe(
      catchError(this.handleError)
    );
  }

  delete(id: number): Observable<IProductAttribute> {
    return this.http.delete<IProductAttribute>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }
}
