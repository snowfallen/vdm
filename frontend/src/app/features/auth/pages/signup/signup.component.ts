// src/app/core/auth/pages/signup/signup.component.ts
import { Component, inject } from '@angular/core';
import { NgClass, NgIf } from "@angular/common";
import { FormControl, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from "@angular/forms";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Router, RouterLink } from "@angular/router";
import {ClientRegistrationService} from '../../../../core/auth/services/client-registration.service';
import {IClientRegistrationRequest} from '../../../../core/auth/models/i-client-registration-request';
import {IUser} from '../../../../core/models/models';

// Імпортуємо новий сервіс та інтерфейси
// Перевір шлях

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule,
    NgClass,
    RouterLink
  ],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SignupComponent {
  // Інжектуємо новий сервіс замість UserService
  private readonly registrationService: ClientRegistrationService = inject(ClientRegistrationService);
  private readonly snackBar: MatSnackBar = inject(MatSnackBar);
  private readonly router: Router = inject(Router);

  // ID ролі для нового КЛІЄНТА (зміни, якщо потрібно, на ID ролі клієнта)
  private readonly DEFAULT_CLIENT_ROLE_ID = 2;

  passwordFieldType: string = 'password';
  public errorMessage: string = '';

  // Основна форма, тепер включає вкладені групи для user і client data
  public registrationForm: FormGroup = new FormGroup({
    userRegistrationData: new FormGroup({ // Група для даних юзера
      email: new FormControl('', [Validators.required, Validators.email]),
      phoneNumber: new FormControl('', Validators.required),
      // roleId не в формі, додамо в onSubmit
      firstName: new FormControl('', Validators.required),
      lastName: new FormControl('', Validators.required),
      password: new FormControl('', [Validators.required, Validators.minLength(8)]),
      repeatPassword: new FormControl('', [Validators.required]) // Тільки required, довжину перевіряє password
    }, { validators: this.passwordMatchValidator }), // Валідатор паролів на рівні цієї групи

    clientData: new FormGroup({ // Група для даних клієнта
      country: new FormControl('', Validators.required),
      city: new FormControl('', Validators.required),
      street: new FormControl('', Validators.required),
      houseNumber: new FormControl('', Validators.required),
      apartmentNumber: new FormControl(''), // Необов'язкове
      postalCode: new FormControl('', [Validators.required, Validators.pattern(/^\d{2}-\d{3}$/)]) // Патерн для польського коду
    })
  });

  // Валідатор співпадіння паролів
  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const repeatPassword = control.get('repeatPassword')?.value;
    // Перевіряємо, тільки якщо обидва поля заповнені
    if (password && repeatPassword && password !== repeatPassword) {
      // Встановлюємо помилку 'mismatch' на полі repeatPassword
      control.get('repeatPassword')?.setErrors({ mismatch: true, ...(control.get('repeatPassword')?.errors || {}) });
      return { mismatch: true }; // Повертаємо помилку і для групи
    } else if (control.get('repeatPassword')?.hasError('mismatch')) {
      // Якщо паролі співпали, але помилка ще є - видаляємо її
      const errors = { ...(control.get('repeatPassword')?.errors || {}) };
      delete errors['mismatch'];
      control.get('repeatPassword')?.setErrors(Object.keys(errors).length > 0 ? errors : null);
    }
    return null;
  };

  togglePasswordVisibility() {
    this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
  }

  onSubmit(): void {
    this.registrationForm.markAllAsTouched(); // Показуємо всі помилки перед перевіркою

    if (this.registrationForm.valid) {
      // Збираємо дані з вкладених груп
      const userRegData = {
        ...this.registrationForm.get('userRegistrationData')?.value,
        roleId: this.DEFAULT_CLIENT_ROLE_ID // Додаємо roleId
      };
      const clientProfData = this.registrationForm.get('clientData')?.value;

      // Формуємо фінальний об'єкт запиту
      const registrationRequest: IClientRegistrationRequest = {
        userRegistrationData: userRegData,
        clientData: clientProfData
      };

      console.log('Sending client registration data:', registrationRequest);

      // Викликаємо метод нового сервісу
      this.registrationService.registerClient(registrationRequest).subscribe({
        next: (response: IUser | any) => { // Тип відповіді може бути іншим
          this.snackBar.open('Konto klienta zostało pomyślnie utworzone! Możesz się teraz zalogować.', 'OK', {
            duration: 5000,
            horizontalPosition: 'center',
            verticalPosition: 'top',
            panelClass: ['success-snackbar']
          });
          this.router.navigate(['/login']);
        },
        error: error => {
          console.error('Client registration error:', error);
          this.setErrorMessage(error.status, error.error);
        }
      });
    } else {
      console.log("Form is invalid");
      // Додатково можна прокрутити до першого невалідного поля, якщо форма довга
    }
  }

  // Getters для доступу до контролів userRegistrationData
  getUserControl(name: string): FormControl {
    return this.registrationForm.get(`userRegistrationData.${name}`) as FormControl;
  }
  get userFirstName() { return this.getUserControl('firstName'); }
  get userLastName() { return this.getUserControl('lastName'); }
  get userEmail() { return this.getUserControl('email'); }
  get userPhoneNumber() { return this.getUserControl('phoneNumber'); }
  get userPassword() { return this.getUserControl('password'); }
  get userRepeatPassword() { return this.getUserControl('repeatPassword'); }

  // Getters для доступу до контролів clientData
  getClientControl(name: string): FormControl {
    return this.registrationForm.get(`clientData.${name}`) as FormControl;
  }
  get clientCountry() { return this.getClientControl('country'); }
  get clientCity() { return this.getClientControl('city'); }
  get clientStreet() { return this.getClientControl('street'); }
  get clientHouseNumber() { return this.getClientControl('houseNumber'); }
  get clientApartmentNumber() { return this.getClientControl('apartmentNumber'); }
  get clientPostalCode() { return this.getClientControl('postalCode'); }


  // Getters для перевірки валідності в шаблоні (оновлені)
  get firstNameInvalid(): boolean { return !!this.userFirstName?.invalid && (!!this.userFirstName?.dirty || !!this.userFirstName?.touched); }
  get lastNameInvalid(): boolean { return !!this.userLastName?.invalid && (!!this.userLastName?.dirty || !!this.userLastName?.touched); }
  get emailInvalid(): boolean { return !!this.userEmail?.invalid && (!!this.userEmail?.dirty || !!this.userEmail?.touched); }
  get phoneNumberInvalid(): boolean { return !!this.userPhoneNumber?.invalid && (!!this.userPhoneNumber?.dirty || !!this.userPhoneNumber?.touched); }
  get passwordInvalid(): boolean { return !!this.userPassword?.invalid && (!!this.userPassword?.dirty || !!this.userPassword?.touched); }
  get repeatPasswordInvalid(): boolean { return !!this.userRepeatPassword?.invalid && (!!this.userRepeatPassword?.dirty || !!this.userRepeatPassword?.touched); }

  get countryInvalid(): boolean { return !!this.clientCountry?.invalid && (!!this.clientCountry?.dirty || !!this.clientCountry?.touched); }
  get cityInvalid(): boolean { return !!this.clientCity?.invalid && (!!this.clientCity?.dirty || !!this.clientCity?.touched); }
  get streetInvalid(): boolean { return !!this.clientStreet?.invalid && (!!this.clientStreet?.dirty || !!this.clientStreet?.touched); }
  get houseNumberInvalid(): boolean { return !!this.clientHouseNumber?.invalid && (!!this.clientHouseNumber?.dirty || !!this.clientHouseNumber?.touched); }
  get postalCodeInvalid(): boolean { return !!this.clientPostalCode?.invalid && (!!this.clientPostalCode?.dirty || !!this.clientPostalCode?.touched); }


  // Перевірка на співпадіння паролів
  get passwordEquals(): boolean {
    // Тепер перевіряємо помилку на групі userRegistrationData або конкретно на полі repeatPassword
    return !this.registrationForm.get('userRegistrationData')?.hasError('mismatch') && !this.userRepeatPassword?.hasError('mismatch');
  }

  setErrorMessage(status: number, errorDetails: any): void {
    // Логіка обробки помилок залишається схожою, але можна додати специфічні перевірки для /clients
    let msgText = 'Wystąpił nieoczekiwany błąd. Spróbuj ponownie.';
    let serverMessage = '';

    if (typeof errorDetails === 'string') {
      serverMessage = errorDetails;
    } else if (errorDetails?.message && typeof errorDetails.message === 'string') {
      serverMessage = errorDetails.message;
    } else if (errorDetails?.error?.message && typeof errorDetails.error.message === 'string') {
      serverMessage = errorDetails.error.message; // Частий патерн для Spring помилок
    }

    // Перевіряємо відомі текстові повідомлення
    if (serverMessage) {
      if (serverMessage.includes('Duplicate entry') && serverMessage.includes('users.email')) {
        msgText = 'Użytkownik z takim adresem email już istnieje.';
      } else if (serverMessage.includes('Duplicate entry') && serverMessage.includes('users.phone_number')) {
        msgText = 'Użytkownik z takim numerem telefonu już istnieje.';
      } else if (serverMessage.length < 200) { // Обмеження довжини, щоб не показувати стектрейси
        msgText = serverMessage;
      }
    }

    // Обробка за статус-кодом
    switch (status) {
      case 400:
        // Якщо бекенд повертає деталі валідації (наприклад, Map<String, String>)
        if (errorDetails?.errors && typeof errorDetails.errors === 'object') {
          const errorFields = Object.keys(errorDetails.errors).join(', ');
          msgText = `Błąd walidacji w polach: ${errorFields}. Sprawdź dane.`;
        } else if (!serverMessage) { // Якщо немає специфічного повідомлення, ставимо загальне
          msgText = 'Nieprawidłowe dane. Sprawdź wypełnione pola.';
        }
        break;
      case 409:
        // msgText вже може бути встановлено вище, якщо це дублікат
        if (!msgText.includes('Użytkownik')) { // Якщо не розпізнали як дублікат
          msgText = 'Konflikt danych. Możliwe, że taki użytkownik już istnieje.';
        }
        break;
      case 500:
        msgText = 'Błąd serwera. Spróbuj ponownie później.';
        break;
    }
    this.errorMessage = msgText;
  }
}
