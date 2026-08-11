import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserApiService } from '../../service/UserApiService';
import { User } from '../../model/dto/user.model';
import { DataTableComponent, TableColumn } from '../shared/DataTableComp';
import { PaginationComponent } from '../shared/PaginationComp';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, PaginationComponent],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>ব্যবহারকারী তালিকা</h4>
      <button class="btn btn-primary" (click)="goToCreate()">
        <i class="bi bi-plus-lg me-1"></i>নতুন ব্যবহারকারী
      </button>
    </div>

    <div class="card mb-3">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-4">
            <input type="text" class="form-control" placeholder="ব্যবহারকারী খুঁজুন..." [(ngModel)]="searchName" (keyup.enter)="loadAll()">
          </div>
          <div class="col-md-2">
            <button class="btn btn-outline-primary" (click)="loadAll()">
              <i class="bi bi-search me-1"></i>খুঁজুন
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <app-data-table
          [columns]="columns"
          [data]="users"
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
export class UserListComponent implements OnInit {
  users: User[] = [];
  columns: TableColumn[] = [
    { key: 'name', label: 'নাম' },
    { key: 'username', label: 'ইউজারনাম' },
    { key: 'email', label: 'ইমেইল' },
    { key: 'subdomain', label: 'সাবডোমেইন' },
    { key: 'role', label: 'রোল' }
  ];
  searchName = '';
  currentPage = 1;
  totalPages = 1;
  totalItems = 0;
  pageSize = 10;

  constructor(private service: UserApiService, private router: Router) {}
  
  ngOnInit() { this.loadAll(); }

  loadAll() {
    this.service.getAll().subscribe({
      next: (data) => {
        this.users = data;
        this.totalItems = data.length;
        this.totalPages = Math.ceil(data.length / this.pageSize);
      },
      error: (err) => console.error('Error loading users', err)
    });
  }

  onPageChange(page: number) {
    this.currentPage = page;
  }

  goToCreate() {
    this.router.navigate(['/users/create']);
  }

  editItem(user: User) {
    this.router.navigate(['/users/edit', user.id]);
  }

  deleteItem(user: User) {
    if (!confirm('আপনি কি এই ব্যবহারকারী মুছে ফেলতে চান?')) return;
    this.service.delete(user.id!).subscribe({
      next: () => this.loadAll(),
      error: (err) => alert(err.error?.message || 'মুছে ফেলায় সমস্যা')
    });
  }
}
