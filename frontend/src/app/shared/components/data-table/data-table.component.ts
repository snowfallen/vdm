import {
  Component, Input, Output, EventEmitter, ContentChildren,
  QueryList, TemplateRef
} from '@angular/core';
import { CommonModule } from '@angular/common';

export interface ITableColumn {
  key: string;
  label: string;
  width?: string;
}

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './data-table.component.html',
  styleUrl: './data-table.component.scss'
})
export class DataTableComponent {
  @Input() columns: ITableColumn[] = [];
  @Input() data: any[] = [];
  @Input() isLoading = false;
  @Input() totalElements = 0;
  @Input() pageSize = 20;
  @Input() currentPage = 0;
  @Input() showActions = true;
  @Input() emptyMessage = 'Немає даних';

  @Output() edit   = new EventEmitter<any>();
  @Output() delete = new EventEmitter<any>();
  @Output() view   = new EventEmitter<any>();
  @Output() pageChange = new EventEmitter<number>();

  get totalPages(): number {
    return Math.ceil(this.totalElements / this.pageSize);
  }

  getValue(row: any, key: string): any {
    return key.split('.').reduce((obj, k) => obj?.[k], row);
  }

  onEdit(row: any): void   { this.edit.emit(row); }
  onDelete(row: any): void { this.delete.emit(row); }
  onView(row: any): void   { this.view.emit(row); }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.pageChange.emit(page);
    }
  }

  get pages(): number[] {
    const total = this.totalPages;
    const current = this.currentPage;
    const range: number[] = [];
    const delta = 2;
    for (let i = Math.max(0, current - delta);
         i <= Math.min(total - 1, current + delta); i++) {
      range.push(i);
    }
    return range;
  }
}
