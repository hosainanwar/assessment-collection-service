import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PourashavaApiService } from '../../service/PourashavaApiService';
import { DivisionApiService } from '../../service/DivisionApiService';
import { DistrictApiService } from '../../service/DistrictApiService';
import { Pourashava } from '../../model/dto/pourashava.model';
import { Division } from '../../model/dto/division.model';
import { District } from '../../model/dto/district.model';
import { DataTableComponent, TableColumn } from '../shared/DataTableComp';
import { PaginationComponent } from '../shared/PaginationComp';

@Component({
  selector: 'app-pourashava-list',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, PaginationComponent],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>পৌরসভা তালিকা</h4>
      <button class="btn btn-primary" (click)="goToCreate()">
        <i class="bi bi-plus-lg me-1"></i>নতুন পৌরসভা
      </button>
    </div>

    <div class="card mb-3">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3">
            <select class="form-select" [(ngModel)]="filterDivisionId" (change)="onDivisionFilterChange()">
              <option value="">সব বিভাগ</option>
              <option *ngFor="let d of allDivisions" [value]="d.id">{{ d.name }}</option>
            </select>
          </div>
          <div class="col-md-3">
            <select class="form-select" [(ngModel)]="filterDistrictId" (change)="loadFiltered()">
              <option value="">সব জেলা</option>
              <option *ngFor="let d of filteredDistricts" [value]="d.id">{{ d.name }}</option>
            </select>
          </div>
          <div class="col-md-3">
            <input type="text" class="form-control" placeholder="পৌরসভা খুঁজুন..." [(ngModel)]="searchName" (keyup.enter)="loadFiltered()">
          </div>
          <div class="col-md-2">
            <button class="btn btn-outline-primary" (click)="loadFiltered()">
              <i class="bi bi-search me-1"></i>খুঁজুন
            </button>
          </div>
          <div class="col-md-1">
            <button class="btn btn-outline-secondary" (click)="clearSearch()">
              <i class="bi bi-x-lg"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <app-data-table
          [columns]="columns"
          [data]="pourashavas"
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
export class PourashavaListComponent implements OnInit {
  pourashavas: Pourashava[] = [];
  allDivisions: Division[] = [];
  allDistricts: District[] = [];
  filteredDistricts: District[] = [];
  columns: TableColumn[] = [
    { key: 'bnName', label: 'বাংলা নাম' },
    { key: 'enName', label: 'ইংরেজি নাম' },
    { key: 'subdomain', label: 'সাবডোমেইন' },
    { key: 'divisionName', label: 'বিভাগ' },
    { key: 'districtName', label: 'জেলা' }
  ];
  filterDivisionId = '';
  filterDistrictId = '';
  searchName = '';
  currentPage = 1;
  totalPages = 1;
  totalItems = 0;
  pageSize = 10;

  constructor(
    private service: PourashavaApiService,
    private divisionService: DivisionApiService,
    private districtService: DistrictApiService,
    private router: Router
  ) {}
  
  ngOnInit() { 
    this.loadDivisions();
    this.loadDistricts();
    this.loadAll(); 
  }

  loadDivisions() {
    this.divisionService.getAll().subscribe({
      next: (data) => this.allDivisions = data
    });
  }

  loadDistricts() {
    this.districtService.getAll().subscribe({
      next: (data) => this.allDistricts = data
    });
  }

  loadAll() {
    this.service.getAll().subscribe({
      next: (data) => {
        this.pourashavas = data;
        this.totalItems = data.length;
        this.totalPages = Math.ceil(data.length / this.pageSize);
      },
      error: (err) => console.error('Error loading pourashavas', err)
    });
  }

  onDivisionFilterChange() {
    if (this.filterDivisionId) {
      this.filteredDistricts = this.allDistricts.filter(d => d.divisionId === +this.filterDivisionId);
    } else {
      this.filteredDistricts = this.allDistricts;
    }
    this.filterDistrictId = '';
    this.loadFiltered();
  }

  loadFiltered() {
    if (this.filterDistrictId) {
      this.service.getByDistrictId(+this.filterDistrictId).subscribe({
        next: (data) => {
          this.pourashavas = data;
          this.totalItems = data.length;
          this.totalPages = Math.ceil(data.length / this.pageSize);
        }
      });
    } else if (this.filterDivisionId) {
      this.service.getByDivisionId(+this.filterDivisionId).subscribe({
        next: (data) => {
          this.pourashavas = data;
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
    this.filterDistrictId = '';
    this.searchName = '';
    this.loadAll();
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }

  goToCreate() {
    this.router.navigate(['/pourashavas/create']);
  }

  editItem(pourashava: Pourashava) {
    this.router.navigate(['/pourashavas/edit', pourashava.id]);
  }

  deleteItem(pourashava: Pourashava) {
    if (!confirm('আপনি কি এই পৌরসভা মুছে ফেলতে চান?')) return;
    this.service.delete(pourashava.id!).subscribe({
      next: () => this.loadAll(),
      error: (err) => alert(err.error?.message || 'মুছে ফেলায় সমস্যা')
    });
  }
}
