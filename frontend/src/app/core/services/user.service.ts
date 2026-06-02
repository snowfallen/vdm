import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { IUser, IUserUpdateRequest, IUserUpdatePasswordRequest, IPage } from '../models/models';
import {environment} from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.rootUrl}users`;

  private handleError = (e: unknown) => throwError(() => e);

  getAll(page = 0, size = 20): Observable<IPage<IUser>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<IPage<IUser>>(this.apiUrl, { params }).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: number): Observable<IUser> {
    return this.http.get<IUser>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getPersonalInfo(): Observable<IUser> {
    return this.http.get<IUser>(`${this.apiUrl}/info`).pipe(
      catchError(this.handleError)
    );
  }

  update(id: number, dto: IUserUpdateRequest): Observable<IUser> {
    return this.http.put<IUser>(`${this.apiUrl}/${id}`, dto).pipe(
      catchError(this.handleError)
    );
  }

  updateOwnData(dto: IUserUpdateRequest): Observable<IUser> {
    return this.http.put<IUser>(`${this.apiUrl}/update`, dto).pipe(
      catchError(this.handleError)
    );
  }

  updatePassword(id: number, dto: IUserUpdatePasswordRequest): Observable<IUser> {
    return this.http.put<IUser>(`${this.apiUrl}/password/${id}`, dto).pipe(
      catchError(this.handleError)
    );
  }

  updateOwnPassword(dto: IUserUpdatePasswordRequest): Observable<IUser> {
    return this.http.put<IUser>(`${this.apiUrl}/password`, dto).pipe(
      catchError(this.handleError)
    );
  }

  delete(id: number): Observable<IUser> {
    return this.http.delete<IUser>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }
}
