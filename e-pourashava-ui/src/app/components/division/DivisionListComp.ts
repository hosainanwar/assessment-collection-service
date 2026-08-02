import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DivisionApiService } from '../../service/DivisionApiService';
import { Division } from '../../model/dto/division.model';
import { DataTableComponent, TableColumn } from '../shared/DataTableComp';
import { PaginationComponent } from '../shared/PaginationComp';

@Component({
  selector: 'app-division-list',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, PaginationComponent],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>বিভাগ তালিকা</h4>
      <button class="btn btn-primary" (click)="goToCreate()">
        <i class="bi bi-plus-lg me-1"></i>নতুন বিভাগ
      </button>
    </div>

    <div class="card mb-3">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-4">
            <input type="text" class="form-control" placeholder="বিভাগ খুঁজুন..." [(ngModel)]="searchName" (keyup.enter)="search()">
          </div>
          <div class="col-md-2">
            <button class="btn btn-outline-primary" (click)="search()">
              <i class="bi bi-search me-1"></i>খুঁজুন
            </button>
          </div>
          <div class="col-md-2">
            <button class="btn btn-outline-secondary" (click)="clearSearch()">
              <i class="bi bi-x-lg me-1"></i>মুছুন
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <app-data-table
          [columns]="columns"
          [data]="divisions"
          [showActions]="true"
          (onEdit)="editItem($event)"
          (onDelete)="deleteItem($event)">
        </app-data-table>

        <app-pagination
          [currentPage]="currentPage"
          [totalPages]="totalPages"
          [totalItems]="totalItems"
          [pageSize]="pageSize"
          (pageChange)="onPageChange($event)">
        </app-pagination>
      </div>
    </div>
  `
})
export class DivisionListComponent implements OnInit {
  divisions: Division[] = [];
  columns: TableColumn[] = [
    { key: 'name', label: 'বিভাগের নাম' }
  ];
  searchName = '';
  currentPage = 1;
  totalPages = 1;
  totalItems = 0;
  pageSize = 10;

  constructor(private service: DivisionApiService, private router: Router) {}
  
  ngOnInit() { this.loadAll(); }

  loadAll() {
    this.service.getAll().subscribe({
      next: (data) => {
        this.divisions = data;
        this.totalItems = data.length;
        this.totalPages = Math.ceil(data.length / this.pageSize);
      },
      error: (err) => console.error('Error loading divisions', err)
    });
  }

  search() {
    if (this.searchName.trim()) {
      this.service.search(this.searchName).subscribe({
        next: (data) => {
          this.divisions = data;
          this.totalItems = data.length;
          this.totalPages = Math.ceil(data.length / this.pageSize);
        }
      });
    } else {
      this.loadAll();
    }
  }

  clearSearch() {
    this.searchName = '';
    this.loadAll();
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }

  goToCreate() {
    this.router.navigate(['/divisions/create']);
  }

  editItem(division: Division) {
    this.router.navigate(['/divisions/edit', division.id]);
  }

  deleteItem(division: Division) {
    if (!confirm('আপনি কি এই বিভাগ মুছে ফেলতে চান?')) return;
    this.service.delete(division.id!).subscribe({
      next: () => {
        this.loadAll();
      },
      error: (err) => alert(err.error?.message || 'মুছে ফেলায় সমস্যা')
    });
  }
}
