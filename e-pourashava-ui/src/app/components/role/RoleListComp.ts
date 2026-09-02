import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RoleApiService } from '../../service/RoleApiService';
import { Role } from '../../model/dto/role.model';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-role-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>রোল তালিকা</h4>
      <button class="btn btn-primary" *ngIf="canCreate" (click)="goToCreate()">
        <i class="bi bi-plus-lg me-1"></i>নতুন রোল
      </button>
    </div>

    <div class="card">
      <div class="card-body p-0">
        <table class="table table-hover mb-0">
          <thead>
            <tr>
              <th>কোড</th>
              <th>নাম</th>
              <th>সিস্টেম</th>
              <th>স্ট্যাটাস</th>
              <th>পারমিশন</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let role of roles">
              <td><code>{{ role.code }}</code></td>
              <td>{{ role.nameBn }} <small class="text-muted">({{ role.nameEn }})</small></td>
              <td>{{ role.isSystem ? 'হ্যাঁ' : 'না' }}</td>
              <td>{{ role.status ? 'সক্রিয়' : 'নিষ্ক্রিয়' }}</td>
              <td>{{ role.permissions?.length || 0 }}</td>
              <td class="text-end">
                <button class="btn btn-sm btn-outline-primary me-1" *ngIf="canUpdate" (click)="edit(role)">সম্পাদনা</button>
                <button class="btn btn-sm btn-outline-danger" *ngIf="canDelete && !role.isSystem" (click)="remove(role)">মুছুন</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class RoleListComponent implements OnInit {
  roles: Role[] = [];
  canCreate = false;
  canUpdate = false;
  canDelete = false;

  constructor(
    private service: RoleApiService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.canCreate = this.auth.hasPermission('ROLE:CREATE');
    this.canUpdate = this.auth.hasPermission('ROLE:UPDATE');
    this.canDelete = this.auth.hasPermission('ROLE:DELETE');
    this.service.getAll().subscribe({ next: data => this.roles = data });
  }

  goToCreate() { this.router.navigate(['/roles/create']); }
  edit(role: Role) { this.router.navigate(['/roles/edit', role.id]); }
  remove(role: Role) {
    if (!role.id || !confirm('এই রোল মুছে ফেলবেন?')) {
      return;
    }
    this.service.delete(role.id).subscribe({ next: () => this.ngOnInit() });
  }
}
