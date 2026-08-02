import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DistrictApiService } from '../../service/DistrictApiService';
import { DivisionApiService } from '../../service/DivisionApiService';
import { District } from '../../model/dto/district.model';
import { Division } from '../../model/dto/division.model';
import { DataTableComponent, TableColumn } from '../shared/DataTableComp';
import { PaginationComponent } from '../shared/PaginationComp';

@Component({
  selector: 'app-district-list',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, PaginationComponent],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>জেলা তালিকা</h4>
      <button class="btn btn-primary" (click)="goToCreate()">
        <i class="bi bi-plus-lg me-1"></i>নতুন জেলা
      </button>
    </div>

    <div class="card mb-3">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3">
            <select class="form-select" [(ngModel)]="filterDivisionId" (change)="loadFiltered()">
              <option value="">সব বিভাগ</option>
              <option *ngFor="let d of allDivisions" [value]="d.id">{{ d.name }}</option>
            </select>
          </div>
          <div class="col-md-4">
            <input type="text" class="form-control" placeholder="জেলা খুঁজুন..." [(ngModel)]="searchName" (keyup.enter)="loadFiltered()">
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
          [data]="districts"
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
export class DistrictListComponent implements OnInit {
  districts: District[] = [];
  allDivisions: Division[] = [];
  columns: TableColumn[] = [
    { key: 'name', label: 'জেলার নাম' },
    { key: 'enName', label: 'ইংরেজি নাম' },
    { key: 'divisionName', label: 'বিভাগ' }
  ];
  filterDivisionId = '';
  searchName = '';
  currentPage = 1;
  totalPages = 1;
  totalItems = 0;
  pageSize = 10;

  constructor(
    private service: DistrictApiService,
    private divisionService: DivisionApiService,
    private router: Router
  ) {}
  
  ngOnInit() { 
    this.loadDivisions();
    this.loadAll(); 
  }

  loadDivisions() {
    this.divisionService.getAll().subscribe({
      next: (data) => this.allDivisions = data
    });
  }

  loadAll() {
    this.service.getAll().subscribe({
      next: (data) => {
        this.districts = data;
        this.totalItems = data.length;
        this.totalPages = Math.ceil(data.length / this.pageSize);
      },
      error: (err) => console.error('Error loading districts', err)
    });
  }

  loadFiltered() {
    if (this.filterDivisionId) {
      this.service.getByDivisionId(+this.filterDivisionId).subscribe({
        next: (data) => {
          this.districts = data;
          this.totalItems = data.length;
          this.totalPages = Math.ceil(data.length / this.pageSize);
        }
      });
    } else {
      this.loadAll();
    }
  }

  clearSearch() {
    this.filterDivisionId = '';
    this.searchName = '';
    this.loadAll();
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }

  goToCreate() {
    this.router.navigate(['/districts/create']);
  }

  editItem(district: District) {
    this.router.navigate(['/districts/edit', district.id]);
  }

  deleteItem(district: District) {
    if (!confirm('আপনি কি এই জেলা মুছে ফেলতে চান?')) return;
    this.service.delete(district.id!).subscribe({
      next: () => this.loadAll(),
      error: (err) => alert(err.error?.message || 'মুছে ফেলায় সমস্যা')
    });
  }
}
