import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ParaApiService } from '../../service/ParaApiService';
import { WordApiService } from '../../service/WordApiService';
import { Para } from '../../model/dto/para.model';
import { Word } from '../../model/dto/word.model';
import { DataTableComponent, TableColumn } from '../shared/DataTableComp';
import { PaginationComponent } from '../shared/PaginationComp';

@Component({
  selector: 'app-para-list',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, PaginationComponent],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>পাড়া তালিকা</h4>
      <button class="btn btn-primary" (click)="goToCreate()">
        <i class="bi bi-plus-lg me-1"></i>নতুন পাড়া
      </button>
    </div>

    <div class="card mb-3">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3">
            <select class="form-select" [(ngModel)]="filterWordId" (change)="loadFiltered()">
              <option value="">সব ওয়ার্ড</option>
              <option *ngFor="let w of allWords" [value]="w.id">{{ w.wordName }}</option>
            </select>
          </div>
          <div class="col-md-4">
            <input type="text" class="form-control" placeholder="সাবডোমেইন..." [(ngModel)]="filterSubdomain" (keyup.enter)="loadFiltered()">
          </div>
          <div class="col-md-2">
            <button class="btn btn-outline-primary" (click)="loadFiltered()">
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
          [data]="paras"
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
export class ParaListComponent implements OnInit {
  paras: Para[] = [];
  allWords: Word[] = [];
  columns: TableColumn[] = [
    { key: 'pbrName', label: 'পাড়ার নাম' },
    { key: 'wordName', label: 'ওয়ার্ড' },
    { key: 'subdomain', label: 'সাবডোমেইন' },
    { key: 'createdBy', label: 'তৈরি করেছেন' }
  ];
  filterWordId = '';
  filterSubdomain = '';
  currentPage = 1;
  totalPages = 1;
  totalItems = 0;
  pageSize = 10;

  constructor(
    private paraService: ParaApiService,
    private wordService: WordApiService,
    private router: Router
  ) {}
  
  ngOnInit() { 
    this.loadWords();
    this.loadAll(); 
  }

  loadWords() {
    this.wordService.getAll().subscribe({
      next: (data) => this.allWords = data
    });
  }

  loadAll() {
    this.paraService.getAll().subscribe({
      next: (data) => {
        this.paras = data;
        this.totalItems = data.length;
        this.totalPages = Math.ceil(data.length / this.pageSize);
      },
      error: (err) => console.error('Error loading paras', err)
    });
  }

  loadFiltered() {
    if (this.filterWordId && this.filterSubdomain) {
      this.paraService.getByWordIdAndSubdomain(+this.filterWordId, this.filterSubdomain).subscribe({
        next: (data) => {
          this.paras = data;
          this.totalItems = data.length;
          this.totalPages = Math.ceil(data.length / this.pageSize);
        }
      });
    } else if (this.filterWordId) {
      this.paraService.getByWordId(+this.filterWordId).subscribe({
        next: (data) => {
          this.paras = data;
          this.totalItems = data.length;
          this.totalPages = Math.ceil(data.length / this.pageSize);
        }
      });
    } else if (this.filterSubdomain) {
      this.paraService.getBySubdomain(this.filterSubdomain).subscribe({
        next: (data) => {
          this.paras = data;
          this.totalItems = data.length;
          this.totalPages = Math.ceil(data.length / this.pageSize);
        }
      });
    } else {
      this.loadAll();
    }
  }

  clearSearch() {
    this.filterWordId = '';
    this.filterSubdomain = '';
    this.loadAll();
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }

  goToCreate() {
    this.router.navigate(['/paras/create']);
  }

  editItem(para: Para) {
    this.router.navigate(['/paras/edit', para.id]);
  }

  deleteItem(para: Para) {
    if (!confirm('আপনি কি এই পাড়া মুছে ফেলতে চান?')) return;
    this.paraService.delete(para.id!).subscribe({
      next: () => this.loadAll(),
      error: (err) => alert(err.error?.message || 'মুছে ফেলায় সমস্যা')
    });
  }
}
