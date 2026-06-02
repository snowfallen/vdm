import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { DataTableComponent, ITableColumn } from '../../../shared/components/data-table/data-table.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { UserService } from '../../../core/services/user.service';
import { IPage, IUser, IUserUpdateRequest } from '../../../core/models/models';

// Окремий тип для відображення в таблиці
// roleId і emailVerified конвертуємо в string для відображення
interface IUserDisplay extends Omit<IUser, 'roleId' | 'emailVerified'> {
  roleId: string;
  emailVerified: string;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DataTableComponent, ConfirmDialogComponent],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {
  private readonly userService = inject(UserService);
  private readonly fb          = inject(FormBuilder);

  users         = signal<IUserDisplay[]>([]);   // для таблиці
  rawUsers      = signal<IUser[]>([]);           // оригінали для edit/delete
  isLoading     = signal(true);
  modalOpen     = signal(false);
  editingUser   = signal<IUser | null>(null);
  deleteId      = signal<number | null>(null);
  error         = signal('');
  totalElements = signal(0);
  currentPage   = signal(0);
  readonly pageSize = 20;

  readonly columns: ITableColumn[] = [
    { key: 'id',            label: 'ID',          width: '60px' },
    { key: 'firstName',     label: 'Ім\'я',       width: '120px' },
    { key: 'lastName',      label: 'Прізвище',    width: '130px' },
    { key: 'email',         label: 'Email' },
    { key: 'phoneNumber',   label: 'Телефон',     width: '140px' },
    { key: 'roleId',        label: 'Роль',        width: '100px' },
    { key: 'emailVerified', label: 'Email підтв.',width: '100px' },
  ];

  readonly roleLabels: Record<number, string> = {
    1: 'Admin',
    2: 'Client',
    3: 'Accountant',
  };

  form = this.fb.group({
    firstName:   ['', [Validators.required, Validators.minLength(2)]],
    lastName:    ['', [Validators.required, Validators.minLength(2)]],
    email:       ['', [Validators.required, Validators.email]],
    phoneNumber: ['', Validators.required],
  });

  ngOnInit(): void { this.load(); }

  load(page = 0): void {
    this.isLoading.set(true);
    this.userService.getAll(page, this.pageSize).subscribe({
      next: (data: IPage<IUser>) => {
        // Зберігаємо оригінали для edit/delete
        this.rawUsers.set(data.content);

        // Конвертуємо для відображення в таблиці
        const mapped: IUserDisplay[] = data.content.map(u => ({
          ...u,
          roleId:        this.roleLabels[u.roleId] ?? String(u.roleId),
          emailVerified: u.emailVerified ? '✓' : '✗',
        }));

        this.users.set(mapped);
        this.totalElements.set(data.totalElements);
        this.currentPage.set(data.number);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  openEdit(displayUser: IUserDisplay): void {
    // Беремо оригінального юзера по id
    const user = this.rawUsers().find(u => u.id === displayUser.id);
    if (!user) return;

    this.editingUser.set(user);
    this.form.patchValue({
      firstName:   user.firstName,
      lastName:    user.lastName,
      email:       user.email,
      phoneNumber: user.phoneNumber,
    });
    this.error.set('');
    this.modalOpen.set(true);
  }

  closeModal(): void {
    this.modalOpen.set(false);
    this.editingUser.set(null);
    this.form.reset();
  }

  save(): void {
    if (this.form.invalid) return;
    const user = this.editingUser();
    if (!user) return;

    const dto = this.form.value as IUserUpdateRequest;
    this.userService.update(user.id, dto).subscribe({
      next: () => { this.closeModal(); this.load(this.currentPage()); },
      error: () => this.error.set('Помилка збереження.')
    });
  }

  confirmDelete(displayUser: IUserDisplay): void {
    this.deleteId.set(displayUser.id);
  }

  doDelete(): void {
    const id = this.deleteId();
    if (!id) return;
    this.userService.delete(id).subscribe({
      next: () => { this.deleteId.set(null); this.load(this.currentPage()); },
      error: () => this.error.set('Помилка видалення.')
    });
  }
}
