import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import { RouterModule } from '@angular/router';
import {AuthService} from '../../../../core/auth/services/auth.service'; // Import RouterModule

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    RouterModule // Add RouterModule here
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})

export class LoginComponent {
  private readonly authService: AuthService = inject(AuthService)
  passwordFieldType: string = 'password';
  errorMessage: string = '';

  public form: FormGroup = new FormGroup({
    email: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required)
  })

  get emailInvalid(): boolean | undefined {
    const control = this.form.get('email');
    return control?.invalid && (control.dirty || control.touched);
  }

  get passwordInvalid(): boolean | undefined {
    const control = this.form.get('password');
    return control?.invalid && (control.dirty || control.touched);
  }

  public login() {
    if (this.form.valid) {
      this.authService.login(this.form.value).subscribe(statusCode => {
        if (statusCode === 200) {
          this.errorMessage = '';
        } else {
          this.setErrorMessage(statusCode);
          console.log(`Login failed with status code: ${statusCode}`);
        }
      });
    }
  }

  togglePasswordVisibility() {
    this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
  }

  setErrorMessage(status: number) {
    switch (status) {
      case 400:
        this.errorMessage = 'Nieprawidłowe dane. Spróbuj ponownie.';
        break;
      case 401:
        this.errorMessage = 'Nieautoryzowany dostęp. Sprawdź swój e-mail i hasło.';
        break;
      case 500:
        this.errorMessage = 'Błąd serwera. Spróbuj ponownie później.';
        break;
      default:
        this.errorMessage = 'Wystąpił nieznany błąd. Spróbuj ponownie.';
    }
  }
}
