import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { WordApiService } from '../../service/WordApiService';
import { Word } from '../../model/dto/word.model';
import { DataTableComponent, TableColumn } from '../shared/DataTableComp';
import { PaginationComponent } from '../shared/PaginationComp';

@Component({
  selector: 'app-word-list',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, PaginationComponent],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>ওয়ার্ড তালিকা</h4>
      <button class="btn btn-primary" (click)="goToCreate()">
        <i class="bi bi-plus-lg me-1"></i>নতুন ওয়ার্ড
      </button>
    </div>

    <div class="card mb-3">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-4">
            <input type="text" class="form-control" placeholder="সাবডোমেইন দিয়ে খুঁজুন..." [(ngModel)]="searchSubdomain" (keyup.enter)="loadBySubdomain()">
          </div>
          <div class="col-md-2">
            <button class="btn btn-outline-primary" (click)="loadBySubdomain()">
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
          [data]="words"
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
export class WordListComponent implements OnInit {
  words: Word[] = [];
  columns: TableColumn[] = [
    { key: 'wordName', label: 'ওয়ার্ডের নাম' },
    { key: 'subdomain', label: 'সাবডোমেইন' },
    { key: 'createdBy', label: 'তৈরি করেছেন' }
  ];
  searchSubdomain = '';
  currentPage = 1;
  totalPages = 1;
  totalItems = 0;
  pageSize = 10;

  constructor(private service: WordApiService, private router: Router) {}
  
  ngOnInit() { this.loadAll(); }

  loadAll() {
    this.service.getAll().subscribe({
      next: (data) => {
        this.words = data;
        this.totalItems = data.length;
        this.totalPages = Math.ceil(data.length / this.pageSize);
      },
      error: (err) => console.error('Error loading words', err)
    });
  }

  loadBySubdomain() {
    if (this.searchSubdomain.trim()) {
      this.service.getBySubdomain(this.searchSubdomain).subscribe({
        next: (data) => {
          this.words = data;
          this.totalItems = data.length;
          this.totalPages = Math.ceil(data.length / this.pageSize);
        }
      });
    } else {
      this.loadAll();
    }
  }

  clearSearch() {
    this.searchSubdomain = '';
    this.loadAll();
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }

  goToCreate() {
    this.router.navigate(['/words/create']);
  }

  editItem(word: Word) {
    this.router.navigate(['/words/edit', word.id]);
  }

  deleteItem(word: Word) {
    if (!confirm('আপনি কি এই ওয়ার্ড মুছে ফেলতে চান?')) return;
    this.service.delete(word.id!).subscribe({
      next: () => this.loadAll(),
      error: (err) => alert(err.error?.message || 'মুছে ফেলায় সমস্যা')
    });
  }
}
