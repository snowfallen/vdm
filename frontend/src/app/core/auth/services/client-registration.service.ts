import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {IClientRegistrationRequest} from '../models/i-client-registration-request';
import {IUser} from '../../models/models'; // Перевір шлях до environment


@Injectable({
  providedIn: 'root'
})
export class ClientRegistrationService {
  private static readonly BASE_URL: string = environment.rootUrl;
  private readonly http: HttpClient = inject(HttpClient);

  /**
   * Реєструє нового клієнта (User + Client Profile).
   * @param registrationData Дані для реєстрації.
   * @returns Observable з результатом (наприклад, створеним IUser або іншим об'єктом відповіді).
   */
  public registerClient(registrationData: IClientRegistrationRequest): Observable<IUser> { // Або Observable<any>, якщо відповідь інша
    // Надсилаємо POST запит на ендпоінт /clients
    return this.http.post<IUser>(`${ClientRegistrationService.BASE_URL}clients`, registrationData); // Або post<any>
  }
}
