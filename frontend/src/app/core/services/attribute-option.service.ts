import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { IAttributeOption, IAttributeOptionRequest } from '../models/models';
import {environment} from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AttributeOptionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}attribute-options`;

  private handleError = (e: unknown) => throwError(() => e);

  getAllByAttributeId(attributeId: number): Observable<IAttributeOption[]> {
    return this.http.get<IAttributeOption[]>(
      `${this.apiUrl}/attribute/${attributeId}`
    ).pipe(catchError(this.handleError));
  }

  getById(id: number): Observable<IAttributeOption> {
    return this.http.get<IAttributeOption>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  create(dto: IAttributeOptionRequest): Observable<IAttributeOption> {
    return this.http.post<IAttributeOption>(this.apiUrl, dto).pipe(
      catchError(this.handleError)
    );
  }

  update(id: number, dto: IAttributeOptionRequest): Observable<IAttributeOption> {
    return this.http.put<IAttributeOption>(`${this.apiUrl}/${id}`, dto).pipe(
      catchError(this.handleError)
    );
  }

  delete(id: number): Observable<IAttributeOption> {
    return this.http.delete<IAttributeOption>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }
}
