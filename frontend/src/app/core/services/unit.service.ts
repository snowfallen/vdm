import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { IUnit, IUnitRequest } from '../models/models';
import {environment} from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UnitService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}units`;

  private handleError = (e: unknown) => throwError(() => e);

  getAll(): Observable<IUnit[]> {
    return this.http.get<IUnit[]>(this.apiUrl).pipe(catchError(this.handleError));
  }

  getById(id: number): Observable<IUnit> {
    return this.http.get<IUnit>(`${this.apiUrl}/${id}`).pipe(catchError(this.handleError));
  }

  create(dto: IUnitRequest): Observable<IUnit> {
    return this.http.post<IUnit>(this.apiUrl, dto).pipe(catchError(this.handleError));
  }

  update(id: number, dto: IUnitRequest): Observable<IUnit> {
    return this.http.put<IUnit>(`${this.apiUrl}/${id}`, dto).pipe(catchError(this.handleError));
  }

  delete(id: number): Observable<IUnit> {
    return this.http.delete<IUnit>(`${this.apiUrl}/${id}`).pipe(catchError(this.handleError));
  }
}
