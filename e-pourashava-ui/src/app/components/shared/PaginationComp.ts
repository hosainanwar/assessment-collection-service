import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface PaginationConfig {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  pageSize: number;
}

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mt-3" *ngIf="totalItems > 0">
      <div class="text-muted small">
        মোট: {{ totalItems }} টি | পৃষ্ঠা {{ currentPage }} / {{ totalPages }}
      </div>
      <nav>
        <ul class="pagination pagination-sm mb-0">
          <li class="page-item" [class.disabled]="currentPage === 1">
            <button class="page-link" (click)="onPageChange(currentPage - 1)" [disabled]="currentPage === 1">
              <i class="bi bi-chevron-left"></i>
            </button>
          </li>
          <li class="page-item" *ngFor="let page of visiblePages" [class.active]="page === currentPage">
            <button class="page-link" (click)="onPageChange(page)">{{ page }}</button>
          </li>
          <li class="page-item" [class.disabled]="currentPage === totalPages">
            <button class="page-link" (click)="onPageChange(currentPage + 1)" [disabled]="currentPage === totalPages">
              <i class="bi bi-chevron-right"></i>
            </button>
          </li>
        </ul>
      </nav>
    </div>
  `
})
export class PaginationComponent {
  @Input() currentPage = 1;
  @Input() totalPages = 1;
  @Input() totalItems = 0;
  @Input() pageSize = 10;
  @Output() pageChange = new EventEmitter<number>();

  get visiblePages(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;
    let start = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(this.totalPages, start + maxVisible - 1);
    
    if (end - start + 1 < maxVisible) {
      start = Math.max(1, end - maxVisible + 1);
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }

  onPageChange(page: number) {
    if (page >= 1 && page <= this.totalPages && page !== this.currentPage) {
      this.pageChange.emit(page);
    }
  }
}
