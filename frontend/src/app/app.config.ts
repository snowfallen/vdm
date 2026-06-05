import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import {provideRouter, withInMemoryScrolling} from '@angular/router';

import { routes } from './app.routes';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {authInterceptor} from './core/auth/interceptors/auth.interceptor';
import {authTokenInterceptor} from './core/auth/interceptors/auth.token.interceptor';
import {provideHttpClient, withInterceptors} from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withInMemoryScrolling({ scrollPositionRestoration: 'top' })),
    provideHttpClient(withInterceptors([authTokenInterceptor, authInterceptor])), provideAnimationsAsync(),
    provideAnimationsAsync(),
  ]
};
