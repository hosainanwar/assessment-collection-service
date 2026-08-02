import { Component, Input, Output, EventEmitter, ContentChild, TemplateRef } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface TableColumn {
  key: string;
  label: string;
  width?: string;
  sortable?: boolean;
}

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="table-responsive">
      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr>
            <th *ngFor="let col of columns" [style.width]="col.width">{{ col.label }}</th>
            <th *ngIf="showActions" class="text-center" style="width: 150px">কার্যক্রম</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let row of data; let i = index">
            <td *ngFor="let col of columns">{{ row[col.key] || '-' }}</td>
            <td *ngIf="showActions" class="text-center">
              <button class="btn btn-sm btn-warning me-1" (click)="onEdit.emit(row)">
                <i class="bi bi-pencil"></i> সম্পাদনা
              </button>
              <button class="btn btn-sm btn-danger" (click)="onDelete.emit(row)">
                <i class="bi bi-trash"></i> মুছুন
              </button>
            </td>
          </tr>
          <tr *ngIf="data.length === 0">
            <td [attr.colspan]="columns.length + (showActions ? 1 : 0)" class="text-center text-muted py-4">
              {{ emptyMessage }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `
})
export class DataTableComponent {
  @Input() columns: TableColumn[] = [];
  @Input() data: any[] = [];
  @Input() showActions = true;
  @Input() emptyMessage = 'কোনো ডেটা পাওয়া যায়নি';
  @Output() onEdit = new EventEmitter<any>();
  @Output() onDelete = new EventEmitter<any>();
}
