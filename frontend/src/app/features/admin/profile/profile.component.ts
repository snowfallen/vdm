import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import {UserService} from '../../../core/services/user.service';
import {AuthService} from '../../../core/auth/services/auth.service';
import {IUser, IUserUpdatePasswordRequest, IUserUpdateRequest} from '../../../core/models/models';
import {Roles} from '../../../core/auth/enums/roles';


@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  private readonly fb          = inject(FormBuilder);

  user      = signal<IUser | null>(null);
  isLoading = signal(true);
  saveOk    = signal(false);
  pwOk      = signal(false);
  error     = signal('');
  pwError   = signal('');

  isAdmin = signal(this.authService.getRoleId() === Roles.ADMIN);

  infoForm = this.fb.group({
    firstName:   ['', [Validators.required, Validators.minLength(2)]],
    lastName:    ['', [Validators.required, Validators.minLength(2)]],
    email:       ['', [Validators.required, Validators.email]],
    phoneNumber: ['', Validators.required],
  });

  pwForm = this.fb.group({
    password:       ['', [Validators.required, Validators.minLength(8)]],
    repeatPassword: ['', Validators.required],
  });

  ngOnInit(): void {
    this.userService.getPersonalInfo().subscribe({
      next: (u) => {
        this.user.set(u);
        this.infoForm.patchValue({
          firstName:   u.firstName,
          lastName:    u.lastName,
          email:       u.email,
          phoneNumber: u.phoneNumber,
        });
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  saveInfo(): void {
    if (this.infoForm.invalid) return;
    const dto = this.infoForm.value as IUserUpdateRequest;
    this.userService.updateOwnData(dto).subscribe({
      next: (u) => {
        this.user.set(u);
        this.saveOk.set(true);
        this.error.set('');
        setTimeout(() => this.saveOk.set(false), 3000);
      },
      error: () => this.error.set('Помилка збереження.')
    });
  }

  savePassword(): void {
    if (this.pwForm.invalid) return;
    const v = this.pwForm.value;
    if (v.password !== v.repeatPassword) {
      this.pwError.set('Паролі не збігаються.');
      return;
    }
    const dto = this.pwForm.value as IUserUpdatePasswordRequest;
    this.userService.updateOwnPassword(dto).subscribe({
      next: () => {
        this.pwOk.set(true);
        this.pwError.set('');
        this.pwForm.reset();
        setTimeout(() => this.pwOk.set(false), 3000);
      },
      error: () => this.pwError.set('Помилка зміни пароля.')
    });
  }

  getRoleLabel(roleId: number): string {
    const map: Record<number, string> = {
      1: 'Адміністратор',
      2: 'Клієнт',
      3: 'Бухгалтер',
    };
    return map[roleId] ?? 'Невідома роль';
  }
}
