import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="overlay" *ngIf="isOpen" (click)="onCancel()">
      <div class="dialog" (click)="$event.stopPropagation()">
        <div class="dialog__icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="1.5">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
        </div>
        <h3 class="dialog__title">{{ title }}</h3>
        <p class="dialog__text">{{ message }}</p>
        <div class="dialog__actions">
          <button class="btn btn--ghost" (click)="onCancel()">Скасувати</button>
          <button class="btn btn--danger" (click)="onConfirm()">{{ confirmLabel }}</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .overlay {
      position: fixed; inset: 0;
      background: rgba(0,0,0,.5);
      display: flex; align-items: center; justify-content: center;
      z-index: 10000;
      animation: fade-in 150ms ease both;
    }
    .dialog {
      background: var(--clr-surface);
      border-radius: var(--radius-xl);
      padding: var(--space-8);
      max-width: 420px; width: 90%;
      text-align: center;
      box-shadow: var(--shadow-xl);
      animation: fade-in 200ms ease both;
    }
    .dialog__icon {
      color: var(--clr-danger);
      margin-bottom: var(--space-4);
    }
    .dialog__title {
      font-size: var(--fs-lg);
      font-weight: var(--fw-bold);
      color: var(--clr-text-primary);
      margin-bottom: var(--space-2);
    }
    .dialog__text {
      font-size: var(--fs-sm);
      color: var(--clr-text-secondary);
      margin-bottom: var(--space-6);
    }
    .dialog__actions {
      display: flex; gap: var(--space-3); justify-content: center;
    }
    .btn--danger {
      display: inline-flex; align-items: center; justify-content: center;
      padding: var(--space-3) var(--space-6);
      background: var(--clr-danger); color: #fff;
      border: none; border-radius: var(--radius-md);
      font-size: var(--fs-sm); font-weight: var(--fw-semibold);
      cursor: pointer; transition: all var(--transition-fast);
      &:hover { background: #dc2626; }
    }
  `]
})
export class ConfirmDialogComponent {
  @Input() isOpen = false;
  @Input() title = 'Підтвердити видалення';
  @Input() message = 'Ця дія незворотна. Продовжити?';
  @Input() confirmLabel = 'Видалити';

  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  onConfirm(): void { this.confirmed.emit(); }
  onCancel(): void  { this.cancelled.emit(); }
}
